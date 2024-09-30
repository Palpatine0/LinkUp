package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.OrderCandidate;
import com.enchanted.entity.User;

import java.util.Map;

public interface IOrderCandidateService extends IService<OrderCandidate> {

    /*C*/
    boolean save(OrderCandidate orderCandidate);

    /*R*/
    Page<OrderCandidate> search(Map<String, Object> params, int page, int size);

    Page<User> getAllServants(Long orderId, int page, int size);

    boolean hasCandidatesForOrder(Long orderId);

    /*U*/
    boolean update(Long id, Map<String, Object> changes);

    /*D*/
    boolean delete(Long id);
}
