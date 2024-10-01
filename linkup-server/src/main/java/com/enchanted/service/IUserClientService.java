package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.UserClient;

import java.util.Map;

public interface IUserClientService extends IService<UserClient> {

    /*C*/
    boolean save(UserClient userClient);

    /*R*/
    Page<UserClient> search(Map<String, Object> params, int page, int size);

    /*U*/
    boolean update(Long id, Map<String, Object> changes);

    /*D*/
    boolean delete(Long id);
}
