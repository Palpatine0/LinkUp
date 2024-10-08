package com.enchanted.handler;

import com.enchanted.entity.Message;
import com.enchanted.service.IMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private IMessageService messageService;

    // Map to track user sessions
    private static Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
            System.out.println("User " + userId + " connected.");
        } else {
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> messageData = objectMapper.readValue(message.getPayload(), Map.class);
        Long senderId = Long.valueOf(messageData.get("senderId").toString());
        Long recipientId = Long.valueOf(messageData.get("recipientId").toString());
        String content = messageData.get("content").toString();
        Integer mediaType = messageData.get("mediaType") != null ? Integer.valueOf(messageData.get("mediaType").toString()) : 0;

        // Save message to the database
        Message chatMessage = new Message();
        chatMessage.setSenderId(senderId);
        chatMessage.setRecipientId(recipientId);
        chatMessage.setContent(content);
        chatMessage.setMediaType(mediaType);
        chatMessage.setStatus(1);
        chatMessage.setIsRead(0);
        chatMessage.setCreatedAt(new Date());
        messageService.save(chatMessage);

        // Prepare message to send
        Map<String, Object> sendMessageData = new HashMap<>();
        sendMessageData.put("id", chatMessage.getId());
        sendMessageData.put("senderId", senderId);
        sendMessageData.put("recipientId", recipientId);
        sendMessageData.put("content", content);
        sendMessageData.put("mediaType", mediaType);
        sendMessageData.put("createdAt", chatMessage.getCreatedAt().toString());
        String sendMessageStr = objectMapper.writeValueAsString(sendMessageData);

        // Send message to recipient if online
        WebSocketSession recipientSession = userSessions.get(recipientId);
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(sendMessageStr));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("User " + userId + " disconnected.");
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        Map<String, String> params = parseQueryParams(session.getUri().getQuery());
        String userIdStr = params.get("userId");
        if (userIdStr != null) {
            try {
                return Long.valueOf(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }
}
