package com.enchanted.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enchanted.entity.User;
import com.enchanted.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IUserService;
import com.enchanted.util.WeChatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User saveUserAuthInfo(String code) {
        User user = new User();

        JSONObject object = WeChatUtil.getOpenId(code);
        String openid = object.get("openid").toString();
        String sessionkey = object.get("session_key").toString();
        String unionId = "";
        String id = "";
        if (object.get("unionid") != null) {
            unionId = object.get("unionid").toString();
        }

        user.setOpenid(openid);
        user.setSessionKey(sessionkey);
        user.setUnionid(unionId);
        user.setCreatedAt(new Date());

        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("openid", openid));
        if (existingUser != null) {
            existingUser.setSessionKey(sessionkey);
            existingUser.setUnionid(unionId);
            userMapper.updateById(existingUser);
            user.setId(existingUser.getId());
        } else {
            userMapper.insert(user);
            user.setId(user.getId());
        }
        return user;
    }

    @Override
    public boolean save(User user) {
        int insert = userMapper.insert(user);
        return retBool(insert);
    }

    @Override
    public User select(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> selectAll() {
        return userMapper.selectList(null);
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(User.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, user, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, user, value);
                }
            }
        });

        int updated = userMapper.updateById(user);
        return retBool(updated);
    }

    private Object convertValueToRequiredType(Object value, Class<?> targetType) {
        // Add more cases as needed
        if (targetType.equals(String.class) && value instanceof Integer) {
            return String.valueOf(value);
        }
        return value;
    }


    @Override
    public boolean delete(Long id) {
        int deleted = userMapper.deleteById(id);
        return retBool(deleted);
    }
}
