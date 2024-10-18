package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Review;
import com.enchanted.mapper.ReviewMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements IReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    /* C */
    @Override
    public boolean save(Review review) {
        review.setIsDeleted(0); // Ensure isDeleted is set to 0 when creating
        return reviewMapper.insert(review) > 0;
    }

    /* R */
    @Override
    public Page<Review> search(Map<String, Object> params, int page, int size) {
        IPage<Review> reviewPage = new Page<>(page, size);
        reviewPage = reviewMapper.search(reviewPage, params);
        return (Page<Review>) reviewPage;
    }

    /* U */
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Review.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                ReflectionUtils.setField(classField, review, value);
            }
        });

        int updated = reviewMapper.updateById(review);
        return updated > 0;
    }

    /* D */
    @Override
    public boolean delete(Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null) {
            return false;
        }
        review.setIsDeleted(1);
        int updated = reviewMapper.updateById(review);
        return updated > 0;
    }
}
