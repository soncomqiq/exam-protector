package com.sonter.exam_protector.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Integer durationMinutes;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private Integer maxTabViolations = 3;
    private Boolean screenShareRequired = true;
    private Integer gracePeriodSeconds = 30;
    private Boolean isPublished = false;
}
