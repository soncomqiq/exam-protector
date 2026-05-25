package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.ViolationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViolationLogRepository extends JpaRepository<ViolationLog, Long> {
    List<ViolationLog> findBySubmissionId(Long submissionId);
    long countBySubmissionId(Long submissionId);
}
