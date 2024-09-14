package com.enchanted.controller;

import com.enchanted.entity.ServantType;
import com.enchanted.service.IServantTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.entity.R;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/servantType")
public class ServantTypeController {

    @Autowired
    private IServantTypeService servantTypeService;

    @PostMapping("/save")
    public R save(@RequestBody ServantType servantType) {
        boolean isSaved = servantTypeService.save(servantType);
        if (isSaved) {
            return R.ok("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    @PostMapping("/select")
    public R select(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        ServantType servantType = servantTypeService.select(id);
        if (servantType != null) {
            return R.ok().put("servantType", servantType);
        } else {
            return R.error("查找失败");
        }
    }

    @GetMapping("/selectAll")
    public R selectAll() {
        List<ServantType> servantTypes = servantTypeService.selectAll();
        return R.ok().put("servantTypeList", servantTypes);
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = servantTypeService.update(id, requestData);
        if (isUpdated) {
            return R.ok("更新成功");
        } else {
            return R.error("更新失败");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = servantTypeService.delete(id);
        if (isDeleted) {
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }
}
