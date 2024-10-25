package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.User;

import java.util.Map;

public interface IUserService extends IService<User> {
    /*C*/
    Map saveInfo(User user);

    /*R*/
    Page<User> search(Map<String, Object> params, int page, int size); // <-- Added search method

    Page<User> searchServant(Map<String, Object> params, int page, int size); // <-- Added search method

    Map getConfigInfo(String code,int role);

    Map referralCodeValidation(String referralCode,String role);

    /*U*/
    boolean update(Long id, Map<String, Object> changes);

    /*D*/
    boolean delete(Long id);

}
