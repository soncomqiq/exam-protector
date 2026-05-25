package com.sonter.exam_protector.config;

import com.sonter.exam_protector.websocket.HeartbeatHandler;
import com.sonter.exam_protector.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final HeartbeatHandler heartbeatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(heartbeatHandler, "/ws/heartbeat")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:5173");
    }
}
