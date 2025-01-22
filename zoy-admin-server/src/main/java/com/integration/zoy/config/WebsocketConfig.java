package com.integration.zoy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.integration.zoy.service.NotificationWebSocketHandler;

@Configuration
@EnableWebSocket

public class WebsocketConfig implements WebSocketConfigurer {

	private NotificationWebSocketHandler notificationWebSocketHandler;

	public WebsocketConfig(NotificationWebSocketHandler marketPageTextHandler) {
		this.notificationWebSocketHandler = marketPageTextHandler;

	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(notificationWebSocketHandler, "/notificationPageHandler").setAllowedOrigins("*")
				.addInterceptors(new HttpSessionHandshakeInterceptor())
				.setHandshakeHandler(new DefaultHandshakeHandler());
		; 
	}

}