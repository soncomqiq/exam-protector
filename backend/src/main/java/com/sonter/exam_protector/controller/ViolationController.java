package com.sonter.exam_protector.controller;

import com.sonter.exam_protector.dto.request.ViolationRequest;
import com.sonter.exam_protector.model.Exam;
import com.sonter.exam_protector.model.ExamSubmission;
import com.sonter.exam_protector.model.ViolationLog;
import com.sonter.exam_protector.model.enums.SubmissionStatus;
import com.sonter.exam_protector.repository.ExamRepository;
import com.sonter.exam_protector.repository.ExamSubmissionRepository;
import com.sonter.exam_protector.repository.ViolationLogRepository;
import com.sonter.exam_protector.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationLogRepository violationRepo;
    private final ExamSubmissionRepository submissionRepo;
    private final ExamRepository examRepo;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> reportViolation(
            @Valid @RequestBody ViolationRequest req,
            @AuthenticationPrincipal UserDetailsImpl user) {

        ExamSubmission sub = submissionRepo.findById(req.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        
        if (!sub.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("Not your submission");
        }

        ViolationLog log = new ViolationLog();
        log.setSubmissionId(req.getSubmissionId());
        log.setUserId(user.getId());
        log.setViolationType(req.getViolationType());
        log.setSeverity(req.getSeverity());
        log.setDetails(req.getDetails());
        log.setClientTimestamp(req.getClientTimestamp());
        violationRepo.save(log);

        long count = violationRepo.countBySubmissionId(req.getSubmissionId());
        Exam exam = examRepo.findById(sub.getExamId()).orElseThrow();
        
        if (count >= exam.getMaxTabViolations()) {
            sub.setStatus(SubmissionStatus.LOCKED);
            submissionRepo.save(sub);
            return ResponseEntity.ok(Map.of("action", "LOCKED"));
        }

        return ResponseEntity.ok(Map.of("action", "WARNING", "count", count));
    }
    
    @GetMapping("/{submissionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getViolationLog(@PathVariable Long submissionId) {
        return ResponseEntity.ok(violationRepo.findBySubmissionId(submissionId));
    }
}
