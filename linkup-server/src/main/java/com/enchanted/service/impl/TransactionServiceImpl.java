package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Transaction;
import com.enchanted.mapper.TransactionMapper;
import com.enchanted.service.ITransactionService;
import com.enchanted.util.ConversionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements ITransactionService {

    @Autowired
    private TransactionMapper transactionMapper;

    /*C*/
    @Override
    public boolean save(Transaction transaction) {
        transaction.setCreatedAt(new java.util.Date());
        transaction.setIdentifier(generateTransactionIdentifier());
        int inserted = transactionMapper.insert(transaction);
        return retBool(inserted);
    }

    private String generateTransactionIdentifier() {
        // Date format for the identifier
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(new Date());

        // Generate a random 6-digit number
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // Ensures a 6-digit number

        // Combine all parts to create the identifier
        return "LK-TXN-" + currentTime + "-" + randomNumber;
    }

    /*R*/
    @Override
    public Page<Transaction> search(Map<String, Object> params, int page, int size) {
        IPage<Transaction> transactionPage = new Page<>(page, size);
        transactionPage = transactionMapper.search(transactionPage, params);
        return (Page<Transaction>) transactionPage;
    }

    /*U*/
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Transaction transaction = transactionMapper.selectById(id);
        if (transaction == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Transaction.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, transaction, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, transaction, value);
                }
            }
        });

        int updated = transactionMapper.updateById(transaction);
        return retBool(updated);
    }

    /*D*/
    @Override
    public boolean delete(Long id) {
        return transactionMapper.deleteById(id) > 0;
    }
}
