package com.sonter.exam_protector.service;

import com.sonter.exam_protector.dto.request.AnswerRequest;
import com.sonter.exam_protector.dto.response.QuestionResponse;
import com.sonter.exam_protector.dto.response.SubmissionResponse;
import com.sonter.exam_protector.model.*;
import com.sonter.exam_protector.model.enums.SubmissionStatus;
import com.sonter.exam_protector.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final ExamSubmissionRepository submissionRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @Transactional
    public SubmissionResponse startExam(Long examId, String email, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));

        // Check if exam is within time window
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new IllegalStateException("Exam is not available at this time");
        }

        // Check for existing submission
        var existing = submissionRepository.findByExamIdAndUserId(examId, user.getId());
        if (existing.isPresent()) {
            ExamSubmission sub = existing.get();
            if (sub.getStatus() == SubmissionStatus.IN_PROGRESS) {
                return toSubmissionResponse(sub, exam);
            }
            throw new IllegalStateException("You have already completed this exam");
        }

        ExamSubmission submission = ExamSubmission.builder()
                .exam(exam)
                .user(user)
                .status(SubmissionStatus.IN_PROGRESS)
                .startedAt(now)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        submission = submissionRepository.save(submission);
        return toSubmissionResponse(submission, exam);
    }

    @Transactional
    public SubmissionResponse submitExam(Long examId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ExamSubmission submission = submissionRepository.findByExamIdAndUserId(examId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (submission.getStatus() != SubmissionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Exam already submitted");
        }

        Exam exam = submission.getExam();
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setScore(calculateScore(submission));

        submissionRepository.save(submission);
        return toSubmissionResponse(submission, exam);
    }

    @Transactional
    public void saveAnswer(Long submissionId, AnswerRequest request, String email) {
        ExamSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (!submission.getUser().getEmail().equals(email)) {
            throw new SecurityException("Not your submission");
        }
        if (submission.getStatus() != SubmissionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot modify a submitted exam");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        StudentAnswer answer = studentAnswerRepository
                .findBySubmissionIdAndQuestionId(submissionId, request.getQuestionId())
                .orElse(StudentAnswer.builder()
                        .submission(submission)
                        .question(question)
                        .build());

        if (request.getSelectedOptionId() != null) {
            QuestionOption option = questionOptionRepository.findById(request.getSelectedOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("Option not found"));
            answer.setSelectedOption(option);
        }
        answer.setAnswerText(request.getAnswerText());

        studentAnswerRepository.save(answer);
    }

    public List<SubmissionResponse> getSubmissionsByExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        return submissionRepository.findByExamId(examId).stream()
                .map(sub -> toSubmissionResponse(sub, exam))
                .toList();
    }

    public List<SubmissionResponse> getSubmissionsByStudent(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return submissionRepository.findByUserId(user.getId()).stream()
                .map(sub -> toSubmissionResponse(sub, sub.getExam()))
                .toList();
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void autoSubmitExpired() {
        List<ExamSubmission> active = submissionRepository.findByStatus(SubmissionStatus.IN_PROGRESS);
        for (ExamSubmission sub : active) {
            Exam exam = sub.getExam();
            long elapsed = Duration.between(sub.getStartedAt(), LocalDateTime.now()).toMinutes();
            if (elapsed >= exam.getDurationMinutes()) {
                sub.setStatus(SubmissionStatus.AUTO_SUBMITTED);
                sub.setSubmittedAt(LocalDateTime.now());
                sub.setScore(calculateScore(sub));
                submissionRepository.save(sub);
            }
        }
    }

    private BigDecimal calculateScore(ExamSubmission submission) {
        List<StudentAnswer> answers = studentAnswerRepository.findBySubmissionId(submission.getId());
        List<Question> questions = questionRepository.findByExamIdOrderBySortOrder(submission.getExam().getId());

        int totalPoints = questions.stream().mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1).sum();
        int earnedPoints = 0;

        for (StudentAnswer answer : answers) {
            if (answer.getSelectedOption() != null && Boolean.TRUE.equals(answer.getSelectedOption().getIsCorrect())) {
                Question q = answer.getQuestion();
                earnedPoints += (q.getPoints() != null ? q.getPoints() : 1);
            }
        }

        if (totalPoints == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(earnedPoints * 100.0 / totalPoints).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private SubmissionResponse toSubmissionResponse(ExamSubmission sub, Exam exam) {
        List<QuestionResponse> questions = questionRepository.findByExamIdOrderBySortOrder(exam.getId()).stream()
                .map(q -> {
                    List<QuestionResponse.OptionResponse> options =
                            questionOptionRepository.findByQuestionIdOrderBySortOrder(q.getId()).stream()
                                    .map(opt -> QuestionResponse.OptionResponse.builder()
                                            .id(opt.getId())
                                            .optionText(opt.getOptionText())
                                            .sortOrder(opt.getSortOrder())
                                            .build())
                                    .toList();
                    return QuestionResponse.builder()
                            .id(q.getId())
                            .questionText(q.getQuestionText())
                            .questionType(q.getQuestionType())
                            .points(q.getPoints())
                            .sortOrder(q.getSortOrder())
                            .options(options)
                            .build();
                })
                .toList();

        return SubmissionResponse.builder()
                .id(sub.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .studentName(sub.getUser().getFullName())
                .studentEmail(sub.getUser().getEmail())
                .status(sub.getStatus())
                .startedAt(sub.getStartedAt())
                .submittedAt(sub.getSubmittedAt())
                .score(sub.getScore())
                .questions(questions)
                .build();
    }
}
