package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.UserServant;
import com.enchanted.service.IUserServantService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user-servant")
public class UserServantController {

    @Autowired
    private IUserServantService userServantService;

    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<UserServant> userServantPage = userServantService.search(requestData, page, size);
        return buildPaginatedResponse(userServantPage);
    }

    @PostMapping("/save")
    public R save(@RequestBody UserServant userServant) {
        boolean isSaved = userServantService.save(userServant);
        return isSaved ? R.ok("添加成功") : R.error("添加失败");
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = userServantService.update(id, requestData);
        return isUpdated ? R.ok("更新成功") : R.error("更新失败");
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = userServantService.delete(id);
        return isDeleted ? R.ok("删除成功") : R.error("删除失败");
    }

    private R buildPaginatedResponse(Page<UserServant> userServantPage) {
        return R.ok()
            .put("userServantList", userServantPage.getRecords())
            .put("total", userServantPage.getTotal())
            .put("pages", userServantPage.getPages())
            .put("current", userServantPage.getCurrent());
    }
}
