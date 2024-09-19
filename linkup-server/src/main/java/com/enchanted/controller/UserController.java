package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.User;
import com.enchanted.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.vo.R;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<User> userPage = userService.search(requestData, page, size);
        return buildPaginatedResponse(userPage);
    }

    @PostMapping("/search-servant")
    public R searchServant(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<User> userPage = userService.searchServant(requestData, page, size);
        return buildPaginatedResponse(userPage);
    }

    @PostMapping("save-auth-info")
    public R saveAuthInfo(@RequestBody Map<String, String> dto) {
        String code = dto.get("code");
        User userAuth = userService.saveAuthInfo(code);
        Map<String, Object> map = new HashMap<>();
        map.put("auth", userAuth);
        return R.ok(map);
    }

    @PostMapping("/save")
    public R save(@RequestBody User user) {
        boolean isSaved = userService.save(user);
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

        boolean isUpdated = userService.update(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }


    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = userService.delete(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    private R buildPaginatedResponse(Page<User> userPage) {
        return R.ok()
            .put("userList", userPage.getRecords())
            .put("total", userPage.getTotal())
            .put("pages", userPage.getPages())
            .put("current", userPage.getCurrent());
    }
}