package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.UserClient;

import java.util.Map;

public interface IUserClientService extends IService<UserClient> {

    boolean save(UserClient userClient);

    Page<UserClient> search(Map<String, Object> params, int page, int size);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
