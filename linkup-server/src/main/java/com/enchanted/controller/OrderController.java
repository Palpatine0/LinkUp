package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Order;
import com.enchanted.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.vo.R;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<Order> orderPage = orderService.search(requestData, page, size);
        return buildPaginatedResponse(orderPage);
    }

    @PostMapping("/save")
    public R save(@RequestBody Order order) {
        boolean isSaved = orderService.save(order);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

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

    @PostMapping("/cancel-order")
    public R cancelOrder(@RequestBody Map<String, Object> requestData) {
        Long orderId = Long.parseLong(requestData.get("orderId").toString());

        Order order = orderService.getById(orderId);
        if (order == null) {
            return R.error("Order not found");
        }

        // Stop monitoring the order and handle failure
        orderService.stopMonitoring(orderId); // Implement this method to stop the monitoring
        orderService.cancelOrder(order);

        return R.ok("Order failure handled successfully");
    }

    @PostMapping("/remaining-free-posting-quota")
    public R getRemainingFreePostingQuota(@RequestBody Map<String, Object> requestData) {
        Long userId = Long.parseLong(requestData.get("userId").toString());
        int freeOrderPostingQuota = orderService.getRemainingFreePostingQuota(userId);
        return R.ok().put("freeOrderPostingQuota", freeOrderPostingQuota);
    }

    private R buildPaginatedResponse(Page<Order> orderPage) {
        return R.ok()
            .put("orderList", orderPage.getRecords())
            .put("total", orderPage.getTotal())
            .put("pages", orderPage.getPages())
            .put("current", orderPage.getCurrent());
    }
}
