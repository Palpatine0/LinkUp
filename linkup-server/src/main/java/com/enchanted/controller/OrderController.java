package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Order;
import com.enchanted.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.vo.R;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("/search")
    public R search(@RequestParam("keyword") String keyword,
                    @RequestParam(defaultValue = "1") int page,
                    @RequestParam(defaultValue = "10") int size) {
        Page<Order> orderPage = orderService.search(keyword, page, size);
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

    @PostMapping("/get")
    public R get(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        Order order = orderService.get(id);
        if (order != null) {
            return R.ok().put("order", order);
        } else {
            return R.error("查找失败");
        }
    }

    @GetMapping("/get-all")
    public R getAll(@RequestParam(defaultValue = "1") int page,
                    @RequestParam(defaultValue = "10") int size) {
        Page<Order> orderPage = orderService.getAll(page, size);
        return buildPaginatedResponse(orderPage);
    }


    @PostMapping("/get-all-by-user-id")
    public R getAllByUserId(@RequestBody Map<String, Object> requestData) {
        Long userId = Long.parseLong(requestData.get("userId").toString());
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;
        Page<Order> orderPage = orderService.getAllByUserId(userId, page, size);
        return buildPaginatedResponse(orderPage);
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


    private R buildPaginatedResponse(Page<Order> orderPage) {
        return R.ok()
            .put("orderList", orderPage.getRecords())
            .put("total", orderPage.getTotal())
            .put("pages", orderPage.getPages())
            .put("current", orderPage.getCurrent());
    }
}
