package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.UserServant;

import java.util.Map;

public interface IUserServantService extends IService<UserServant> {

    Page<UserServant> search(Map<String, Object> params, int page, int size);

    boolean save(UserServant userServant);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
