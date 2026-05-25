package com.sonter.exam_protector.controller;

import com.sonter.exam_protector.dto.request.AnswerRequest;
import com.sonter.exam_protector.dto.response.ApiResponse;
import com.sonter.exam_protector.dto.response.SubmissionResponse;
import com.sonter.exam_protector.service.SubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<SubmissionResponse>> startExam(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails user,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        return ResponseEntity.ok(ApiResponse.ok(submissionService.startExam(examId, user.getUsername(), ip, ua)));
    }

    @PostMapping("/exams/{examId}/submit")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitExam(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(submissionService.submitExam(examId, user.getUsername())));
    }

    @PutMapping("/submissions/{id}/answers")
    public ResponseEntity<ApiResponse<Void>> saveAnswer(
            @PathVariable Long id,
            @RequestBody AnswerRequest request,
            @AuthenticationPrincipal UserDetails user) {
        submissionService.saveAnswer(id, request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Answer saved", null));
    }

    @GetMapping("/exams/{examId}/submissions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissions(@PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.ok(submissionService.getSubmissionsByExam(examId)));
    }

    @GetMapping("/my-submissions")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getMySubmissions(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(submissionService.getSubmissionsByStudent(user.getUsername())));
    }
}
