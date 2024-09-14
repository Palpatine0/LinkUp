package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.UserServant;

import java.util.List;
import java.util.Map;

public interface IUserServantService extends IService<UserServant> {

    boolean saveUserServant(UserServant userServant);

    UserServant get(Long id);

    List<UserServant> getAll();

    boolean updateUserServant(Long id, Map<String, Object> changes);

    boolean deleteUserServant(Long id);
}
