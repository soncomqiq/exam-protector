package com.sonter.exam_protector.service;

import com.sonter.exam_protector.model.*;
import com.sonter.exam_protector.model.enums.SubmissionStatus;
import com.sonter.exam_protector.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final ExamSubmissionRepository submissionRepo;
    private final ExamRepository examRepo;
    private final QuestionRepository questionRepo;
    private final StudentAnswerRepository answerRepo;

    @Transactional
    public ExamSubmission startExam(Long examId, Long userId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        Instant now = Instant.now();
        Instant startTime = exam.getStartTime().atZone(ZoneId.systemDefault()).toInstant();
        Instant endTime = exam.getEndTime().atZone(ZoneId.systemDefault()).toInstant();

        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            throw new RuntimeException("Outside exam window");
        }

        // Check if already started
        return submissionRepo.findByExamIdAndUserId(examId, userId)
                .orElseGet(() -> {
                    ExamSubmission sub = new ExamSubmission();
                    sub.setExamId(examId);
                    sub.setUserId(userId);
                    sub.setStartedAt(LocalDateTime.now());
                    sub.setStatus(SubmissionStatus.IN_PROGRESS);
                    return submissionRepo.save(sub);
                });
    }

    @Transactional
    public void submitExam(Long submissionId) {
        ExamSubmission sub = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (sub.getStatus() != SubmissionStatus.IN_PROGRESS) {
            throw new RuntimeException("Exam already submitted or locked");
        }

        sub.setSubmittedAt(LocalDateTime.now());
        sub.setStatus(SubmissionStatus.SUBMITTED);
        // sub.setScore(calculateScore(sub));
        submissionRepo.save(sub);
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void autoSubmitExpired() {
        List<ExamSubmission> active = submissionRepo.findByStatus(SubmissionStatus.IN_PROGRESS);
        for (ExamSubmission sub : active) {
            Exam exam = examRepo.findById(sub.getExamId()).orElseThrow();
            long elapsed = Duration.between(sub.getStartedAt(), LocalDateTime.now()).toMinutes();
            if (elapsed >= exam.getDurationMinutes()) {
                sub.setStatus(SubmissionStatus.AUTO_SUBMITTED);
                sub.setSubmittedAt(LocalDateTime.now());
                submissionRepo.save(sub);
            }
        }
    }
}
