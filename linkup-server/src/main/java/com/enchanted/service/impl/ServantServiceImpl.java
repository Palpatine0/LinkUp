package com.enchanted.service.impl;

import com.enchanted.mapper.ServantMapper;
import com.enchanted.entity.Servant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IServantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServantServiceImpl extends ServiceImpl<ServantMapper, Servant> implements IServantService {

    @Autowired
    private ServantMapper servantMapper;

    @Override
    public boolean save(Servant servant) {
        return servantMapper.insert(servant) > 0;
    }

    @Override
    public Servant find(Long id) {
        return servantMapper.selectById(id);
    }

    @Override
    public List<Servant> findAll() {
        return servantMapper.selectList(null); // Retrieve all servants
    }

    @Override
    public boolean update(Servant servant) {
        return servantMapper.updateById(servant) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return servantMapper.deleteById(id) > 0;
    }
}
