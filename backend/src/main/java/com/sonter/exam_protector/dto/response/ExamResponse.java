package com.sonter.exam_protector.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private String createdByName;
    private Integer durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxTabViolations;
    private Boolean screenShareRequired;
    private Integer gracePeriodSeconds;
    private Boolean isPublished;
    private LocalDateTime createdAt;
}
