package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.AlipayAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AlipayAccountMapper extends BaseMapper<AlipayAccount> {
    IPage<AlipayAccount> search(IPage<AlipayAccount> page, @Param("params") Map<String, Object> params);
}
