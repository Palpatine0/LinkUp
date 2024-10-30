package com.enchanted.handler;

import com.enchanted.entity.Conversation;
import com.enchanted.entity.Message;
import com.enchanted.entity.User;
import com.enchanted.mapper.ConversationMapper;
import com.enchanted.mapper.MessageMapper;
import com.enchanted.mapper.UserMapper;
import com.enchanted.service.IConversationService;
import com.enchanted.service.IMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
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

    @Autowired
    private ConversationMapper conversationMapper;

    private static Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserMapper userMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
        } else {
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
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
            sendErrorMessage(session, "Invalid conversation ID.");
            return;
        }

        // Update conversation fields as needed
        if (!updateConversation(conversation, senderId, recipientId, session)) {
            return;  // Stop further processing if there’s an error
        }

        // Save the message to the database
        Message chatMessage = saveChatMessage(senderId, recipientId, conversationId, tempId, content, mediaType);

        // Prepare and send the message to the recipient and sender
        Map<String, Object> sendMessageData = prepareMessageData(chatMessage, tempId);
        String sendMessageStr = objectMapper.writeValueAsString(sendMessageData);

        // Send message to recipient if online
        WebSocketSession recipientSession = userSessions.get(recipientId);
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(sendMessageStr));
        }

        // Send message back to sender to update local message with database ID
        WebSocketSession senderSession = userSessions.get(senderId);
        if (senderSession != null && senderSession.isOpen() && senderSession != session) {
            senderSession.sendMessage(new TextMessage(sendMessageStr));
        }

        // Update contact list view for both sender and recipient
        updateContactListForUser(conversationId, senderId, recipientId);
        updateContactListForUser(conversationId, recipientId, senderId);
    }

    // Helper to send an error message using session
    private void sendErrorMessage(WebSocketSession session, String message) throws IOException, JsonProcessingException {
        Map<String, Object> errorMessageData = new HashMap<>();
        errorMessageData.put("type", "error");
        errorMessageData.put("data", message);

        String errorMessageStr = objectMapper.writeValueAsString(errorMessageData);
        session.sendMessage(new TextMessage(errorMessageStr));
    }

    // Helper to update conversation status fields based on sender
    private boolean updateConversation(Conversation conversation, Long senderId, Long recipientId, WebSocketSession session) throws IOException {
        if (senderId.equals(conversation.getClientId())) {
            if (conversation.getIsServantMessagingAvailable() == 0) {
                conversation.setIsServantMessagingAvailable(1);
            }
            conversation.setServantResponseRequired(1);
            conversation.setLastClientMessageTime(new Date());
            conversationService.updateById(conversation);
            return true;
        } else if (senderId.equals(conversation.getServantId())) {
            if (conversation.getIsServantMessagingAvailable() == 0) {
                sendErrorMessage(session, "No permission to initiate conversation.");
                return false;
            }
            conversation.setServantResponseRequired(0);
            conversationService.updateById(conversation);
            return true;
        }
        return false;
    }

    // Helper to save the chat message
    private Message saveChatMessage(Long senderId, Long recipientId, Long conversationId, Long tempId, String content, Integer mediaType) {
        Message chatMessage = new Message();
        chatMessage.setSenderId(senderId);
        chatMessage.setRecipientId(recipientId);
        chatMessage.setConversationId(conversationId);
        chatMessage.setTempId(tempId);
        chatMessage.setContent(content);
        chatMessage.setMediaType(mediaType);
        chatMessage.setStatus(1); // Sent
        chatMessage.setIsRead(0);
        chatMessage.setCreatedAt(new Date());
        messageService.save(chatMessage);
        return chatMessage;
    }

    // Helper to prepare message data for WebSocket
    private Map<String, Object> prepareMessageData(Message message, Long tempId) {
        Map<String, Object> sendMessageData = new HashMap<>();
        sendMessageData.put("type", "message");
        Map<String, Object> sendData = new HashMap<>();
        sendData.put("id", message.getId());
        sendData.put("tempId", tempId);
        sendData.put("senderId", message.getSenderId());
        sendData.put("recipientId", message.getRecipientId());
        sendData.put("content", message.getContent());
        sendData.put("mediaType", message.getMediaType());
        sendData.put("createdAt", message.getCreatedAt().toString());
        sendData.put("isRead", message.getIsRead());
        sendMessageData.put("data", sendData);
        return sendMessageData;
    }

    // Helper to update the contact list for a user
    private void updateContactListForUser(Long conversationId, Long recipientId, Long senderId) throws IOException {
        Message latestMessage = conversationService.getLatestMessage(conversationId);
        int unreadMessageCount = conversationService.countUnreadMessages(recipientId, conversationId);
        Conversation conversation = conversationMapper.selectById(conversationId);
        User sender = userMapper.selectById(senderId);

        Map<String, Object> contactUpdateData = new HashMap<>();
        contactUpdateData.put("type", "contactUpdate");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("conversationId", conversationId);
        dataMap.put("unreadMessageCount", unreadMessageCount);
        dataMap.put("latestMessage", latestMessage.getContent());
        dataMap.put("messageTime", latestMessage.getCreatedAt());
        dataMap.put("clientId", conversation.getClientId());
        dataMap.put("servantId", conversation.getServantId());
        dataMap.put("nickname", sender.getNickname());
        dataMap.put("avatar", sender.getAvatar());
        contactUpdateData.put("data", dataMap);

        String contactUpdateStr = objectMapper.writeValueAsString(contactUpdateData);

        WebSocketSession userSession = userSessions.get(recipientId);
        if (userSession != null && userSession.isOpen()) {
            userSession.sendMessage(new TextMessage(contactUpdateStr));
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
        } else {
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
