package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Order;

import java.math.BigDecimal;
import java.util.Map;

public interface IOrderService extends IService<Order> {

    /*C*/
    boolean save(Order order);

    /*R*/
    Page<Order> search(Map<String, Object> params, int page, int size);

    int getRemainingFreePostingQuota(Long userId);

    Page<Order> getServantOrders(Long servantId, Integer statusType, int page, int size);

    /*U*/
    boolean update(Long id, Map<String, Object> changes);

    boolean updateStatus(Long orderId, int newStatus);

    boolean assignServant(Long orderId, Long servantId, BigDecimal quotedPrice);

    boolean rateServant(Long orderId, Integer rating);

    boolean reviewClient(Long orderId);

    /*D*/
    boolean delete(Long id);

}
