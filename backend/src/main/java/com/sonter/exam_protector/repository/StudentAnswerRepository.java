package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findBySubmissionId(Long submissionId);
    Optional<StudentAnswer> findBySubmissionIdAndQuestionId(Long submissionId, Long questionId);
}
