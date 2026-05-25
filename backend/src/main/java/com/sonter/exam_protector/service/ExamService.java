package com.sonter.exam_protector.service;

import com.sonter.exam_protector.dto.request.ExamRequest;
import com.sonter.exam_protector.dto.request.QuestionRequest;
import com.sonter.exam_protector.dto.response.ExamResponse;
import com.sonter.exam_protector.dto.response.QuestionResponse;
import com.sonter.exam_protector.model.*;
import com.sonter.exam_protector.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final UserRepository userRepository;

    public List<ExamResponse> getExamsByTeacher(String email) {
        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return examRepository.findByCreatedBy(teacher).stream()
                .map(this::toExamResponse)
                .toList();
    }

    public List<ExamResponse> getAvailableExams() {
        return examRepository.findByIsPublishedTrue().stream()
                .map(this::toExamResponse)
                .toList();
    }

    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        return toExamResponse(exam);
    }

    @Transactional
    public ExamResponse createExam(ExamRequest request, String email) {
        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Exam exam = Exam.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(teacher)
                .durationMinutes(request.getDurationMinutes())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxTabViolations(request.getMaxTabViolations())
                .screenShareRequired(request.getScreenShareRequired())
                .gracePeriodSeconds(request.getGracePeriodSeconds())
                .isPublished(request.getIsPublished())
                .build();

        exam = examRepository.save(exam);
        return toExamResponse(exam);
    }

    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request, String email) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));

        if (!exam.getCreatedBy().getEmail().equals(email)) {
            throw new SecurityException("Not authorized to update this exam");
        }

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setMaxTabViolations(request.getMaxTabViolations());
        exam.setScreenShareRequired(request.getScreenShareRequired());
        exam.setGracePeriodSeconds(request.getGracePeriodSeconds());
        exam.setIsPublished(request.getIsPublished());

        exam = examRepository.save(exam);
        return toExamResponse(exam);
    }

    @Transactional
    public void deleteExam(Long id, String email) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));

        if (!exam.getCreatedBy().getEmail().equals(email)) {
            throw new SecurityException("Not authorized to delete this exam");
        }

        examRepository.delete(exam);
    }

    @Transactional
    public QuestionResponse addQuestion(Long examId, QuestionRequest request, String email) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found"));

        if (!exam.getCreatedBy().getEmail().equals(email)) {
            throw new SecurityException("Not authorized to modify this exam");
        }

        Question question = Question.builder()
                .exam(exam)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .points(request.getPoints())
                .sortOrder(request.getSortOrder())
                .build();

        question = questionRepository.save(question);

        if (request.getOptions() != null) {
            for (QuestionRequest.OptionRequest optReq : request.getOptions()) {
                QuestionOption option = QuestionOption.builder()
                        .question(question)
                        .optionText(optReq.getOptionText())
                        .isCorrect(optReq.getIsCorrect())
                        .sortOrder(optReq.getSortOrder())
                        .build();
                questionOptionRepository.save(option);
            }
        }

        return toQuestionResponse(question);
    }

    public List<QuestionResponse> getQuestions(Long examId) {
        return questionRepository.findByExamIdOrderBySortOrder(examId).stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    private ExamResponse toExamResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .createdByName(exam.getCreatedBy().getFullName())
                .durationMinutes(exam.getDurationMinutes())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .maxTabViolations(exam.getMaxTabViolations())
                .screenShareRequired(exam.getScreenShareRequired())
                .gracePeriodSeconds(exam.getGracePeriodSeconds())
                .isPublished(exam.getIsPublished())
                .createdAt(exam.getCreatedAt())
                .build();
    }

    private QuestionResponse toQuestionResponse(Question question) {
        List<QuestionResponse.OptionResponse> options =
                questionOptionRepository.findByQuestionIdOrderBySortOrder(question.getId()).stream()
                        .map(opt -> QuestionResponse.OptionResponse.builder()
                                .id(opt.getId())
                                .optionText(opt.getOptionText())
                                .sortOrder(opt.getSortOrder())
                                .build())
                        .toList();

        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .points(question.getPoints())
                .sortOrder(question.getSortOrder())
                .options(options)
                .build();
    }
}
