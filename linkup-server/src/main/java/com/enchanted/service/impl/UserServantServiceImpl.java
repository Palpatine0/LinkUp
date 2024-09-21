package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.UserServant;
import com.enchanted.mapper.UserServantMapper;
import com.enchanted.service.IUserServantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.util.ConversionUtils;
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
    public Page<UserServant> search(Map<String, Object> params, int page, int size) {
        IPage<UserServant> userServantPage = new Page<>(page, size);
        userServantPage = userServantMapper.search(userServantPage, params);
        return (Page<UserServant>) userServantPage;
    }

    @Override
    public boolean save(UserServant userServant) {
        int insert = userServantMapper.insert(userServant);
        return retBool(insert);
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
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
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, userServant, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, userServant, value);
                }
            }
        });

        int updated = userServantMapper.updateById(userServant);
        return retBool(updated);
    }

    @Override
    public boolean delete(Long id) {
        int deleted = userServantMapper.deleteById(id);
        return retBool(deleted);
    }
}
