package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enchanted.entity.WithdrawalRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface WithdrawalRequestMapper extends BaseMapper<WithdrawalRequest> {
    IPage<WithdrawalRequest> search(IPage<WithdrawalRequest> page, @Param("params") Map<String, Object> params);
}
