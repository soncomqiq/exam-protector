package com.sonter.exam_protector.service;

import com.sonter.exam_protector.dto.request.ViolationRequest;
import com.sonter.exam_protector.model.ExamSubmission;
import com.sonter.exam_protector.model.User;
import com.sonter.exam_protector.model.ViolationLog;
import com.sonter.exam_protector.model.enums.SubmissionStatus;
import com.sonter.exam_protector.model.enums.ViolationSeverity;
import com.sonter.exam_protector.model.enums.ViolationType;
import com.sonter.exam_protector.repository.ExamSubmissionRepository;
import com.sonter.exam_protector.repository.UserRepository;
import com.sonter.exam_protector.repository.ViolationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationLogRepository violationLogRepository;
    private final ExamSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> reportViolation(ViolationRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ExamSubmission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (!submission.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Not your submission");
        }

        if (submission.getStatus() != SubmissionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Exam is not in progress");
        }

        ViolationLog log = ViolationLog.builder()
                .submission(submission)
                .user(user)
                .violationType(request.getViolationType())
                .severity(request.getSeverity())
                .details(request.getDetails())
                .clientTimestamp(request.getClientTimestamp())
                .build();

        violationLogRepository.save(log);

        // Check lockout threshold
        long count = violationLogRepository.countBySubmissionId(request.getSubmissionId());
        int maxViolations = submission.getExam().getMaxTabViolations() != null
                ? submission.getExam().getMaxTabViolations() : 3;

        if (count >= maxViolations) {
            submission.setStatus(SubmissionStatus.LOCKED);
            submissionRepository.save(submission);
            return Map.of("action", "LOCKED", "count", count);
        }

        return Map.of("action", "WARNING", "count", count);
    }

    @Transactional
    public void logServerViolation(Long submissionId, ViolationType type, String details) {
        ExamSubmission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null || submission.getStatus() != SubmissionStatus.IN_PROGRESS) return;

        ViolationLog log = ViolationLog.builder()
                .submission(submission)
                .user(submission.getUser())
                .violationType(type)
                .severity(ViolationSeverity.HIGH)
                .details(details)
                .clientTimestamp(LocalDateTime.now())
                .build();

        violationLogRepository.save(log);
    }

    public List<ViolationLog> getViolationsBySubmission(Long submissionId) {
        return violationLogRepository.findBySubmissionId(submissionId);
    }
}
