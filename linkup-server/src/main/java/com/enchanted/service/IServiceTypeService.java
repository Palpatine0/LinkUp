package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.ServiceType;

import java.util.Map;

public interface IServiceTypeService extends IService<ServiceType> {

    Page<ServiceType> search(Map<String, Object> params, int page, int size);

    boolean save(ServiceType serviceType);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
