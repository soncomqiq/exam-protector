package com.sonter.exam_protector.service;

import com.sonter.exam_protector.dto.request.ExamRequest;
import com.sonter.exam_protector.dto.request.QuestionRequest;
import com.sonter.exam_protector.model.Exam;
import com.sonter.exam_protector.model.Question;
import com.sonter.exam_protector.model.QuestionOption;
import com.sonter.exam_protector.model.User;
import com.sonter.exam_protector.repository.ExamRepository;
import com.sonter.exam_protector.repository.QuestionOptionRepository;
import com.sonter.exam_protector.repository.QuestionRepository;
import com.sonter.exam_protector.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
    
    public Exam getExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }

    @Transactional
    public Exam createExam(ExamRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Exam exam = Exam.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxTabViolations(request.getMaxTabViolations() != null ? request.getMaxTabViolations() : 3)
                .screenShareRequired(request.getScreenShareRequired() != null ? request.getScreenShareRequired() : true)
                .gracePeriodSeconds(request.getGracePeriodSeconds() != null ? request.getGracePeriodSeconds() : 30)
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .createdBy(user)
                .build();

        return examRepository.save(exam);
    }

    @Transactional
    public Exam updateExam(Long id, ExamRequest request, Long userId) {
        Exam exam = getExamById(id);
        if (!exam.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this exam");
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

        return examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Long id, Long userId) {
        Exam exam = getExamById(id);
        if (!exam.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this exam");
        }
        examRepository.delete(exam);
    }

    @Transactional
    public Question addQuestion(Long examId, QuestionRequest request, Long userId) {
        Exam exam = getExamById(examId);
        if (!exam.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to modify this exam");
        }

        Question question = new Question();
        question.setExam(exam);
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setPoints(request.getPoints() != null ? request.getPoints() : 1);
        question.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        Question savedQuestion = questionRepository.save(question);

        if (request.getOptions() != null) {
            for (QuestionRequest.OptionRequest optionRequest : request.getOptions()) {
                QuestionOption option = new QuestionOption();
                option.setQuestion(savedQuestion);
                option.setOptionText(optionRequest.getOptionText());
                option.setIsCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false);
                option.setSortOrder(optionRequest.getSortOrder() != null ? optionRequest.getSortOrder() : 0);
                questionOptionRepository.save(option);
            }
        }

        return savedQuestion;
    }
}
