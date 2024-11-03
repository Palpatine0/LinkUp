package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.WithdrawalRequest;

import java.util.Map;

public interface IWithdrawalRequestService extends IService<WithdrawalRequest> {

    /* C */
    boolean save(WithdrawalRequest withdrawalRequest);

    /* R */
    Page<WithdrawalRequest> search(Map<String, Object> params, int page, int size);

    /* U */
    boolean update(Long id, Map<String, Object> changes);

    /* D */
    boolean delete(Long id);
}
