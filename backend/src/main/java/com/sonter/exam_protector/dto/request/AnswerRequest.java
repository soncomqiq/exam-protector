package com.sonter.exam_protector.dto.request;

import lombok.Data;

@Data
public class AnswerRequest {
    private Long questionId;
    private Long selectedOptionId;
    private String answerText;
}
