package com.sonter.exam_protector.controller;

import com.sonter.exam_protector.dto.request.ViolationRequest;
import com.sonter.exam_protector.dto.response.ApiResponse;
import com.sonter.exam_protector.model.ViolationLog;
import com.sonter.exam_protector.service.ViolationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
public class ViolationController {

    private final ViolationService violationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> reportViolation(
            @Valid @RequestBody ViolationRequest request,
            @AuthenticationPrincipal UserDetails user) {
        Map<String, Object> result = violationService.reportViolation(request, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{submissionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ViolationLog>>> getViolations(@PathVariable Long submissionId) {
        return ResponseEntity.ok(ApiResponse.ok(violationService.getViolationsBySubmission(submissionId)));
    }
}
