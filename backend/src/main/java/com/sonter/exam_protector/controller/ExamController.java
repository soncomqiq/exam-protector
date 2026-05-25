package com.sonter.exam_protector.controller;

import com.sonter.exam_protector.dto.request.ExamRequest;
import com.sonter.exam_protector.dto.request.QuestionRequest;
import com.sonter.exam_protector.dto.response.ApiResponse;
import com.sonter.exam_protector.dto.response.ExamResponse;
import com.sonter.exam_protector.dto.response.QuestionResponse;
import com.sonter.exam_protector.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getMyExams(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getExamsByTeacher(user.getUsername())));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getAvailableExams() {
        return ResponseEntity.ok(ApiResponse.ok(examService.getAvailableExams()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getExamById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(
            @Valid @RequestBody ExamRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(examService.createExam(request, user.getUsername())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExamResponse>> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(examService.updateExam(id, request, user.getUsername())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteExam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        examService.deleteExam(id, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Exam deleted", null));
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(examService.addQuestion(id, request, user.getUsername())));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getQuestions(id)));
    }
}
