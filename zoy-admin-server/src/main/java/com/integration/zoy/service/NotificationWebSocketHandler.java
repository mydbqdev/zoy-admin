package com.integration.zoy.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.entity.NotificationsAndAlerts;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private SessionManager sessionManager;
    
    private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
				return new JsonPrimitive(dateFormat.format(src)); 
			})
			.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
					return new Timestamp(dateFormat.parse(json.getAsString()).getTime()); 
				} catch (Exception e) {
					throw new JsonParseException("Failed to parse Timestamp", e);
				}
			})
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();
  

    // Called when a WebSocket connection is established
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Assuming the user information is passed as a header or URI param
    	String query = session.getUri().getQuery();
    	 
        if (query != null && query.contains("userId=")) {
            String userId = query.split("userId=")[1].split("&")[0];
            sessionManager.addSession(userId, session);
        }
    }

    // Called when the WebSocket connection is closed
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
    }
    

    // Send a notification to a specific user
    public void sendNotification(String userId, NotificationsAndAlerts notificationMessage) throws Exception {
        WebSocketSession userSession = sessionManager.getSession(userId);
        if (userSession != null && userSession.isOpen()) {
            TextMessage message = new TextMessage(gson.toJson(notificationMessage));
            userSession.sendMessage(message);
        }
    }

    // Called if an error occurs during WebSocket communication
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Error occurred: " + exception.getMessage());
    }
}
