package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.User;

import java.util.Map;

public interface IUserService extends IService<User> {
    boolean save(User user);

    User saveAuthInfo(String code, int role);

    Page<User> search(Map<String, Object> params, int page, int size); // <-- Added search method

    Page<User> searchServant(Map<String, Object> params, int page, int size); // <-- Added search method

    Map getAuthInfo(String code);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);

}
