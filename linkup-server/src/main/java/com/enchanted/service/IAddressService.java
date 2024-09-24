package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Address;

import java.util.Map;

public interface IAddressService extends IService<Address> {

    Page<Address> search(Map<String, Object> params, int page, int size);

    boolean save(Address address);

    boolean update(Long id, Map<String, Object> changes);

    boolean delete(Long id);
}
