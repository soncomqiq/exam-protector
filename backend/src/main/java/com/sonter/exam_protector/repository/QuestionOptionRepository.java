package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestionIdOrderBySortOrder(Long questionId);
}
