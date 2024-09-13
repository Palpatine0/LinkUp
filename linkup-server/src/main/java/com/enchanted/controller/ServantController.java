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

    @PostMapping("/save")
    public R save(@RequestBody Servant servant) {
        boolean isSaved = servantService.save(servant);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

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

    @GetMapping("/findAll")
    public R findAll() {
        List<Servant> servants = servantService.findAll();
        return R.ok().put("servants", servants);
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = servantService.update(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }

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
