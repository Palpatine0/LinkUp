package com.enchanted.service.impl;

import com.enchanted.entity.Client;
import com.enchanted.entity.Order;
import com.enchanted.mapper.ServantMapper;
import com.enchanted.entity.Servant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IServantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class ServantServiceImpl extends ServiceImpl<ServantMapper, Servant> implements IServantService {

    @Autowired
    private ServantMapper servantMapper;

    @Override
    public boolean save(Servant servant) {
        return servantMapper.insert(servant) > 0;
    }

    @Override
    public Servant select(Long id) {
        return servantMapper.selectById(id);
    }

    @Override
    public List<Servant> selectAll() {
        return servantMapper.selectList(null); // Retrieve all servants
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Servant servant = servantMapper.selectById(id);
        if (servant == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Client.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, servant, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, servant, value);
                }
            }
        });

        int updated = servantMapper.updateById(servant);
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
        return servantMapper.deleteById(id) > 0;
    }
}
