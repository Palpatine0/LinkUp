package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.ServantType;

import java.util.List;

public interface IServantTypeService extends IService<ServantType> {

    boolean save(ServantType servantType);

    ServantType find(Long id);

    List<ServantType> findAll();

    boolean update(ServantType servantType);

    boolean delete(Long id);
}
