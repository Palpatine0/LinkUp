package com.enchanted.mapper;

import com.enchanted.entity.Servant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServantMapper extends BaseMapper<Servant> {
    // All CRUD methods (insert, selectById, selectList, updateById, deleteById) are provided by MyBatis Plus
}
