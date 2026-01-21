package org.example.binancetickerdemo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PriceWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println("Client disconnected: " + session.getId());
    }

    public void broadcastPrice(String symbol, BigDecimal price, long binanceTimestamp) {
        executor.submit(() -> {
            try {
                Map<String, Object> message = Map.of(
                        "symbol", symbol,
                        "price", price,
                        "binanceTime", binanceTimestamp,
                        "serverTime", System.currentTimeMillis()
                );

                String json = objectMapper.writeValueAsString(message);
                TextMessage textMessage = new TextMessage(json);

                // Send to all clients in parallel
                for (WebSocketSession session : sessions.values()) {
                    if (session.isOpen()) {
                        executor.submit(() -> {
                            try {
                                session.sendMessage(textMessage);
                            } catch (Exception e) {
                                // Session might have closed
                            }
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("Error broadcasting price: " + e.getMessage());
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}