package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.ServantType;
import com.enchanted.service.IServantTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.vo.R;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/servant-type")
public class ServantTypeController {

    @Autowired
    private IServantTypeService servantTypeService;

    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<ServantType> servantTypePage = servantTypeService.search(requestData, page, size);
        return buildPaginatedResponse(servantTypePage);
    }


    @PostMapping("/save")
    public R save(@RequestBody ServantType servantType) {
        boolean isSaved = servantTypeService.save(servantType);
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

    private R buildPaginatedResponse(Page<ServantType> servantTypePage) {
        return R.ok()
            .put("servantTypeList", servantTypePage.getRecords())
            .put("total", servantTypePage.getTotal())
            .put("pages", servantTypePage.getPages())
            .put("current", servantTypePage.getCurrent());
    }
}
