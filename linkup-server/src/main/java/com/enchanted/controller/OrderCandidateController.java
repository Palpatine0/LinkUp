package com.enchanted.controller;

import com.enchanted.entity.OrderCandidate;
import com.enchanted.service.IOrderCandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.entity.R;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/orderCandidate")
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

    @PostMapping("/find")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        OrderCandidate orderCandidate = orderCandidateService.find(id);
        if (orderCandidate != null) {
            return R.ok().put("orderCandidate", orderCandidate);
        } else {
            return R.error("查找失败");
        }
    }

    @GetMapping("/findAll")
    public R findAll() {
        List<OrderCandidate> orderCandidates = orderCandidateService.findAll();
        return R.ok().put("orderCandidates", orderCandidates);
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
