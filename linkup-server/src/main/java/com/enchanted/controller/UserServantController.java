package com.enchanted.controller;

import com.enchanted.entity.UserServant;
import com.enchanted.service.IUserServantService;
import com.enchanted.entity.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/userServant")
public class UserServantController {

    @Autowired
    private IUserServantService userServantService;

    @PostMapping("/save")
    public R save(@RequestBody UserServant userServant) {
        boolean isSaved = userServantService.saveUserServant(userServant);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @PostMapping("/select")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        UserServant userServant = userServantService.selectUserServant(id);
        if (userServant != null) {
            return R.ok().put("userServant", userServant);
        } else {
            return R.error("查找失败");
        }
    }

    @GetMapping("/selectAll")
    public R findAll() {
        return R.ok().put("userServantList", userServantService.selectAllUserServants());
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = userServantService.updateUserServant(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = userServantService.deleteUserServant(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}
