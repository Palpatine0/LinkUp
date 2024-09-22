package com.enchanted.mapper;

import com.enchanted.entity.ServantType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServantTypeMapper extends BaseMapper<ServantType> {
    // MyBatis Plus provides all necessary CRUD methods via BaseMapper
}
