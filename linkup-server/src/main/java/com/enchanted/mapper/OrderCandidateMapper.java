package com.enchanted.mapper;

import com.enchanted.entity.OrderCandidate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderCandidateMapper extends BaseMapper<OrderCandidate> {
    // MyBatis Plus provides all necessary CRUD methods via BaseMapper
}
