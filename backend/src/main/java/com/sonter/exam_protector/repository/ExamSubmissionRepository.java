package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.ExamSubmission;
import com.sonter.exam_protector.model.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    Optional<ExamSubmission> findByExamIdAndUserId(Long examId, Long userId);
    List<ExamSubmission> findByStatus(SubmissionStatus status);
    List<ExamSubmission> findByExamId(Long examId);
    List<ExamSubmission> findByUserId(Long userId);
}
