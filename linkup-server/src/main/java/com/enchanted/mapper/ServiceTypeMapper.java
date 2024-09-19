package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.ServiceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ServiceTypeMapper extends BaseMapper<ServiceType> {

    IPage<ServiceType> search(IPage<ServiceType> page, @Param("params") Map<String, Object> params);
}
