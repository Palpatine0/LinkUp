package com.enchanted.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Servant;

import java.util.List;

public interface IServantService extends IService<Servant> {

    boolean save(Servant servant);

    Servant find(Long id);

    List<Servant> findAll();

    boolean update(Servant servant);

    boolean delete(Long id);
}
