package com.sonter.exam_protector.dto.response;

import com.sonter.exam_protector.model.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long examId;
    private String examTitle;
    private String studentName;
    private String studentEmail;
    private SubmissionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private BigDecimal score;
    private List<QuestionResponse> questions;
}
