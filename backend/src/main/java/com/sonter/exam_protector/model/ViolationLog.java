package com.sonter.exam_protector.model;

import com.sonter.exam_protector.model.enums.ViolationSeverity;
import com.sonter.exam_protector.model.enums.ViolationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "violation_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private ExamSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type", nullable = false)
    private ViolationType violationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationSeverity severity;

    @Column(columnDefinition = "JSON")
    private String details;

    @Column(name = "client_timestamp", nullable = false)
    private LocalDateTime clientTimestamp;

    @CreationTimestamp
    @Column(name = "server_timestamp", updatable = false)
    private LocalDateTime serverTimestamp;
}
