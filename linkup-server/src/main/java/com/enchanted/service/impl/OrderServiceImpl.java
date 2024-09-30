package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.constant.OrderConstant;
import com.enchanted.entity.User;
import com.enchanted.mapper.OrderMapper;
import com.enchanted.entity.Order;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IOrderCandidateService;
import com.enchanted.service.IOrderService;
import com.enchanted.service.IUserService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private IUserService userService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IOrderCandidateService orderCandidateService;


    private final Map<Long, Thread> autoRefundMonitoringThreads = new ConcurrentHashMap<>();
    private final Map<Long, Thread> assignmentMonitoringThreads = new ConcurrentHashMap<>();
    private final Map<Long, Thread> autoRatingMonitoringThreads = new ConcurrentHashMap<>();


    /*C*/
    @Override
    public boolean save(Order order) {
        order.setIdentifier(generateOrderIdentifier());
        order.setStatus(OrderConstant.PENDING);
        // Get the user who posted the order
        User user = userService.getById(order.getClientId());
        if (user == null) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        // Payment processing
        if (OrderConstant.BALANCE.equals(order.getPaymentMethod())) { // Pay by balance
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
        boolean isSaved = orderMapper.insert(order) > 0;
        if (isSaved) {
            startAutoRefundMonitor(order.getId());
        }
        return isSaved;
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
    public void startAutoRefundMonitor(Long orderId) {
        Thread autoRefundThread = new Thread(() -> {
            try {
                // Wait for 30 minutes (30 * 60 * 1000 milliseconds)
                Thread.sleep(OrderConstant.AUTO_REFUND_MONITOR_DELAY);

                // Check if any candidates picked this order

                boolean hasCandidates = orderCandidateService.hasCandidatesForOrder(orderId);

                // If no candidate picked the order, auto-refund the order
                if (!hasCandidates) {
                    Order order = this.getById(orderId);
                    if (order != null && order.getServantId() == null) {
                        cancelOrder(order);  // No servant picked, full refund
                    }
                }

            } catch (InterruptedException e) {
                // Thread was interrupted (in case a servant was assigned before 30 minutes)
            } finally {
                autoRefundMonitoringThreads.remove(orderId);  // Clean up the thread from the map
            }
        });

        autoRefundMonitoringThreads.put(orderId, autoRefundThread);
        autoRefundThread.start();
    }

    @Override
    public void stopAutoRefundMonitor(Long orderId) {
        Thread autoRefundThread = autoRefundMonitoringThreads.get(orderId);
        if (autoRefundThread != null && autoRefundThread.isAlive()) {
            autoRefundThread.interrupt(); // Interrupt the thread to stop monitoring
            autoRefundMonitoringThreads.remove(orderId); // Remove from active monitoring map
        }
    }

    @Override
    public void startServantSelectionMonitor(Long orderId) {
        // Start a new thread to monitor the order assignment. Tf the order is not binding with a servant 10 minutes after, cancel it
        Thread monitoringThread = new Thread(() -> {
            try {
                Thread.sleep(OrderConstant.SERVANT_SELECTION_MONITOR_DELAY); // Sleep for 10 minutes
                Order order = this.getById(orderId);
                if (order != null && order.getServantId() == null) {
                    cancelOrder(order);
                } else {
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        assignmentMonitoringThreads.put(orderId, monitoringThread);
        monitoringThread.start();
    }

    @Override
    public void stopServantSelectionMonitor(Long orderId) {
        Thread monitoringThread = assignmentMonitoringThreads.get(orderId);
        if (monitoringThread != null && monitoringThread.isAlive()) {
            monitoringThread.interrupt(); // Interrupt the thread to stop monitoring
            assignmentMonitoringThreads.remove(orderId); // Remove from active monitoring map
            System.out.println("Stopped monitoring order with ID: " + orderId);
        }
    }


    /*R*/
    @Override
    public Page<Order> search(Map<String, Object> params, int page, int size) {
        IPage<Order> orderPage = new Page<>(page, size);
        orderPage = orderMapper.search(orderPage, params);
        return (Page<Order>) orderPage;
    }

    @Override
    public int getRemainingFreePostingQuota(Long userId) {
        int freeAttemptsToday = countFreeAttemptsToday(userId);
        return Math.max(0, OrderConstant.FREE_POSTING_QUOTA - freeAttemptsToday)+1;
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
        queryWrapper.isNotNull("countdown_start_at"); // No servant matched
        queryWrapper.between("created_at", startOfDay, endOfDay); // Only orders from today
        Long cnt = orderMapper.selectCount(queryWrapper);
        // Return the count of such orders
        return Math.toIntExact(cnt);
    }


    /*U*/
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
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
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
    @Transactional
    public boolean updateStatus(Long orderId, int newStatus) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }

        // Update the order status
        order.setStatus(newStatus);
        int updated = orderMapper.updateById(order);
        if (updated == 0) {
            return false;
        }

        // Perform actions based on the new status
        switch (newStatus) {
            case OrderConstant.COMPLETED:
                completeOrder(order);
                break;
            case OrderConstant.CANCELED:
                stopAutoRefundMonitor(order.getId());
                cancelOrder(order);
                break;
            case OrderConstant.PROCESSING:
                stopAutoRefundMonitor(order.getId());
                processingOrder(order);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Complete Order
     * Mark an order as completed
     *
     * @param order
     */
    public void completeOrder(Order order) {
        // Check if the order is already marked as completed
        if (order.getCompletedAt() != null) {
            return;
        }

        // Set the completion timestamp
        order.setCompletedAt(new Date());
        orderMapper.updateById(order);

        // Proceed with completion logic
        sendClientReferrerCommission(order);
        sendServantReferrerCommission(order);
        payServantInitialAmount(order);

        // Start monitoring for auto-rating
        monitorAutoRating(order.getId());
    }

    private void sendClientReferrerCommission(Order order) {
        // Fetch the client
        User client = userService.getById(order.getClientId());
        if (client == null || client.getReferrerId() == null) {
            return;
        }

        // Fetch the referrer
        User referrer = userService.getById(client.getReferrerId());
        if (referrer == null) {
            return;
        }

        // Calculate 5% of the order price
        BigDecimal commission = order.getPrice().multiply(OrderConstant.CLIENT_REFERRER_SHARE);

        // Update referrer's balance
        referrer.setBalance(referrer.getBalance().add(commission));
        userService.updateById(referrer);
    }

    private void sendServantReferrerCommission(Order order) {
        User servant = userService.getById(order.getServantId());
        if (servant == null || servant.getReferrerId() == null) {
            return;
        }

        // Fetch the referrer
        User referrer = userService.getById(servant.getReferrerId());
        if (referrer == null) {
            return;
        }

        // Calculate 5% of the order price
        BigDecimal commission = order.getPrice().multiply(OrderConstant.SERVANT_REFERRER_SHARE);

        // Update referrer's balance
        referrer.setBalance(referrer.getBalance().add(commission));
        userService.updateById(referrer);
    }

    private void payServantInitialAmount(Order order) {
        User servant = userService.getById(order.getServantId());
        if (servant == null) {
            return;
        }

        // Calculate the maximum servant share (70% of order price)
        BigDecimal maxServantShare = order.getPrice().multiply(OrderConstant.MAX_SERVANT_COMMISSION);

        // Calculate 80% of the servant's maximum share
        BigDecimal initialPayment = maxServantShare.multiply(OrderConstant.INITIAL_SERVANT_PAYMENT_RATE);

        // Update servant's balance
        servant.setBalance(servant.getBalance().add(initialPayment));
        userService.updateById(servant);

        // Store the remaining 20% for potential additional payment after rating
        order.setPendingServantPayment(maxServantShare.subtract(initialPayment));
        orderMapper.updateById(order);
    }


    /**
     * Cancel Order
     * The client should retake its money or partial of it after order canceled
     *
     * @param order
     */
    public void cancelOrder(Order order) {
        User user = userService.getById(order.getClientId());
        if (user == null) {
            return;
        }

        boolean hasCandidates = orderCandidateService.hasCandidatesForOrder(order.getId());
        BigDecimal refundAmount = order.getPrice();

        // If no servant picked the order, refund 100%
        if (!hasCandidates) {
            refundAmount = order.getPrice(); // Full refund
        } else {
            int freeAttemptsToday = countFreeAttemptsToday(order.getClientId());
            if (freeAttemptsToday < OrderConstant.FREE_POSTING_QUOTA + 1) {
                refundAmount = order.getPrice(); // 100% refund
            } else {
                refundAmount = order.getPrice().multiply(BigDecimal.valueOf(0.8)); // 80% refund
            }
        }

        // Refund the user
        user.setBalance(user.getBalance().add(refundAmount));
        userService.updateById(user);

        // Mark the order as canceled
        order.setStatus(OrderConstant.CANCELED);
        orderMapper.updateById(order);

        stopAutoRefundMonitor(order.getId());
    }

    /**
     * Processing Order
     * Mark an order as processing
     *
     * @param order
     */
    public void processingOrder(Order order) {
        order.setStatus(OrderConstant.PROCESSING);
        orderMapper.updateById(order);
    }


    @Override
    @Transactional
    public boolean rateOrder(Long orderId, Integer rating) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != OrderConstant.COMPLETED) {
            return false;
        }

        // Update the order's performance rating
        order.setPerformanceRating(rating);
        int updated = orderMapper.updateById(order);
        if (updated == 0) {
            return false;
        }

        // Adjust servant's payment based on the rating
        payServantPerformanceAmount(order, rating);

        // Stop the auto-rating monitor thread
        stopAutoRatingMonitor(orderId);

        return true;
    }

    private void payServantPerformanceAmount(Order order, Integer rating) {
        // Fetch the servant
        User servant = userService.getById(order.getServantId());
        if (servant == null) {
            return;
        }

        // Calculate the maximum servant share (70% of order price)
        BigDecimal maxServantShare = order.getPrice().multiply(OrderConstant.MAX_SERVANT_COMMISSION);

        // Determine additional payment based on rating
        BigDecimal additionalPercentage = BigDecimal.ZERO;
        if (rating == OrderConstant.LIMITED) {
            additionalPercentage = OrderConstant.ADDITIONAL_PAYMENT_RATE_LIMITED; // 0%
        } else if (rating == OrderConstant.FAIR) {
            additionalPercentage = OrderConstant.ADDITIONAL_PAYMENT_RATE_FAIR; // 10% of 70%
        } else if (rating == OrderConstant.GOOD) {
            additionalPercentage = OrderConstant.ADDITIONAL_PAYMENT_RATE_GOOD; // 20% of 70%
        }

        // Calculate the additional payment
        BigDecimal additionalPayment = maxServantShare.multiply(additionalPercentage);

        // Update servant's balance
        servant.setBalance(servant.getBalance().add(additionalPayment));
        userService.updateById(servant);

        // Update order's pending payment
        BigDecimal pendingPayment = order.getPendingServantPayment();
        if (pendingPayment == null) {
            pendingPayment = BigDecimal.ZERO;
        }
        pendingPayment = pendingPayment.subtract(additionalPayment);
        order.setPendingServantPayment(pendingPayment);
        orderMapper.updateById(order);
    }

    public void monitorAutoRating(Long orderId) {
        Thread autoRatingThread = new Thread(() -> {
            try {
                Thread.sleep(OrderConstant.AUTO_RATING_MONITOR_DELAY); // Sleep for 24 hours
                Order order = this.getById(orderId);
                if (order != null && order.getPerformanceRating() == null) {
                    rateOrder(orderId, OrderConstant.GOOD);
                }
            } catch (InterruptedException e) {
                // Thread was interrupted, do nothing
            } finally {
                // Clean up the thread from the map
                autoRatingMonitoringThreads.remove(orderId);
            }
        });

        autoRatingMonitoringThreads.put(orderId, autoRatingThread);
        autoRatingThread.start();
    }

    public void stopAutoRatingMonitor(Long orderId) {
        Thread autoRatingThread = autoRatingMonitoringThreads.get(orderId);
        if (autoRatingThread != null && autoRatingThread.isAlive()) {
            autoRatingThread.interrupt(); // Interrupt the thread to stop monitoring
            autoRatingMonitoringThreads.remove(orderId); // Remove from active monitoring map
        }
    }


    /*D*/
    @Override
    public boolean delete(Long id) {
        return orderMapper.deleteById(id) > 0;
    }


}
