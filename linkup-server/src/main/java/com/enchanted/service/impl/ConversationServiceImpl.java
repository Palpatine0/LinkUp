package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Conversation;
import com.enchanted.mapper.ConversationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements IConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    /* C */
    @Override
    public boolean save(Conversation conversation) {
        return conversationMapper.insert(conversation) > 0;
    }

    /* R */
    @Override
    public Page<Conversation> search(Map<String, Object> params, int page, int size) {
        IPage<Conversation> conversationPage = new Page<>(page, size);
        conversationPage = conversationMapper.search(conversationPage, params);
        return (Page<Conversation>) conversationPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Conversation.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                ReflectionUtils.setField(classField, conversation, value);
            }
        });

        int updated = conversationMapper.updateById(conversation);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        return conversationMapper.deleteById(id) > 0;
    }
}
