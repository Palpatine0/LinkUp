package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enchanted.entity.UserClient;
import com.enchanted.mapper.UserClientMapper;
import com.enchanted.service.IUserClientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class UserClientServiceImpl extends ServiceImpl<UserClientMapper, UserClient> implements IUserClientService {

    @Autowired
    private UserClientMapper userClientMapper;

    @Override
    public boolean saveUserClient(UserClient userClient) {
        int insert = userClientMapper.insert(userClient);
        return retBool(insert);
    }

    @Override
    public UserClient selectUserClient(Long id) {
        return userClientMapper.selectById(id);
    }

    @Override
    public List<UserClient> selectAllUserClients() {
        return userClientMapper.selectList(null);
    }

    @Override
    public boolean updateUserClient(Long id, Map<String, Object> changes) {
        UserClient userClient = userClientMapper.selectById(id);
        if (userClient == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(UserClient.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Handle type conversion if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, userClient, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, userClient, value);
                }
            }
        });

        int updated = userClientMapper.updateById(userClient);
        return retBool(updated);
    }

    private Object convertValueToRequiredType(Object value, Class<?> targetType) {
        // Implement conversion logic if needed
        if (targetType.equals(Boolean.class) && value instanceof Integer) {
            return ((Integer) value) != 0;
        }
        return value;
    }

    @Override
    public boolean deleteUserClient(Long id) {
        int deleted = userClientMapper.deleteById(id);
        return retBool(deleted);
    }
}
