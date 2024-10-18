package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Review;
import com.enchanted.service.IReviewService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    /* C */
    @PostMapping("/save")
    public R save(@RequestBody Review review) {
        boolean isSaved = reviewService.save(review);
        if (isSaved) {
            return R.ok("Review added successfully");
        } else {
            return R.error("Failed to add review");
        }
    }

    /* R */
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<Review> reviewPage = reviewService.search(requestData, page, size);
        return R.paginate(reviewPage);
    }

    /* U */
    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = reviewService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Review updated successfully");
        } else {
            return R.error("Failed to update review");
        }
    }

    /* D */
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = reviewService.delete(id);
        if (isDeleted) {
            return R.ok("Review deleted successfully");
        } else {
            return R.error("Failed to delete review");
        }
    }
}
