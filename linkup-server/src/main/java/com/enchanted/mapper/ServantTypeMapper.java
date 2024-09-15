package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enchanted.entity.ServantType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ServantTypeMapper extends BaseMapper<ServantType> {

    IPage<ServantType> search(IPage<ServantType> page, @Param("params") Map<String, Object> params);
}
