package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Order;
import com.enchanted.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.vo.R;

import java.math.BigDecimal;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;


    /*C*/
    @PostMapping("/save")
    public R save(@RequestBody Order order) {
        boolean isSaved = orderService.save(order);
        if (isSaved) {
            return R.ok("Success");
        } else {
            return R.error("Failed");
        }
    }

    /*R*/
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<Order> orderPage = orderService.search(requestData, page, size);
        return R.paginate(orderPage);
    }

    @PostMapping("/remaining-free-posting-quota")
    public R getRemainingFreePostingQuota(@RequestBody Map<String, Object> requestData) {
        Long userId = Long.parseLong(requestData.get("userId").toString());
        int freeOrderPostingQuota = orderService.getRemainingFreePostingQuota(userId);
        return R.ok().put("freeOrderPostingQuota", freeOrderPostingQuota);
    }

    /*U*/
    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = orderService.update(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }

    @PostMapping("/update-status")
    public R changeStatus(@RequestBody Map<String, Object> requestData) {
        Long orderId = Long.parseLong(requestData.get("orderId").toString());
        Integer newStatus = Integer.parseInt(requestData.get("status").toString());

        boolean isUpdated = orderService.updateStatus(orderId, newStatus);
        if (isUpdated) {
            return R.ok("Order status changed successfully");
        } else {
            return R.error("Failed to change order status");
        }
    }

    @PostMapping("/assign-servant")
    public R assignServant(@RequestBody Map<String, Object> requestData) {
        Long orderId = Long.parseLong(requestData.get("orderId").toString());
        Long servantId = Long.parseLong(requestData.get("servantId").toString());
        BigDecimal quotedPrice = BigDecimal.valueOf(Long.parseLong(requestData.get("quotedPrice").toString()));

        boolean isAssigned = orderService.assignServant(orderId, servantId,quotedPrice);
        if (isAssigned) {
            return R.ok("Servant assigned to order successfully");
        } else {
            return R.error("Failed to assign servant to order");
        }
    }


    @PostMapping("/rate-servant")
    public R rateServant(@RequestBody Map<String, Object> requestData) {
        Long orderId = Long.parseLong(requestData.get("orderId").toString());
        Integer rating = Integer.parseInt(requestData.get("rating").toString());

        boolean isRated = orderService.rateServant(orderId, rating);
        if (isRated) {
            return R.ok("Order rated successfully");
        } else {
            return R.error("Failed to rate order");
        }
    }

    @PostMapping("/review-client")
    public R reviewClient(@RequestBody Map<String, Object> requestData) {
        Long orderId = Long.parseLong(requestData.get("orderId").toString());

        boolean isRated = orderService.reviewClient(orderId);
        if (isRated) {
            return R.ok("Order rated successfully");
        } else {
            return R.error("Failed to rate order");
        }
    }


    /*D*/
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = orderService.delete(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}
