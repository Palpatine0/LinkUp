package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.User;
import com.enchanted.mapper.OrderMapper;
import com.enchanted.entity.Order;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Page<Order> search(String keyword, int page, int size) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
            .eq("is_deleted", 0)
            .like("title", keyword)
            .orderByDesc("created_at");
        return getOrderPage(queryWrapper, page, size);
    }

    @Override
    public boolean save(Order order) {
        return orderMapper.insert(order) > 0;
    }

    @Override
    public Order get(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Page<Order> getAll(int page, int size) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
            .eq("is_deleted", 0)
            .orderByDesc("created_at");
        return getOrderPage(queryWrapper, page, size);
    }

    @Override
    public Page<Order> getAllByUserId(Long userId, int page, int size) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
            .eq("is_deleted", 0)
            .eq("client_id", userId)
            .orderByDesc("created_at");
        return getOrderPage(queryWrapper, page, size);
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(User.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, order, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, order, value);
                }
            }
        });

        int updated = orderMapper.updateById(order);
        return retBool(updated);
    }

    @Override
    public boolean delete(Long id) {
        return orderMapper.deleteById(id) > 0;
    }

    private Object convertValueToRequiredType(Object value, Class<?> targetType) {
        // Add more cases as needed
        if (targetType.equals(String.class) && value instanceof Integer) {
            return String.valueOf(value);
        }
        return value;
    }

    private Page<Order> getOrderPage(QueryWrapper<Order> queryWrapper, int page, int size) {
        Page<Order> orderPage = new Page<>(page, size);
        return this.page(orderPage, queryWrapper);
    }
}
