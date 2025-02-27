package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Bank;
import com.enchanted.entity.BankCard;

import java.util.Map;

public interface IBankCardService extends IService<BankCard> {

    /* C */
    boolean save(BankCard bankCard);

    Map bankCardValidation(Long userId, String name, String idCardNumber, String bankCardNumber);

    /* R */
    Page<BankCard> search(Map<String, Object> params, int page, int size);

    Page<Bank> searchBank(Map<String, Object> params, int page, int size);

    /* U */
    boolean update(Long id, Map<String, Object> changes);

    /* D */
    boolean delete(Long id);
}
