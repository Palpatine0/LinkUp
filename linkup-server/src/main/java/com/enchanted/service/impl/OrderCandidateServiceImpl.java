package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.entity.Order;
import com.enchanted.entity.OrderCandidate;
import com.enchanted.entity.User;
import com.enchanted.mapper.OrderCandidateMapper;
import com.enchanted.service.IOrderCandidateService;
import com.enchanted.service.IOrderService;
import com.enchanted.service.IUserService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderCandidateServiceImpl extends ServiceImpl<OrderCandidateMapper, OrderCandidate> implements IOrderCandidateService {

    @Autowired
    private OrderCandidateMapper orderCandidateMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    @Override
    public boolean save(OrderCandidate orderCandidate) {
        boolean isFirstPick = isFirstPickForOrder(orderCandidate.getOrderId());
        boolean isSaved = orderCandidateMapper.insert(orderCandidate) > 0;

        // If it's the first pick, start monitoring the order and set countdown_start_at
        if (isSaved && isFirstPick) {
            // Start the countdown
            Order order = orderService.getById(orderCandidate.getOrderId());
            if (order != null) {
                order.setStatus(0);
                Integer candidateCount = order.getCandidateCount();
                candidateCount++;
                order.setCandidateCount(candidateCount);
                order.setCountdownStartAt(new Date());
                orderService.updateById(order);
            }
            orderService.startOrderAssignmentMonitor(orderCandidate.getOrderId());
        }else {
            Order order = orderService.getById(orderCandidate.getOrderId());
            order.setStatus(0);
            Integer candidateCount = order.getCandidateCount();
            candidateCount++;
            order.setCandidateCount(candidateCount);
            orderService.updateById(order);
        }
        return isSaved;
    }


    private boolean isFirstPickForOrder(Long orderId) {
        QueryWrapper<OrderCandidate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("is_deleted", 0);
        return orderCandidateMapper.selectCount(queryWrapper) == 0;
    }


    @Override
    public Page<OrderCandidate> search(Map<String, Object> params, int page, int size) {
        QueryWrapper<OrderCandidate> queryWrapper = new QueryWrapper<>();

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

        Page<OrderCandidate> orderCandidatePage = new Page<>(page, size);
        return this.page(orderCandidatePage, queryWrapper);
    }

    @Override
    public Page<User> getAllServants(Long orderId, int page, int size) {
        // Fetch OrderCandidates for the given orderId
        QueryWrapper<OrderCandidate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<OrderCandidate> orderCandidates = orderCandidateMapper.selectList(queryWrapper);

        if (orderCandidates.isEmpty()) {
            return new Page<>();
        }

        // Extract servantIds from OrderCandidates
        List<Long> servantIds = orderCandidates.stream()
            .map(OrderCandidate::getServantId)
            .collect(Collectors.toList());

        // Fetch User entities using servantIds with pagination
        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", servantIds);  // Find users with the given servantIds

        // Perform paginated query for users
        return userService.page(userPage, userQueryWrapper);
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        OrderCandidate orderCandidate = orderCandidateMapper.selectById(id);
        if (orderCandidate == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(OrderCandidate.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, orderCandidate, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, orderCandidate, value);
                }
            }
        });

        int updated = orderCandidateMapper.updateById(orderCandidate);
        return retBool(updated);
    }

    @Override
    public boolean delete(Long id) {
        return orderCandidateMapper.deleteById(id) > 0;
    }
}
