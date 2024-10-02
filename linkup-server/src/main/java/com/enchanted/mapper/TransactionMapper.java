package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.Transaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
    IPage<Transaction> search(IPage<Transaction> page, @Param("params") Map<String, Object> params);
}
