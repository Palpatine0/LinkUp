package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.OrderCandidate;

import java.util.List;
import java.util.Map;

public interface IOrderCandidateService extends IService<OrderCandidate> {

    boolean save(OrderCandidate orderCandidate);

    OrderCandidate get(Long id);

    List<OrderCandidate> getAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
