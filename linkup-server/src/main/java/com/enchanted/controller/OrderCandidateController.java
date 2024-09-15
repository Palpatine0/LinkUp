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
        return buildPaginatedResponse(orderCandidatePage);
    }

    @PostMapping("/get-servants-by-order-id")
    public R getServantsByOrderId(@RequestParam("orderId") Long orderId,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = orderCandidateService.getAllServantByOrderId(orderId, page, size);
        return R.ok()
            .put("servantList", userPage.getRecords())
            .put("total", userPage.getTotal())
            .put("pages", userPage.getPages())
            .put("current", userPage.getCurrent());
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

    private R buildPaginatedResponse(Page<OrderCandidate> orderCandidatePage) {
        return R.ok()
            .put("orderCandidateList", orderCandidatePage.getRecords())
            .put("total", orderCandidatePage.getTotal())
            .put("pages", orderCandidatePage.getPages())
            .put("current", orderCandidatePage.getCurrent());
    }
}
