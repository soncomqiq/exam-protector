package com.sonter.exam_protector.dto.request;

import com.sonter.exam_protector.model.enums.ViolationSeverity;
import com.sonter.exam_protector.model.enums.ViolationType;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ViolationRequest {
    private Long submissionId;
    private ViolationType violationType;
    private ViolationSeverity severity;
    private String details;
    private LocalDateTime clientTimestamp;
}
