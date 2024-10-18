package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Review;

import java.util.Map;

public interface IReviewService extends IService<Review> {

    /* C */
    boolean save(Review review);

    /* R */
    Page<Review> search(Map<String, Object> params, int page, int size);

    /* U */
    boolean update(Long id, Map<String, Object> changes);

    /* D */
    boolean delete(Long id);
}
