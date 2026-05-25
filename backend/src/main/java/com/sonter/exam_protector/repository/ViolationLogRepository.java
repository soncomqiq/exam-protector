package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.ViolationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationLogRepository extends JpaRepository<ViolationLog, Long> {
    long countBySubmissionId(Long submissionId);
    List<ViolationLog> findBySubmissionId(Long submissionId);
}
