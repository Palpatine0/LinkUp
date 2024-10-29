package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    IPage<Order> search(IPage<Order> page, @Param("params") Map<String, Object> params);

    IPage<Order> getServantOrders(IPage<Order> page, @Param("params") Map<String, Object> params);
}
