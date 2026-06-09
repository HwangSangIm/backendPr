package com.mapleinfo.rank.handler;

import com.mapleinfo.rank.dto.SearchRankResponse;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
public class LiveRankWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcastRankList(List<SearchRankResponse> rankList) {
        sendToSessions(sessions, rankList);
    }

    public void sendToSingleSession(WebSocketSession session, List<SearchRankResponse> rankList) {
        sendToSessions(Set.of(session), rankList);
    }

    private void sendToSessions(Set<WebSocketSession> targetSessions, List<SearchRankResponse> rankList) {
        try {
            String json = objectMapper.writeValueAsString(rankList);
            TextMessage message = new TextMessage(json);

            for (WebSocketSession session : targetSessions) {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}