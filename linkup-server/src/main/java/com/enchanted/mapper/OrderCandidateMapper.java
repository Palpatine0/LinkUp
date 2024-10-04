package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.Order;
import com.enchanted.entity.OrderCandidate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface OrderCandidateMapper extends BaseMapper<OrderCandidate> {
    IPage<OrderCandidate> search(IPage<OrderCandidate> page, @Param("params") Map<String, Object> params);
}
