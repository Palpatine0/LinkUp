package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.UserServant;

import java.util.List;
import java.util.Map;

public interface IUserServantService extends IService<UserServant> {

    boolean saveUserServant(UserServant userServant);

    UserServant selectUserServant(Long id);

    List<UserServant> selectAllUserServants();

    boolean updateUserServant(Long id, Map<String, Object> changes);

    boolean deleteUserServant(Long id);
}
