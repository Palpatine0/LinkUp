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
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Integer.*;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IUserService userService;

    private final Map<Long, Thread> monitoringThreads = new ConcurrentHashMap<>();

    private static final Dotenv dotenv = Dotenv.load();

    private static int FREE_POSTING_QUOTA = parseInt(dotenv.get("ORDER_FREE_POSTING_QUOTA"));

    @Override
    public Page<Order> search(Map<String, Object> params, int page, int size) {
        IPage<Order> orderPage = new Page<>(page, size);
        orderPage = orderMapper.search(orderPage, params);
        return (Page<Order>) orderPage;
    }

    @Override
    public boolean save(Order order) {
        order.setIdentifier(generateOrderIdentifier());

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

    private String generateOrderIdentifier() {
        // Date format for the identifier
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(new Date());

        // Generate a random 6-digit number
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Ensures a 6-digit number

        // Combine all parts to create the identifier
        return "LK-ORD-" + currentTime + "-" + randomNumber;
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
        Thread monitoringThread = new Thread(() -> {
            try {
                Thread.sleep(600000); // Sleep for 10 minutes
                Order order = this.getById(orderId);
                if (order != null && order.getServantId() == null) {
                    cancelOrder(order);
                } else {
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        monitoringThreads.put(orderId, monitoringThread);
        monitoringThread.start();
    }


    @Override
    public void stopMonitoring(Long orderId) {
        Thread monitoringThread = monitoringThreads.get(orderId);
        if (monitoringThread != null && monitoringThread.isAlive()) {
            monitoringThread.interrupt(); // Interrupt the thread to stop monitoring
            monitoringThreads.remove(orderId); // Remove from active monitoring map
            System.out.println("Stopped monitoring order with ID: " + orderId);
        }
    }

    @Override
    public void cancelOrder(Order order) {
        // Fetch the user who posted the order
        User user = userService.getById(order.getClientId());
        if (user == null) {
            return;
        }

        // Check the number of free attempts used by the user today
        int freeAttemptsToday = countFreeAttemptsToday(order.getClientId());

        BigDecimal refundAmount = order.getPrice();

        if (freeAttemptsToday < FREE_POSTING_QUOTA + 1) {
            refundAmount = order.getPrice(); // 100% refund
        } else {
            refundAmount = order.getPrice().multiply(BigDecimal.valueOf(0.8)); // 80% refund
        }

        // Refund the user
        user.setBalance(user.getBalance().add(refundAmount));
        userService.updateById(user);

        // Mark the order as completed and failed
        order.setStatus(3);
        this.updateById(order);
    }

    public int getRemainingFreePostingQuota(Long userId) {
        int freeAttemptsToday = countFreeAttemptsToday(userId);
        return Math.max(0, FREE_POSTING_QUOTA - freeAttemptsToday);
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
        Long cnt = orderMapper.selectCount(queryWrapper);
        // Return the count of such orders
        return Math.toIntExact(cnt);
    }

    private Object convertValueToRequiredType(Object value, Class<?> targetType) {
        // Add more cases as needed
        if (targetType.equals(String.class) && value instanceof Integer) {
            return String.valueOf(value);
        }
        return value;
    }
}
