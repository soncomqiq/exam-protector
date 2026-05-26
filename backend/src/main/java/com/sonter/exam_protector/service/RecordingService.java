package com.sonter.exam_protector.service;

import com.sonter.exam_protector.model.ExamSubmission;
import com.sonter.exam_protector.model.ScreenRecording;
import com.sonter.exam_protector.repository.ExamSubmissionRepository;
import com.sonter.exam_protector.repository.ScreenRecordingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordingService {

    private final ScreenRecordingRepository recordingRepository;
    private final ExamSubmissionRepository submissionRepository;

    @Value("${app.recordings.storage-path}")
    private String storagePath;

    public ScreenRecording uploadChunk(Long submissionId, MultipartFile file, Long durationMs) throws IOException {
        ExamSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));

        int nextIndex = recordingRepository.countBySubmissionId(submissionId);

        // Create directory: recordings/{submissionId}/
        Path dir = Paths.get(storagePath, String.valueOf(submissionId));
        Files.createDirectories(dir);

        // Save file: recordings/{submissionId}/chunk_{index}.webm
        String filename = "chunk_" + nextIndex + ".webm";
        Path filePath = dir.resolve(filename);
        file.transferTo(filePath.toFile());

        ScreenRecording recording = ScreenRecording.builder()
                .submission(submission)
                .chunkIndex(nextIndex)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType() != null ? file.getContentType() : "video/webm")
                .durationMs(durationMs)
                .build();

        return recordingRepository.save(recording);
    }

    public List<ScreenRecording> getRecordingsForSubmission(Long submissionId) {
        return recordingRepository.findBySubmissionIdOrderByChunkIndexAsc(submissionId);
    }

    public Resource getRecordingFile(Long recordingId) {
        ScreenRecording recording = recordingRepository.findById(recordingId)
                .orElseThrow(() -> new IllegalArgumentException("Recording not found: " + recordingId));

        Path path = Paths.get(recording.getFilePath());
        if (!Files.exists(path)) {
            throw new IllegalStateException("Recording file missing on disk: " + recording.getFilePath());
        }
        return new FileSystemResource(path);
    }
}
