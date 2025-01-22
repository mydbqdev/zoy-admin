package com.integration.zoy.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {

    // A map to hold WebSocket sessions for each user (userId -> WebSocketSession)
    private final ConcurrentHashMap<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    // Add a new session for a user
    public void addSession(String userId, WebSocketSession session) {
        userSessions.put(userId, session);
    }

    // Remove a session when the user disconnects
    public void removeSession(String userId) {
        userSessions.remove(userId);
    }

    // Get the session for a user
    public WebSocketSession getSession(String userId) {
        return userSessions.get(userId);
    }

    // Check if a user is currently connected
    public boolean isUserConnected(String userId) {
        return userSessions.containsKey(userId);
    }
}
