package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Address;
import com.enchanted.mapper.AddressMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    @Autowired
    private AddressMapper addressMapper;

    /* C */
    @Override
    public boolean save(Address address) {
        return addressMapper.insert(address) > 0;
    }

    /* R */
    @Override
    public Page<Address> search(Map<String, Object> params, int page, int size) {
        IPage<Address> addressPage = new Page<>(page, size);
        addressPage = addressMapper.search(addressPage, params);
        return (Page<Address>) addressPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Address address = addressMapper.selectById(id);
        if (address == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Address.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                ReflectionUtils.setField(classField, address, value);
            }
        });

        int updated = addressMapper.updateById(address);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        Address address = addressMapper.selectById(id);
        address.setIsDeleted(1);
        int updated = addressMapper.updateById(address);
        return updated > 0;
    }
}
