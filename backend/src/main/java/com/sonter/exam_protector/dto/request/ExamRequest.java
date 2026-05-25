package com.sonter.exam_protector.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExamRequest {
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxTabViolations;
    private Boolean screenShareRequired;
    private Integer gracePeriodSeconds;
    private Boolean isPublished;
}
