package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Client;

import java.util.List;

public interface IClientService extends IService<Client> {

    boolean save(Client client);

    Client find(Long id);

    List<Client> findAll();

    boolean update(Client client);

    boolean delete(Long id);
}
