package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Servant;

import java.util.List;
import java.util.Map;

public interface IServantService extends IService<Servant> {

    boolean save(Servant servant);

    Servant find(Long id);

    List<Servant> findAll();

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}