package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.entity.AlipayAccount;
import com.enchanted.mapper.AlipayAccountMapper;
import com.enchanted.service.IAlipayAccountService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class AlipayAccountServiceImpl extends ServiceImpl<AlipayAccountMapper, AlipayAccount> implements IAlipayAccountService {

    @Autowired
    private AlipayAccountMapper alipayAccountMapper;

    /* C */
    @Override
    public boolean save(AlipayAccount alipayAccount) {
        QueryWrapper<AlipayAccount> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", alipayAccount.getUserId());
        wrapper.eq("is_deleted", 0);
        AlipayAccount existedAlipayAccount = alipayAccountMapper.selectOne(wrapper);
        if (existedAlipayAccount != null) {
            throw new IllegalArgumentException("Data Existed");
        }
        return alipayAccountMapper.insert(alipayAccount) > 0;
    }

    /* R */
    @Override
    public Page<AlipayAccount> search(Map<String, Object> params, int page, int size) {
        IPage<AlipayAccount> alipayAccountPage = new Page<>(page, size);
        return (Page<AlipayAccount>) alipayAccountMapper.search(alipayAccountPage, params);
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        AlipayAccount alipayAccount = alipayAccountMapper.selectById(id);
        if (alipayAccount == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(AlipayAccount.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                ReflectionUtils.setField(classField, alipayAccount, convertedValue);
            }
        });

        int updated = alipayAccountMapper.updateById(alipayAccount);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        AlipayAccount alipayAccount = alipayAccountMapper.selectById(id);
        alipayAccount.setIsDeleted(1);
        int updated = alipayAccountMapper.updateById(alipayAccount);
        return updated > 0;
    }
}
