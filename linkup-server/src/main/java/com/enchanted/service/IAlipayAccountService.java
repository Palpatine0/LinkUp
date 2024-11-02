package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.AlipayAccount;

import java.util.Map;

public interface IAlipayAccountService extends IService<AlipayAccount> {

    /* C */
    boolean save(AlipayAccount alipayAccount);

    /* R */
    Page<AlipayAccount> search(Map<String, Object> params, int page, int size);

    /* U */
    boolean update(Long id, Map<String, Object> changes);

    /* D */
    boolean delete(Long id);
}
