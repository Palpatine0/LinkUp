package com.enchanted.controller;

import com.enchanted.entity.Order;
import com.enchanted.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.entity.R;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping("/save")
    public R save(@RequestBody Order order) {
        boolean isSaved = orderService.save(order);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @PostMapping("/find")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        Order order = orderService.find(id);
        if (order != null) {
            return R.ok().put("order", order);
        } else {
            return R.error("查找失败");
        }
    }

    // 3. Find all orders
    @GetMapping("/findAll")
    public R findAll() {
        List<Order> orders = orderService.findAll();
        return R.ok().put("orders", orders);
    }

    // 4. Update order
    @PutMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        Order order = orderService.find(id);

        if (order != null) {
            boolean isUpdated = orderService.update(order);
            if (isUpdated) {
                return R.ok("更新成功");
            }
        }
        return R.error("更新失败");
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
}
