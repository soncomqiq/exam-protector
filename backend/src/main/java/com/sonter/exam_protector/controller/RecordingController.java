package com.sonter.exam_protector.controller;

import com.sonter.exam_protector.dto.response.ApiResponse;
import com.sonter.exam_protector.model.ScreenRecording;
import com.sonter.exam_protector.service.RecordingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/recordings")
@RequiredArgsConstructor
public class RecordingController {

    private final RecordingService recordingService;

    /**
     * Upload a recording chunk (called by student client during exam)
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Long>> uploadChunk(
            @RequestParam("submissionId") Long submissionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "durationMs", defaultValue = "0") Long durationMs) throws IOException {

        ScreenRecording saved = recordingService.uploadChunk(submissionId, file, durationMs);
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Chunk uploaded")
                .data(saved.getId())
                .build());
    }

    /**
     * List all recording chunks for a submission (teacher view)
     */
    @GetMapping("/submission/{submissionId}")
    public ResponseEntity<ApiResponse<List<RecordingInfo>>> listRecordings(
            @PathVariable Long submissionId) {

        List<ScreenRecording> recordings = recordingService.getRecordingsForSubmission(submissionId);
        List<RecordingInfo> infos = recordings.stream().map(r -> new RecordingInfo(
                r.getId(), r.getChunkIndex(), r.getFileSize(), r.getDurationMs()
        )).toList();

        return ResponseEntity.ok(ApiResponse.<List<RecordingInfo>>builder()
                .success(true)
                .data(infos)
                .build());
    }

    /**
     * Stream/download a specific recording chunk (teacher view)
     */
    @GetMapping("/{recordingId}/stream")
    public ResponseEntity<Resource> streamRecording(@PathVariable Long recordingId) {
        Resource resource = recordingService.getRecordingFile(recordingId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/webm"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"recording_" + recordingId + ".webm\"")
                .body(resource);
    }

    record RecordingInfo(Long id, Integer chunkIndex, Long fileSize, Long durationMs) {}
}
