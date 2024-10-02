package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Transaction;

import java.util.Map;

public interface ITransactionService extends IService<Transaction> {

    /*C*/
    boolean save(Transaction transaction);

    /*R*/
    Page<Transaction> search(Map<String, Object> params, int page, int size);

    /*U*/
    boolean update(Long id, Map<String, Object> changes);

    /*D*/
    boolean delete(Long id);
}
