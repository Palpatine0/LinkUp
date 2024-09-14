package com.enchanted.controller;

import com.enchanted.entity.User;
import com.enchanted.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.entity.R;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/client")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("saveAuth")
    public R saveUserAuthInfo(@RequestBody Map<String, String> dto) {
        String code = dto.get("code");
        User userAuth = userService.saveUserAuthInfo(code);
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

    @PostMapping("/select")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());  // Extract the 'id' from request
        User user = userService.select(id);
        if (user != null) {
            return R.ok().put("client", user);
        } else {
            return R.error("查找失败");
        }
    }

    @GetMapping("/selectAll")
    public R findAll() {
        return R.ok().put("clientList", userService.selectAll());
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
}