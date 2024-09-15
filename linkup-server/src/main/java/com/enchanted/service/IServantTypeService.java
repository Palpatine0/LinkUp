package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.ServantType;

import java.util.List;
import java.util.Map;

public interface IServantTypeService extends IService<ServantType> {

    Page<ServantType> search(Map<String, Object> params, int page, int size);

    boolean save(ServantType servantType);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
