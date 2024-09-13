package com.enchanted.controller;

import com.enchanted.entity.Client;
import com.enchanted.service.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.entity.R;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private IClientService clientService;

    // Save a new client (Accepting full Client entity)
    @PostMapping("/save")
    public R save(@RequestBody Client client) {
        boolean isSaved = clientService.save(client);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    // Find a client by ID (Accepting ID as part of a JSON request, without assuming it's a full Client entity)
    @PostMapping("/find")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());  // Extract the 'id' from request
        Client client = clientService.find(id);
        if (client != null) {
            return R.ok().put("client", client);
        } else {
            return R.error("查找失败");
        }
    }

    // Find all clients (No parameters required)
    @GetMapping("/findAll")
    public R findAll() {
        return R.ok().put("clients", clientService.findAll());
    }

    // Update client (Can accept partial data, for example just ID and some fields to update)
    @PutMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());  // Extract the 'id' from request
        Client client = clientService.find(id);

        if (client != null) {
            // Update only the fields passed in requestData, for example nickname and avatar
            if (requestData.containsKey("nickname")) {
                client.setNickname(requestData.get("nickname").toString());
            }
            if (requestData.containsKey("avatar")) {
                client.setAvatar(requestData.get("avatar").toString());
            }

            boolean isUpdated = clientService.update(client);
            if (isUpdated) {
                return R.ok("更新成功");
            }
        }
        return R.error("更新失败");
    }

    // Delete client by ID (Accepting just the ID in JSON)
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