package com.enchanted.service.impl;

import com.enchanted.mapper.ClientMapper;
import com.enchanted.entity.Client;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements IClientService {

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public boolean save(Client client) {
        int insert = clientMapper.insert(client);
        return retBool(insert);
    }

    @Override
    public Client find(Long id) {
        return clientMapper.selectById(id);
    }

    @Override
    public List<Client> findAll() {
        return clientMapper.selectList(null);
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Client client = clientMapper.selectById(id);
        if (client == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Client.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, client, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, client, value);
                }
            }
        });

        int updated = clientMapper.updateById(client);
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
        int deleted = clientMapper.deleteById(id);
        return retBool(deleted);
    }
}
