package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Order;

import java.util.List;
import java.util.Map;

public interface IOrderService extends IService<Order> {

    Page<Order> search(Map<String, Object> params, int page, int size);

    boolean save(Order order);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);

    void monitorOrder(Long orderId);
}
