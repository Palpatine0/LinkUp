package com.enchanted.controller;

import com.enchanted.entity.Servant;
import com.enchanted.service.IServantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.entity.R;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/servant")
public class ServantController {

    @Autowired
    private IServantService servantService;

    // 1. Save a new servant
    @PostMapping("/save")
    public R save(@RequestBody Servant servant) {
        boolean isSaved = servantService.save(servant);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    // 2. Find a servant by ID
    @PostMapping("/find")
    public R find(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        Servant servant = servantService.find(id);
        if (servant != null) {
            return R.ok().put("servant", servant);
        } else {
            return R.error("查找失败");
        }
    }

    // 3. Find all servants
    @GetMapping("/findAll")
    public R findAll() {
        List<Servant> servants = servantService.findAll();
        return R.ok().put("servants", servants);
    }

    // 4. Update servant (partial updates allowed)
    @PutMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        Servant servant = servantService.find(id);

        if (servant != null) {
            if (requestData.containsKey("nickname")) {
                servant.setNickname(requestData.get("nickname").toString());
            }
            if (requestData.containsKey("current_location")) {
                servant.setCurrentLocation(requestData.get("current_location").toString());
            }

            boolean isUpdated = servantService.update(servant);
            if (isUpdated) {
                return R.ok("更新成功");
            }
        }
        return R.error("更新失败");
    }

    // 5. Delete servant by ID
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = servantService.delete(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}
