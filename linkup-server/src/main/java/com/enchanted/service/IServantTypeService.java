package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.ServantType;

import java.util.List;
import java.util.Map;

public interface IServantTypeService extends IService<ServantType> {

    boolean save(ServantType servantType);

    ServantType find(Long id);

    List<ServantType> findAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}