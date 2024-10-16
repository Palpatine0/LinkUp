package com.enchanted.handler;

import com.enchanted.entity.Conversation;
import com.enchanted.entity.Message;
import com.enchanted.mapper.MessageMapper;
import com.enchanted.service.IConversationService;
import com.enchanted.service.IMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IConversationService conversationService;

    @Autowired
    private MessageMapper messageMapper;

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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("User " + userId + " disconnected.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> messageData = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) messageData.get("type");
        Map<String, Object> data = (Map<String, Object>) messageData.get("data");

        if ("message".equals(type)) {
            handleChatMessage(session, data);
        } else if ("readReceipt".equals(type)) {
            handleReadReceipt(session, data);
        }
    }

    private void handleChatMessage(WebSocketSession session, Map<String, Object> data) throws Exception {
        Long senderId = Long.valueOf(data.get("senderId").toString());
        Long recipientId = Long.valueOf(data.get("recipientId").toString());
        Long conversationId = Long.valueOf(data.get("conversationId").toString());
        Long tempId = Long.valueOf(data.get("tempId").toString());
        String content = data.get("content").toString();
        Integer mediaType = data.get("mediaType") != null ? Integer.valueOf(data.get("mediaType").toString()) : 0;

        // Validate the conversation
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation == null) {
            session.sendMessage(new TextMessage("{\"type\":\"error\",\"message\":\"Invalid conversation ID.\"}"));
            return;
        }

        // Save message to the database
        Message chatMessage = new Message();
        chatMessage.setSenderId(senderId);
        chatMessage.setRecipientId(recipientId);
        chatMessage.setConversationId(conversationId);
        chatMessage.setTempId(tempId);
        chatMessage.setContent(content);
        chatMessage.setMediaType(mediaType);
        chatMessage.setStatus(1);
        chatMessage.setIsRead(0);
        chatMessage.setCreatedAt(new Date());
        messageService.save(chatMessage);

        // Update conversation
        if (senderId.equals(conversation.getClientId())) {
            // Client sent a message, servant response is required
            conversation.setServantResponseRequired(1);
            conversation.setLastClientMessageTime(new Date());
            conversationService.updateById(conversation);
        } else if (senderId.equals(conversation.getServantId())) {
            // Servant responded, no response required
            conversation.setServantResponseRequired(0);
            conversationService.updateById(conversation);
        }

        // Prepare message to send
        Map<String, Object> sendMessageData = new HashMap<>();
        sendMessageData.put("type", "message");
        Map<String, Object> sendData = new HashMap<>();
        sendData.put("id", chatMessage.getId());
        sendData.put("senderId", senderId);
        sendData.put("recipientId", recipientId);
        sendData.put("content", content);
        sendData.put("mediaType", mediaType);
        sendData.put("createdAt", chatMessage.getCreatedAt().toString());
        sendData.put("isRead", chatMessage.getIsRead());
        sendMessageData.put("data", sendData);
        String sendMessageStr = objectMapper.writeValueAsString(sendMessageData);

        // Send message to recipient if online
        WebSocketSession recipientSession = userSessions.get(recipientId);
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(sendMessageStr));
        }
    }

    private void handleReadReceipt(WebSocketSession session, Map<String, Object> data) throws Exception {
        List<Integer> messageIdIntegers = (List<Integer>) data.get("messageIds");
        List<Long> messageIds = messageIdIntegers.stream().map(Integer::longValue).collect(Collectors.toList());
        Long readerId = Long.valueOf(data.get("senderId").toString());
        Long originalSenderId = Long.valueOf(data.get("recipientId").toString());

        // Update database to mark messages as read
        messageService.markAsRead(messageIds);

        // Fetch corresponding tempIds from the message table
        List<Message> messages = messageMapper.selectBatchIds(messageIds);
        List<Long> tempIds = messages.stream()
            .map(Message::getTempId)
            .collect(Collectors.toList());

        // Prepare read receipt to send with both messageIds and tempIds
        Map<String, Object> sendReadReceiptData = new HashMap<>();
        sendReadReceiptData.put("type", "readReceipt");
        Map<String, Object> readReceiptData = new HashMap<>();
        readReceiptData.put("messageIds", messageIds);  // Real database IDs
        readReceiptData.put("tempMessageIds", tempIds);        // Corresponding temp IDs
        readReceiptData.put("readerId", readerId);
        sendReadReceiptData.put("data", readReceiptData);

        String readReceiptStr = objectMapper.writeValueAsString(sendReadReceiptData);

        // Send read receipt to original sender if online
        WebSocketSession senderSession = userSessions.get(originalSenderId);
        if (senderSession != null && senderSession.isOpen()) {
            senderSession.sendMessage(new TextMessage(readReceiptStr));
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
