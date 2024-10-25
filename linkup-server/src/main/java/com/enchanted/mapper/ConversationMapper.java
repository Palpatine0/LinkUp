package com.enchanted.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enchanted.entity.Conversation;
import com.enchanted.entity.Message;
import com.enchanted.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
    IPage<Conversation> search(IPage<Conversation> page, @Param("params") Map<String, Object> params);

    List<Conversation> selectOverdueConversations(@Param("thresholdTime") Date thresholdTime);

    IPage<User> searchContacts(IPage<User> page, @Param("clientId") Long clientId, @Param("servantId") Long servantId);
}
