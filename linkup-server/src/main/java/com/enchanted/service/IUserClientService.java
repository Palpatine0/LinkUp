package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.UserClient;

import java.util.List;
import java.util.Map;

public interface IUserClientService extends IService<UserClient> {

    boolean saveUserClient(UserClient userClient);

    UserClient get(Long id);

    List<UserClient> getAll();

    boolean updateUserClient(Long id, Map<String, Object> changes);

    boolean deleteUserClient(Long id);
}
