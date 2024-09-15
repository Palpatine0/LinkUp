package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.User;
import com.enchanted.mapper.ServantTypeMapper;
import com.enchanted.entity.ServantType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IServantTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class ServantTypeServiceImpl extends ServiceImpl<ServantTypeMapper, ServantType> implements IServantTypeService {

    @Autowired
    private ServantTypeMapper servantTypeMapper;

    @Override
    public Page<ServantType> search(Map<String, Object> params, int page, int size) {
        IPage<ServantType> servantTypePage = new Page<>(page, size);
        servantTypePage = servantTypeMapper.search(servantTypePage, params);
        return (Page<ServantType>) servantTypePage;
    }

    @Override
    public boolean save(ServantType servantType) {
        return servantTypeMapper.insert(servantType) > 0;
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        ServantType servantType = servantTypeMapper.selectById(id);
        if (servantType == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(User.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, servantType, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, servantType, value);
                }
            }
        });

        int updated = servantTypeMapper.updateById(servantType);
        return retBool(updated);
    }

    private Object convertValueToRequiredType(Object value, Class<?> targetType) {
        // Add more cases as needed
        if (targetType.equals(String.class) && value instanceof Integer) {
            return String.valueOf(value);
        }
        return value;
    }

    @Override
    public boolean delete(Long id) {
        return servantTypeMapper.deleteById(id) > 0;
    }
}
