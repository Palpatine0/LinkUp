package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Conversation;
import com.enchanted.entity.Message;
import com.enchanted.entity.User;

import java.util.Map;

public interface IConversationService extends IService<Conversation> {

    /* C */
    boolean save(Conversation conversation);

    /* R */
    Page<Conversation> search(Map<String, Object> params, int page, int size);

    Page<User> searchContacts(Map<String, Object> params, int page, int size);

    Message getLatestMessage(Long conversationId);

    int countUnreadMessages(Long userId, Long conversationId);

    /* U */
    boolean update(Long id, Map<String, Object> changes);

    /* D */
    boolean delete(Long id);
}
