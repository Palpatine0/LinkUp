package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enchanted.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
    IPage<Conversation> search(IPage<Conversation> page, @Param("params") Map<String, Object> params);
}
