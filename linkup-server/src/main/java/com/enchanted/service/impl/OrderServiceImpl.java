package com.enchanted.service.impl;

import com.enchanted.mapper.OrderMapper;
import com.enchanted.entity.Order;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public boolean save(Order order) {
        return orderMapper.insert(order) > 0;
    }

    @Override
    public Order find(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public List<Order> findAll() {
        return orderMapper.selectList(null); // Retrieve all orders
    }

    @Override
    public boolean update(Order order) {
        return orderMapper.updateById(order) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return orderMapper.deleteById(id) > 0;
    }
}
