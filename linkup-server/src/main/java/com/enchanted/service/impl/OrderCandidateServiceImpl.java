package com.enchanted.service.impl;

import com.enchanted.entity.Client;
import com.enchanted.mapper.OrderCandidateMapper;
import com.enchanted.entity.OrderCandidate;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IOrderCandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class OrderCandidateServiceImpl extends ServiceImpl<OrderCandidateMapper, OrderCandidate> implements IOrderCandidateService {

    @Autowired
    private OrderCandidateMapper orderCandidateMapper;

    @Override
    public boolean save(OrderCandidate orderCandidate) {
        return orderCandidateMapper.insert(orderCandidate) > 0;
    }

    @Override
    public OrderCandidate select(Long id) {
        return orderCandidateMapper.selectById(id);
    }

    @Override
    public List<OrderCandidate> selectAll() {
        return orderCandidateMapper.selectList(null); // Retrieve all order candidates
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        OrderCandidate orderCandidate = orderCandidateMapper.selectById(id);
        if (orderCandidate == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Client.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, orderCandidate, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, orderCandidate, value);
                }
            }
        });

        int updated = orderCandidateMapper.updateById(orderCandidate);
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
        return orderCandidateMapper.deleteById(id) > 0;
    }
}
