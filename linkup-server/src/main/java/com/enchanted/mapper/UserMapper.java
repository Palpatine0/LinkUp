package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    IPage<User> search(IPage<User> page, @Param("params") Map<String, Object> params);

    IPage<User> searchServant(IPage<User> page, @Param("params") Map<String, Object> params);
}
