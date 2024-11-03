package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.entity.WithdrawalRequest;
import com.enchanted.mapper.WithdrawalRequestMapper;
import com.enchanted.service.IWithdrawalRequestService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class WithdrawalRequestServiceImpl extends ServiceImpl<WithdrawalRequestMapper, WithdrawalRequest> implements IWithdrawalRequestService {

    @Autowired
    private WithdrawalRequestMapper withdrawalRequestMapper;

    /* C */
    @Override
    public boolean save(WithdrawalRequest withdrawalRequest) {
        return withdrawalRequestMapper.insert(withdrawalRequest) > 0;
    }

    /* R */
    @Override
    public Page<WithdrawalRequest> search(Map<String, Object> params, int page, int size) {
        IPage<WithdrawalRequest> withdrawalRequestPage = new Page<>(page, size);
        withdrawalRequestPage = withdrawalRequestMapper.search(withdrawalRequestPage, params);
        return (Page<WithdrawalRequest>) withdrawalRequestPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        WithdrawalRequest withdrawalRequest = withdrawalRequestMapper.selectById(id);
        if (withdrawalRequest == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(WithdrawalRequest.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, withdrawalRequest, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, withdrawalRequest, value);
                }
            }
        });

        int updated = withdrawalRequestMapper.updateById(withdrawalRequest);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        WithdrawalRequest withdrawalRequest = withdrawalRequestMapper.selectById(id);
        if (withdrawalRequest == null) {
            return false;
        }
        withdrawalRequest.setIsDeleted(1);
        return withdrawalRequestMapper.updateById(withdrawalRequest) > 0;
    }
}
