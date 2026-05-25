package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.ExamSubmission;
import com.sonter.exam_protector.model.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    List<ExamSubmission> findByStatus(SubmissionStatus status);
    Optional<ExamSubmission> findByExamIdAndUserId(Long examId, Long userId);
}
