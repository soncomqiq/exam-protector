package com.sonter.exam_protector.service;

import com.sonter.exam_protector.model.HeartbeatLog;
import com.sonter.exam_protector.model.enums.ViolationType;
import com.sonter.exam_protector.model.enums.ViolationSeverity;
import com.sonter.exam_protector.model.ViolationLog;
import com.sonter.exam_protector.repository.HeartbeatLogRepository;
import com.sonter.exam_protector.repository.ViolationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationLogRepository violationRepo;
    private final HeartbeatLogRepository heartbeatRepo;

    @Transactional
    public void logServerViolation(Long submissionId, Long userId, ViolationType type, String details) {
        ViolationLog log = new ViolationLog();
        log.setSubmissionId(submissionId);
        log.setUserId(userId); // can be null if not readily available in WS, but schema requires it
        log.setViolationType(type);
        log.setSeverity(ViolationSeverity.HIGH);
        log.setDetails(details);
        log.setClientTimestamp(LocalDateTime.now());
        violationRepo.save(log);
    }
}
