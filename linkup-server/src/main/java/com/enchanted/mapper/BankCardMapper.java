package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.Bank;
import com.enchanted.entity.BankCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface BankCardMapper extends BaseMapper<BankCard> {
    IPage<BankCard> search(IPage<BankCard> page, @Param("params") Map<String, Object> params);
    IPage<Bank> searchBank(IPage<Bank> page, @Param("params") Map<String, Object> params);
}
