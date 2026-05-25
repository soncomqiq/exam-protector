package com.sonter.exam_protector.dto.request;

import com.sonter.exam_protector.model.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    @NotBlank
    private String questionText;

    @NotNull
    private QuestionType questionType;

    private Integer points = 1;
    private Integer sortOrder = 0;
    private List<OptionRequest> options;

    @Data
    public static class OptionRequest {
        @NotBlank
        private String optionText;
        private Boolean isCorrect = false;
        private Integer sortOrder = 0;
    }
}
