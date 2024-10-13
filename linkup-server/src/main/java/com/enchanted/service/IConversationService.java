package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Conversation;

import java.util.Map;

public interface IConversationService extends IService<Conversation> {

    /* C */
    boolean save(Conversation conversation);

    /* R */
    Page<Conversation> search(Map<String, Object> params, int page, int size);

    /* U */
    boolean update(Long id, Map<String, Object> changes);

    /* D */
    boolean delete(Long id);
}
