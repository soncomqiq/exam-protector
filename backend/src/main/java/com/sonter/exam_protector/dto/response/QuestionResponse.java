package com.sonter.exam_protector.dto.response;

import com.sonter.exam_protector.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private Integer points;
    private Integer sortOrder;
    private List<OptionResponse> options;

    @Data
    @Builder
    @AllArgsConstructor
    public static class OptionResponse {
        private Long id;
        private String optionText;
        private Integer sortOrder;
        // Note: isCorrect is intentionally excluded for student-facing responses
    }
}
