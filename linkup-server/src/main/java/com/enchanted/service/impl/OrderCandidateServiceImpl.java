package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import java.math.BigDecimal;
import java.util.ArrayList;
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
            orderService.startServantSelectionMonitor(orderCandidate.getOrderId());
        }else {
            Order order = orderService.getById(orderCandidate.getOrderId());
            order.setStatus(0);
            Integer candidateCount = order.getCandidateCount();
            candidateCount++;
            order.setCandidateCount(candidateCount);
            orderService.updateById(order);
        }

        if (isSaved) {
            orderService.stopAutoRefundMonitor(orderCandidate.getOrderId());
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
        IPage<OrderCandidate> orderPage = new Page<>(page, size);
        orderPage = orderCandidateMapper.search(orderPage, params);
        return (Page<OrderCandidate>) orderPage;
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

        // Extract servantIds and map them to their corresponding quoted prices
        Map<Long, BigDecimal> servantQuotedPriceMap = orderCandidates.stream()
            .collect(Collectors.toMap(OrderCandidate::getServantId, OrderCandidate::getQuotedPrice));

        // Fetch User entities using servantIds with pagination
        List<Long> servantIds = new ArrayList<>(servantQuotedPriceMap.keySet());
        Page<User> userPage = new Page<>(page, size);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", servantIds);  // Find users with the given servantIds

        // Perform paginated query for users
        Page<User> paginatedUsers = userService.page(userPage, userQueryWrapper);

        // Set the quotedPrice for each user
        paginatedUsers.getRecords().forEach(user -> {
            user.setQuotedPrice(servantQuotedPriceMap.get(user.getId()));
        });

        return paginatedUsers;
    }

    @Override
    public boolean hasCandidatesForOrder(Long orderId) {
        QueryWrapper<OrderCandidate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("is_deleted", 0);  // Assuming is_deleted is used to track soft deletions

        // Count the number of candidates for the order
        return orderCandidateMapper.selectCount(queryWrapper) > 0;
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
        OrderCandidate orderCandidate = orderCandidateMapper.selectById(id);
        orderCandidate.setIsDeleted(1);
        int updated = orderCandidateMapper.updateById(orderCandidate);
        return updated > 0;
    }
}
