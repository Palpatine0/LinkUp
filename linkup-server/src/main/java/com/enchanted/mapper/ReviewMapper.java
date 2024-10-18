package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    IPage<Review> search(IPage<Review> page, @Param("params") Map<String, Object> params);
}
