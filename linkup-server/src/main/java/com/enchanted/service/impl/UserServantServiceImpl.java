package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enchanted.entity.UserServant;
import com.enchanted.mapper.UserServantMapper;
import com.enchanted.service.IUserServantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class UserServantServiceImpl extends ServiceImpl<UserServantMapper, UserServant> implements IUserServantService {

    @Autowired
    private UserServantMapper userServantMapper;

    @Override
    public boolean saveUserServant(UserServant userServant) {
        int insert = userServantMapper.insert(userServant);
        return retBool(insert);
    }

    @Override
    public UserServant selectUserServant(Long id) {
        return userServantMapper.selectById(id);
    }

    @Override
    public List<UserServant> selectAllUserServants() {
        return userServantMapper.selectList(null);
    }

    @Override
    public boolean updateUserServant(Long id, Map<String, Object> changes) {
        UserServant userServant = userServantMapper.selectById(id);
        if (userServant == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(UserServant.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Handle type conversion if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, userServant, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, userServant, value);
                }
            }
        });

        int updated = userServantMapper.updateById(userServant);
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
    public boolean deleteUserServant(Long id) {
        int deleted = userServantMapper.deleteById(id);
        return retBool(deleted);
    }
}
