package com.enchanted.service.impl;

import com.enchanted.mapper.ServantTypeMapper;
import com.enchanted.entity.ServantType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IServantTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServantTypeServiceImpl extends ServiceImpl<ServantTypeMapper, ServantType> implements IServantTypeService {

    @Autowired
    private ServantTypeMapper servantTypeMapper;

    @Override
    public boolean save(ServantType servantType) {
        return servantTypeMapper.insert(servantType) > 0;
    }

    @Override
    public ServantType find(Long id) {
        return servantTypeMapper.selectById(id);
    }

    @Override
    public List<ServantType> findAll() {
        return servantTypeMapper.selectList(null);
    }

    @Override
    public boolean update(ServantType servantType) {
        return servantTypeMapper.updateById(servantType) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return servantTypeMapper.deleteById(id) > 0;
    }
}
