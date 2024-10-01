package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.entity.UserClient;
import com.enchanted.mapper.UserClientMapper;
import com.enchanted.service.IUserClientService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class UserClientServiceImpl extends ServiceImpl<UserClientMapper, UserClient> implements IUserClientService {

    @Autowired
    private UserClientMapper userClientMapper;

    /*C*/
    @Override
    public boolean save(UserClient userClient) {
        return userClientMapper.insert(userClient) > 0;
    }

    /*R*/
    @Override
    public Page<UserClient> search(Map<String, Object> params, int page, int size) {
        QueryWrapper<UserClient> queryWrapper = new QueryWrapper<>();

        // Dynamically add conditions based on params
        params.forEach((key, value) -> {
            if (value != null && !value.toString().trim().isEmpty()) {
                if ("keyword".equals(key)) {
                    // Implement keyword search if applicable
                    // For example: queryWrapper.like("some_field", value.toString());
                } else {
                    queryWrapper.eq(key, value);
                }
            }
        });

        queryWrapper.orderByDesc("created_at"); // Adjust as per your requirements

        Page<UserClient> userClientPage = new Page<>(page, size);
        return this.page(userClientPage, queryWrapper);
    }

    /*U*/
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        UserClient userClient = userClientMapper.selectById(id);
        if (userClient == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(UserClient.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, userClient, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, userClient, value);
                }
            }
        });

        int updated = userClientMapper.updateById(userClient);
        return retBool(updated);
    }

    /*D*/
    @Override
    public boolean delete(Long id) {
        return userClientMapper.deleteById(id) > 0;
    }
}
