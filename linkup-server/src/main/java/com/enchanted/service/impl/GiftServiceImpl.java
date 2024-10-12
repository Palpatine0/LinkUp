package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Gift;
import com.enchanted.mapper.GiftMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class GiftServiceImpl extends ServiceImpl<GiftMapper, Gift> implements IGiftService {

    @Autowired
    private GiftMapper giftMapper;

    /* C */
    @Override
    public boolean save(Gift gift) {
        return giftMapper.insert(gift) > 0;
    }

    /* R */
    @Override
    public Page<Gift> search(Map<String, Object> params, int page, int size) {
        IPage<Gift> giftPage = new Page<>(page, size);
        giftPage = giftMapper.search(giftPage, params);
        return (Page<Gift>) giftPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Gift gift = giftMapper.selectById(id);
        if (gift == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Gift.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                ReflectionUtils.setField(classField, gift, value);
            }
        });

        int updated = giftMapper.updateById(gift);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        return giftMapper.deleteById(id) > 0;
    }
}
