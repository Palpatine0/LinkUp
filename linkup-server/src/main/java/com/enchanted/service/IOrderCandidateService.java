package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.OrderCandidate;

import java.util.List;

public interface IOrderCandidateService extends IService<OrderCandidate> {

    boolean save(OrderCandidate orderCandidate);

    OrderCandidate find(Long id);

    List<OrderCandidate> findAll();

    boolean update(OrderCandidate orderCandidate);

    boolean delete(Long id);
}
