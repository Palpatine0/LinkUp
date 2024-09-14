package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.User;

import java.util.List;
import java.util.Map;

public interface IUserService extends IService<User> {

    User saveAuthInfo(String code);

    boolean save(User user);

    User get(Long id);

    List<User> getAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
