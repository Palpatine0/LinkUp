package com.enchanted.controller;

import com.enchanted.entity.Client;
import com.enchanted.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.entity.R;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private IClientService clientService;

    @PostMapping("saveAuth")
    public R saveUserAuthInfo(@RequestBody Map<String, String> dto) {
        String code = dto.get("code");
        Client clientAuth = clientService.saveUserAuthInfo(code);
        Map<String, Object> map = new HashMap<>();
        map.put("auth", clientAuth);
        return R.ok(map);
    }

    @PostMapping("/save")
    public R save(@RequestBody Client client) {
        boolean isSaved = clientService.save(client);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @PostMapping("/select")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());  // Extract the 'id' from request
        Client client = clientService.select(id);
        if (client != null) {
            return R.ok().put("client", client);
        } else {
            return R.error("查找失败");
        }
    }

    @GetMapping("/selectAll")
    public R findAll() {
        return R.ok().put("clientList", clientService.selectAll());
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = clientService.update(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }


    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = clientService.delete(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}