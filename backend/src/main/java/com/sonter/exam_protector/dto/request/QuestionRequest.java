package com.sonter.exam_protector.dto.request;

import com.sonter.exam_protector.model.enums.QuestionType;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class QuestionRequest {
    private String questionText;
    private QuestionType questionType;
    private Integer points;
    private Integer sortOrder;
    private List<OptionRequest> options;

    @Getter
    @Setter
    public static class OptionRequest {
        private String optionText;
        private Boolean isCorrect;
        private Integer sortOrder;
    }
}
