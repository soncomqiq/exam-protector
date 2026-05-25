package com.sonter.exam_protector.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonter.exam_protector.model.HeartbeatLog;
import com.sonter.exam_protector.model.ExamSubmission;
import com.sonter.exam_protector.model.enums.SubmissionStatus;
import com.sonter.exam_protector.model.enums.ViolationType;
import com.sonter.exam_protector.repository.ExamSubmissionRepository;
import com.sonter.exam_protector.repository.HeartbeatLogRepository;
import com.sonter.exam_protector.service.ViolationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartbeatHandler extends TextWebSocketHandler {

    private final HeartbeatLogRepository heartbeatLogRepository;
    private final ExamSubmissionRepository submissionRepository;
    private final ViolationService violationService;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<Long, Instant> lastSeen = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connected: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode payload = objectMapper.readTree(message.getPayload());

        Long submissionId = payload.get("submissionId").asLong();
        boolean screenSharing = payload.path("screenSharing").asBoolean(false);
        boolean tabVisible = payload.path("tabVisible").asBoolean(true);

        lastSeen.put(submissionId, Instant.now());

        ExamSubmission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null || submission.getStatus() != SubmissionStatus.IN_PROGRESS) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        HeartbeatLog heartbeat = HeartbeatLog.builder()
                .submission(submission)
                .screenSharing(screenSharing)
                .tabVisible(tabVisible)
                .build();

        heartbeatLogRepository.save(heartbeat);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket disconnected: {} ({})", session.getId(), status);
    }

    @Scheduled(fixedRate = 20000)
    public void detectMissedHeartbeats() {
        Instant threshold = Instant.now().minusSeconds(45);
        lastSeen.forEach((subId, lastTime) -> {
            if (lastTime.isBefore(threshold)) {
                violationService.logServerViolation(subId, ViolationType.HEARTBEAT_MISS,
                        "No heartbeat for 45s+");
                lastSeen.remove(subId);
            }
        });
    }
}
