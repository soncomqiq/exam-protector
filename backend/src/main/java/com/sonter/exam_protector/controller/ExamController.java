package com.sonter.exam_protector.controller;

import com.sonter.exam_protector.dto.request.ExamRequest;
import com.sonter.exam_protector.dto.request.QuestionRequest;
import com.sonter.exam_protector.security.UserDetailsImpl;
import com.sonter.exam_protector.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createExam(@RequestBody ExamRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(examService.createExam(request, userDetails.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateExam(@PathVariable Long id,
                                        @RequestBody ExamRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(examService.updateExam(id, request, userDetails.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteExam(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            examService.deleteExam(id, userDetails.getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> addQuestion(@PathVariable Long id,
                                         @RequestBody QuestionRequest request,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.ok(examService.addQuestion(id, request, userDetails.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
