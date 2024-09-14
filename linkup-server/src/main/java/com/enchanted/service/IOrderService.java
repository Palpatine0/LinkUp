package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Order;

import java.util.List;
import java.util.Map;

public interface IOrderService extends IService<Order> {

    Page<Order> search(String keyword, int page, int size);

    boolean save(Order order);

    Order get(Long id);

    Page<Order> getAll(int page, int size);

    Page<Order> getAllByUserId(Long userId, int page, int size);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
