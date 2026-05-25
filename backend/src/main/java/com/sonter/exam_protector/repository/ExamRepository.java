package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.Exam;
import com.sonter.exam_protector.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCreatedBy(User createdBy);
    List<Exam> findByIsPublishedTrue();
}
