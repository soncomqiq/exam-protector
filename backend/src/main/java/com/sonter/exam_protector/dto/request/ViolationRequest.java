package com.sonter.exam_protector.dto.request;

import com.sonter.exam_protector.model.enums.ViolationSeverity;
import com.sonter.exam_protector.model.enums.ViolationType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationRequest {
    @NotNull
    private Long submissionId;

    @NotNull
    private ViolationType violationType;

    @NotNull
    private ViolationSeverity severity;

    @NotNull
    private LocalDateTime clientTimestamp;

    private String details;
}
