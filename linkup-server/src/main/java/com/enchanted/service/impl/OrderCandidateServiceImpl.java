package com.enchanted.service.impl;

import com.enchanted.mapper.OrderCandidateMapper;
import com.enchanted.entity.OrderCandidate;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IOrderCandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderCandidateServiceImpl extends ServiceImpl<OrderCandidateMapper, OrderCandidate> implements IOrderCandidateService {

    @Autowired
    private OrderCandidateMapper orderCandidateMapper;

    @Override
    public boolean save(OrderCandidate orderCandidate) {
        return orderCandidateMapper.insert(orderCandidate) > 0;
    }

    @Override
    public OrderCandidate find(Long id) {
        return orderCandidateMapper.selectById(id);
    }

    @Override
    public List<OrderCandidate> findAll() {
        return orderCandidateMapper.selectList(null); // Retrieve all order candidates
    }

    @Override
    public boolean update(OrderCandidate orderCandidate) {
        return orderCandidateMapper.updateById(orderCandidate) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return orderCandidateMapper.deleteById(id) > 0;
    }
}
