package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Client;

import java.util.List;
import java.util.Map;

public interface IClientService extends IService<Client> {

    boolean save(Client client);

    Client find(Long id);

    List<Client> findAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
