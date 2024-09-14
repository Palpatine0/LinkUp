package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.User;

import java.util.List;
import java.util.Map;

public interface IUserService extends IService<User> {

    User saveUserAuthInfo(String code);

    boolean save(User user);

    User select(Long id);

    List<User> selectAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
