package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Gift;

import java.util.Map;

public interface IGiftService extends IService<Gift> {

    /* C */
    boolean save(Gift gift);

    Long purchase(Long senderId, Long recipientId, Long giftId);

    /* R */
    Page<Gift> search(Map<String, Object> params, int page, int size);

    /* U */
    boolean update(Long id, Map<String, Object> changes);

    /* D */
    boolean delete(Long id);
}
