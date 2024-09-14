package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Client;

import java.util.List;
import java.util.Map;

public interface IClientService extends IService<Client> {

    Client saveUserAuthInfo(String code);

    boolean save(Client client);

    Client select(Long id);

    List<Client> selectAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
