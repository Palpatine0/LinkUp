package com.enchanted.service.impl;

import com.enchanted.mapper.ClientMapper;
import com.enchanted.entity.Client;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements IClientService {

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public boolean save(Client client) {
        int insert = clientMapper.insert(client);
        return retBool(insert);
    }

    @Override
    public Client find(Long id) {
        return clientMapper.selectById(id);
    }

    @Override
    public List<Client> findAll() {
        return clientMapper.selectList(null);
    }

    @Override
    public boolean update(Client client) {
        int updated = clientMapper.updateById(client);
        return retBool(updated);
    }

    @Override
    public boolean delete(Long id) {
        int deleted = clientMapper.deleteById(id);
        return retBool(deleted);
    }
}
