package com.enchanted.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.constant.OrderConstant;
import com.enchanted.constant.TransactionConstant;
import com.enchanted.entity.Conversation;
import com.enchanted.entity.Transaction;
import com.enchanted.entity.User;
import com.enchanted.mapper.ConversationMapper;
import com.enchanted.mapper.OrderMapper;
import com.enchanted.entity.Order;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IOrderCandidateService;
import com.enchanted.service.IOrderService;
import com.enchanted.service.ITransactionService;
import com.enchanted.service.IUserService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
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
import java.util.concurrent.ScheduledFuture;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private IUserService userService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IOrderCandidateService orderCandidateService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private TaskScheduler taskScheduler;

    // Maps to keep track of scheduled tasks
    private final Map<Long, ScheduledFuture<?>> autoRefundTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> autoSelectionRefundTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> autoRatingTasks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> autoCompletionTasks = new ConcurrentHashMap<>();


    /*C*/
    @Override
    public boolean save(Order order) {
        order.setCreatedAt(new Date());
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
            userService.updateById(user);
        } else {
            // External payment method handling
            // Implement the payment gateway interaction here if necessary
        }

        boolean isSaved = orderMapper.insert(order) > 0;
        if (StringUtils.equals(order.getPaymentMethod(), OrderConstant.BALANCE)) {
            // Record the transaction
            Transaction transaction = new Transaction();
            transaction.setCurrencyType(Integer.valueOf(OrderConstant.BALANCE));
            transaction.setUserId(user.getId());
            transaction.setOrderId(order.getId());
            transaction.setAmount(order.getPrice().negate());
            transaction.setBalanceAfter(user.getBalance());
            transaction.setTransactionType(TransactionConstant.DEDUCTION);
            transaction.setDescription(TransactionConstant.CLIENT_ORDER_PAYMENT);
            transaction.setDescriptionCn(TransactionConstant.CLIENT_ORDER_PAYMENT_CN);
            transactionService.save(transaction);
        }
        startAutoRefundMonitor(order.getId());
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
        Runnable task = () -> {
            // Check if any candidates picked this order
            boolean hasCandidates = orderCandidateService.hasCandidatesForOrder(orderId);

            // If no candidate picked the order, auto-refund the order
            if (!hasCandidates) {
                Order order = this.getById(orderId);
                if (order != null && order.getServantId() == null) {
                    cancelOrder(order);  // No servant picked, full refund
                }
            }

            // Remove the task from the map after execution
            autoRefundTasks.remove(orderId);
        };

        // Schedule the task with the delay from constants
        ScheduledFuture<?> future = taskScheduler.schedule(
            task,
            new Date(System.currentTimeMillis() + OrderConstant.AUTO_REFUND_MONITOR_DELAY)
        );

        autoRefundTasks.put(orderId, future);
    }

    @Override
    public void stopAutoRefundMonitor(Long orderId) {
        ScheduledFuture<?> future = autoRefundTasks.get(orderId);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            autoRefundTasks.remove(orderId);
        }
    }

    @Override
    public void startServantSelectionMonitor(Long orderId) {
        Runnable task = () -> {
            Order order = this.getById(orderId);
            if (order != null && order.getServantId() == null) {
                cancelOrder(order);
            }

            // Remove the task from the map after execution
            autoSelectionRefundTasks.remove(orderId);
        };

        // Schedule the task with the delay from constants
        ScheduledFuture<?> future = taskScheduler.schedule(
            task,
            new Date(System.currentTimeMillis() + OrderConstant.SERVANT_SELECTION_MONITOR_DELAY)
        );

        autoSelectionRefundTasks.put(orderId, future);
    }

    @Override
    public void stopServantSelectionMonitor(Long orderId) {
        ScheduledFuture<?> future = autoSelectionRefundTasks.get(orderId);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            autoSelectionRefundTasks.remove(orderId);
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
        int quota = Math.max(0, OrderConstant.FREE_POSTING_QUOTA - freeAttemptsToday);
        return quota;
    }

    private int countFreeAttemptsToday(Long userId) {
        // Get the start and end of the current day
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusSeconds(1);

        // Query the order table to count free attempts
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("client_id", userId);
        queryWrapper.eq("status", OrderConstant.CANCELED);
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
            Field classField = ReflectionUtils.findField(Order.class, field);
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
                cancelOrder(order);
                break;
            case OrderConstant.PROCESSING:
                processingOrder(order);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean assignServant(Long orderId, Long servantId, BigDecimal quotedPrice) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }

        // Check if the order is in a state that allows assignment
        if (order.getStatus() != OrderConstant.PENDING) {
            return false;
        }

        // Assign the servant to the order
        order.setServantId(servantId);
        order.setPrice(quotedPrice);

        // Update the order status to PROCESSING
        boolean statusUpdated = updateStatus(orderId, OrderConstant.PROCESSING);
        if (!statusUpdated) {
            return false;
        }

        // Save the updated order with the servantId
        int updated = orderMapper.updateById(order);
        if (updated > 0) {
            startOrderCompletionMonitor(orderId, order.getServiceScheduleEnd());
        }

        return updated > 0;
    }

    public void startOrderCompletionMonitor(Long orderId, Date serviceScheduleEnd) {
        Runnable task = () -> {
            Order order = this.getById(orderId);
            if (order != null && order.getStatus() == OrderConstant.PROCESSING) {
                updateStatus(orderId, OrderConstant.COMPLETED);
            }
            autoCompletionTasks.remove(orderId);
        };
        ScheduledFuture<?> future = taskScheduler.schedule(task, serviceScheduleEnd);
        autoCompletionTasks.put(orderId, future);
    }

    public void stopOrderCompletionMonitor(Long orderId) {
        ScheduledFuture<?> future = autoCompletionTasks.get(orderId);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            autoCompletionTasks.remove(orderId);
        }
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

        // monitor control
        stopAutoRefundMonitor(order.getId());
        startAutoRatingMonitor(order.getId());
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

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setCurrencyType(Integer.valueOf(OrderConstant.BALANCE));
        transaction.setUserId(referrer.getId());
        transaction.setOrderId(order.getId());
        transaction.setAmount(commission);
        transaction.setBalanceAfter(referrer.getBalance());
        transaction.setTransactionType(TransactionConstant.ADDITION);
        transaction.setDescription(TransactionConstant.CLIENT_REFERRER_COMMISSION);
        transaction.setDescriptionCn(TransactionConstant.CLIENT_REFERRER_COMMISSION_CN);
        transactionService.save(transaction);
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

        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setCurrencyType(Integer.valueOf(OrderConstant.BALANCE));
        transaction.setUserId(referrer.getId());
        transaction.setOrderId(order.getId());
        transaction.setAmount(commission);
        transaction.setBalanceAfter(referrer.getBalance());
        transaction.setTransactionType(TransactionConstant.ADDITION);
        transaction.setDescription(TransactionConstant.SERVANT_REFERRER_COMMISSION);
        transaction.setDescriptionCn(TransactionConstant.SERVANT_REFERRER_COMMISSION_CN);
        transactionService.save(transaction);
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

        Transaction transaction = new Transaction();
        transaction.setCurrencyType(Integer.valueOf(OrderConstant.BALANCE));
        transaction.setUserId(servant.getId());
        transaction.setOrderId(order.getId());
        transaction.setAmount(initialPayment);
        transaction.setBalanceAfter(servant.getBalance());
        transaction.setTransactionType(TransactionConstant.ADDITION);
        transaction.setDescription(TransactionConstant.SERVANT_INITIAL_PAYMENT);
        transaction.setDescriptionCn(TransactionConstant.SERVANT_INITIAL_PAYMENT_CN);
        transactionService.save(transaction);
    }

    /**
     * Cancel Order
     * The client should retake its money or partial of it after order canceled
     *
     * @param order
     */
    @Async("taskExecutor")
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
        order.setCanceledAt(new Date());
        orderMapper.updateById(order);

        stopAutoRefundMonitor(order.getId());

        // Store the transaction
        Transaction transaction = new Transaction();
        transaction.setCurrencyType(Integer.valueOf(OrderConstant.BALANCE));
        transaction.setUserId(user.getId());
        transaction.setOrderId(order.getId());
        transaction.setAmount(refundAmount);
        transaction.setBalanceAfter(user.getBalance());
        transaction.setTransactionType(TransactionConstant.ADDITION);
        transaction.setDescription(TransactionConstant.CLIENT_CANCEL_ORDER_REFUND);
        transaction.setDescriptionCn(TransactionConstant.CLIENT_CANCEL_ORDER_REFUND_CN);
        transactionService.save(transaction);
    }

    /**
     * Processing Order
     * Mark an order as processing
     *
     * @param order
     */
    public void processingOrder(Order order) {
        stopAutoRefundMonitor(order.getId());
        QueryWrapper<Conversation> wrapper = new QueryWrapper<>();
        wrapper.eq("client_id", order.getClientId());
        wrapper.eq("servant_id", order.getServantId());
        wrapper.eq("is_deleted", 0);
        Conversation conversation = conversationMapper.selectOne(wrapper);

        if (conversation==null){
            Conversation newConversation = new Conversation();
            newConversation.setClientId(order.getClientId());
            newConversation.setServantId(order.getServantId());
            newConversation.setExpirationTime(order.getServiceScheduleEnd());
            conversationMapper.insert(newConversation);
        }else{
            conversation.setExpirationTime(order.getServiceScheduleEnd());
            conversationMapper.updateById(conversation);
        }


        order.setStatus(OrderConstant.PROCESSING);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public boolean rate(Long orderId, Integer rating) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != OrderConstant.COMPLETED) {
            return false;
        }
        if (order.getPerformanceRating() != null) {
            throw new IllegalArgumentException("Rated");
        }

        // Update the order's performance rating
        order.setPerformanceRating(rating);
        int updated = orderMapper.updateById(order);
        if (updated == 0) {
            return false;
        }

        // Adjust servant's payment based on the rating
        payServantPerformanceAmount(order, rating);

        // Stop the auto-rating monitor task
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

        // Record the transaction
        if (additionalPayment.compareTo(BigDecimal.ZERO) != 0) {
            Transaction transaction = new Transaction();
            transaction.setCurrencyType(Integer.valueOf(OrderConstant.BALANCE));
            transaction.setUserId(servant.getId());
            transaction.setOrderId(order.getId());
            transaction.setAmount(additionalPayment);
            transaction.setBalanceAfter(servant.getBalance());
            transaction.setTransactionType(TransactionConstant.ADDITION);
            transaction.setDescription(TransactionConstant.SERVANT_PERFORMANCE_PAYMENT);
            transaction.setDescriptionCn(TransactionConstant.SERVANT_PERFORMANCE_PAYMENT_CN);
            transactionService.save(transaction);
        }
    }

    public void startAutoRatingMonitor(Long orderId) {

        Runnable task = () -> {
            Order order = this.getById(orderId);
            if (order != null && order.getPerformanceRating() == null) {
                rate(orderId, OrderConstant.GOOD);
            }
            // Remove the task from the map after execution
            autoRatingTasks.remove(orderId);
        };

        // Schedule the task with the delay from constants
        ScheduledFuture<?> future = taskScheduler.schedule(
            task,
            new Date(System.currentTimeMillis() + OrderConstant.AUTO_RATING_MONITOR_DELAY)
        );

        autoRatingTasks.put(orderId, future);
    }

    public void stopAutoRatingMonitor(Long orderId) {
        ScheduledFuture<?> future = autoRatingTasks.get(orderId);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            autoRatingTasks.remove(orderId);
        }
    }

    /*D*/
    @Override
    public boolean delete(Long id) {
        return orderMapper.deleteById(id) > 0;
    }

}
