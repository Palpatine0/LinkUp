package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.User;
import com.enchanted.mapper.ServiceTypeMapper;
import com.enchanted.entity.ServiceType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IServiceTypeService;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class ServiceTypeServiceImpl extends ServiceImpl<ServiceTypeMapper, ServiceType> implements IServiceTypeService {

    @Autowired
    private ServiceTypeMapper serviceTypeMapper;

    @Override
    public Page<ServiceType> search(Map<String, Object> params, int page, int size) {
        IPage<ServiceType> serviceTypePage = new Page<>(page, size);
        serviceTypePage = serviceTypeMapper.search(serviceTypePage, params);
        return (Page<ServiceType>) serviceTypePage;
    }

    @Override
    public boolean save(ServiceType serviceType) {
        return serviceTypeMapper.insert(serviceType) > 0;
    }

    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        ServiceType serviceType = serviceTypeMapper.selectById(id);
        if (serviceType == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(User.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, serviceType, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, serviceType, value);
                }
            }
        });

        int updated = serviceTypeMapper.updateById(serviceType);
        return retBool(updated);
    }

    @Override
    public boolean delete(Long id) {
        ServiceType serviceType = serviceTypeMapper.selectById(id);
        serviceType.setIsDeleted(1);
        int updated = serviceTypeMapper.updateById(serviceType);
        return updated > 0;
    }
}
