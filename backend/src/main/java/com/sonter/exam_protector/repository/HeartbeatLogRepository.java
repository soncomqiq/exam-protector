package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.HeartbeatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartbeatLogRepository extends JpaRepository<HeartbeatLog, Long> {
    List<HeartbeatLog> findBySubmissionId(Long submissionId);
}
