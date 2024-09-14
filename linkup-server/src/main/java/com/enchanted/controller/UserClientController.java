package com.enchanted.controller;

import com.enchanted.entity.UserClient;
import com.enchanted.service.IUserClientService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/userClient")
public class UserClientController {

    @Autowired
    private IUserClientService userClientService;

    @PostMapping("/save")
    public R save(@RequestBody UserClient userClient) {
        boolean isSaved = userClientService.saveUserClient(userClient);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @PostMapping("/select")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        UserClient userClient = userClientService.selectUserClient(id);
        if (userClient != null) {
            return R.ok().put("userClient", userClient);
        } else {
            return R.error("查找失败");
        }
    }

    @GetMapping("/selectAll")
    public R findAll() {
        return R.ok().put("userClientList", userClientService.selectAllUserClients());
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = userClientService.updateUserClient(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = userClientService.deleteUserClient(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}
