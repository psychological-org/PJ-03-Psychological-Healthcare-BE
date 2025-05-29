package com.microservices.notification.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // gửi từ server → client
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user"); // client gửi → server
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. raw WebSocket handshake tại /ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
        // 2. SockJS-fallback (tuỳ nếu bạn còn cần client web dùng SockJS)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

}
