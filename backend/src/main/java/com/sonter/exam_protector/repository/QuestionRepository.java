package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamIdOrderBySortOrder(Long examId);
}
