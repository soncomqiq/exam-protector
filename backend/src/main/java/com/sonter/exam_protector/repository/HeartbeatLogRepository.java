package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.HeartbeatLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeartbeatLogRepository extends JpaRepository<HeartbeatLog, Long> {
    List<HeartbeatLog> findBySubmissionIdOrderByReceivedAtDesc(Long submissionId);
}
