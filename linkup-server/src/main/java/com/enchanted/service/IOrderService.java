package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Order;

import java.util.List;
import java.util.Map;

public interface IOrderService extends IService<Order> {

    boolean save(Order order);

    Order select(Long id);

    List<Order> selectAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
