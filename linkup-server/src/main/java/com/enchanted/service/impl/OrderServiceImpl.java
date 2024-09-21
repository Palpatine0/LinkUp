package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.User;
import com.enchanted.mapper.OrderMapper;
import com.enchanted.entity.Order;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IOrderService;
import com.enchanted.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IUserService userService;

    @Override
    public Page<Order> search(Map<String, Object> params, int page, int size) {
        IPage<Order> orderPage = new Page<>(page, size);
        orderPage = orderMapper.search(orderPage, params);
        return (Page<Order>) orderPage;
    }

    @Override
    public boolean save(Order order) {
        // Get the user who posted the order
        User user = userService.getById(order.getClientId());
        if (user == null) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        // Payment processing
        if ("1".equals(order.getPaymentMethod())) { // Pay by balance
            if (user.getBalance().compareTo(order.getPrice()) < 0) {
                throw new IllegalArgumentException("Insufficient balance");
            }
            user.setBalance(user.getBalance().subtract(order.getPrice()));
            userService.updateById(user); // Update user balance
        } else {
            // External payment method handling
            // Implement the payment gateway interaction here if necessary
        }

        // Save the order
        order.setCreatedAt(new Date());
        return orderMapper.insert(order) > 0;
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

    @Override
    public void monitorOrder(Long orderId) {
        // Start a new thread to monitor the order for 10 minutes
        new Thread(() -> {
            try {
                System.out.println("Monitoring order with ID: " + orderId + " has started.");

                // Sleep for 10 minutes (600,000 milliseconds) - use this in production
                Thread.sleep(600000);

                // For testing, you can use a shorter time (e.g., 1000 ms)
                // Thread.sleep(10000); // Change to 1000 for 1 second during testing

                // Re-fetch the order from the database to get the most recent state
                Order order = this.getById(orderId);
                if (order != null && order.getServantId() == null) {
                    // Order is considered failed if no servant has picked it within the time window
                    System.out.println("Order with ID: " + orderId + " failed due to no servant assignment.");
                    handleFailedOrder(order); // Implement refund logic here
                } else {
                    System.out.println("Order with ID: " + orderId + " has been assigned a servant or already completed.");
                }
            } catch (InterruptedException e) {
                System.err.println("Monitoring thread interrupted for order with ID: " + orderId);
                e.printStackTrace();
            }
        }).start();
    }


    private void handleFailedOrder(Order order) {
        // Fetch the user who posted the order
        User user = userService.getById(order.getClientId());
        if (user == null) {
            return;
        }

        // Check the number of free attempts used by the user today
        int freeAttemptsToday = countFreeAttemptsToday(order.getClientId());

        BigDecimal refundAmount = order.getPrice();
        if (freeAttemptsToday < 2) {
            refundAmount = order.getPrice(); // 100% refund
        } else if (freeAttemptsToday == 2) {
            refundAmount = order.getPrice().multiply(BigDecimal.valueOf(0.8)); // 80% refund
        }

        // Refund the user
        user.setBalance(user.getBalance().add(refundAmount));
        userService.updateById(user);

        // Mark the order as completed and failed
        order.setStatus(3);
        this.updateById(order);
    }

    private int countFreeAttemptsToday(Long userId) {
        // Get the start and end of the current day
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusSeconds(1);

        // Query the order table to count free attempts
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("client_id", userId);
        queryWrapper.isNull("servant_id"); // No servant matched
        queryWrapper.between("created_at", startOfDay, endOfDay); // Only orders from today
        queryWrapper.eq("is_completed", 1); // Ensure the order is completed (failed or succeeded)

        // Return the count of such orders
        return Math.toIntExact(orderMapper.selectCount(queryWrapper));
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
