package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.Gift;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface GiftMapper extends BaseMapper<Gift> {
    IPage<Gift> search(IPage<Gift> page, @Param("params") Map<String, Object> params);
}
