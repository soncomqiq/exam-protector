package com.sonter.exam_protector.repository;

import com.sonter.exam_protector.model.ScreenRecording;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRecordingRepository extends JpaRepository<ScreenRecording, Long> {
    List<ScreenRecording> findBySubmissionIdOrderByChunkIndexAsc(Long submissionId);
    int countBySubmissionId(Long submissionId);
}
