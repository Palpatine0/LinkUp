package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.BankCard;
import com.enchanted.mapper.BankCardMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IBankCardService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class BankCardServiceImpl extends ServiceImpl<BankCardMapper, BankCard> implements IBankCardService {

    @Autowired
    private BankCardMapper bankCardMapper;

    /* C */
    @Override
    public boolean save(BankCard bankCard) {
        return bankCardMapper.insert(bankCard) > 0;
    }

    /* R */
    @Override
    public Page<BankCard> search(Map<String, Object> params, int page, int size) {
        IPage<BankCard> bankCardPage = new Page<>(page, size);
        bankCardPage = bankCardMapper.search(bankCardPage, params);
        return (Page<BankCard>) bankCardPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        BankCard bankCard = bankCardMapper.selectById(id);
        if (bankCard == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(BankCard.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, bankCard, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, bankCard, value);
                }
            }
        });

        int updated = bankCardMapper.updateById(bankCard);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        BankCard bankCard = bankCardMapper.selectById(id);
        bankCard.setIsDeleted(1);
        int updated = bankCardMapper.updateById(bankCard);
        return updated > 0;
    }
}
