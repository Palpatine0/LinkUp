package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.OrderCandidate;
import com.enchanted.entity.User;
import com.enchanted.service.IOrderCandidateService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/order-candidate")
public class OrderCandidateController {

    @Autowired
    private IOrderCandidateService orderCandidateService;

    @PostMapping("/save")
    public R save(@RequestBody OrderCandidate orderCandidate) {
        boolean isSaved = orderCandidateService.save(orderCandidate);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        // Remove pagination parameters from the map
        requestData.remove("page");
        requestData.remove("size");

        // Call the service search method
        Page<OrderCandidate> orderCandidatePage = orderCandidateService.search(requestData, page, size);
        return R.paginate(orderCandidatePage);
    }

    @PostMapping("/get-servants")
    public R getServantsBy(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;
        requestData.remove("page");
        requestData.remove("size");
        Long orderId = Long.parseLong(requestData.get("orderId").toString());
        // Fetch paginated list of users (servants) based on order ID
        Page<User> userPage = orderCandidateService.getAllServants(orderId, page, size);

        if (userPage.getRecords().isEmpty()) {
            return R.error("No servants found for the given order.");
        }

        return R.ok()
            .put("servantList", userPage.getRecords()) // List of servant users
            .put("total", userPage.getTotal())         // Total number of users
            .put("pages", userPage.getPages())         // Total pages
            .put("current", userPage.getCurrent());    // Current page
    }


    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = orderCandidateService.update(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = orderCandidateService.delete(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}
