package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.UserServant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserServantMapper extends BaseMapper<UserServant> {
    IPage<UserServant> search(IPage<UserServant> page, @Param("params") Map<String, Object> params);
}
