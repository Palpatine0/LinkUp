package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Order;

import java.util.Map;

public interface IOrderService extends IService<Order> {

    /*C*/
    boolean save(Order order);

    /*R*/
    Page<Order> search(Map<String, Object> params, int page, int size);

    int getRemainingFreePostingQuota(Long userId);

    /*U*/
    boolean update(Long id, Map<String, Object> changes);

    boolean changeStatus(Long orderId, int newStatus);

    boolean rateOrder(Long orderId, Integer rating);

    void startOrderAssignmentMonitor(Long orderId);

    void stopOrderAssignmentMonitor(Long orderId);


    /*D*/
    boolean delete(Long id);

}
