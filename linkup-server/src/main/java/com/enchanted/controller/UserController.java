package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.User;
import com.enchanted.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.vo.R;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    /*C*/
    @PostMapping("/save")
    public R save(@RequestBody User user) {
        return R.ok().put("data", userService.saveInfo(user));
    }


    /*R*/
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        Integer ageMin = requestData.get("ageMin") != null ? Integer.parseInt(requestData.get("ageMin").toString()) : null;
        Integer ageMax = requestData.get("ageMax") != null ? Integer.parseInt(requestData.get("ageMax").toString()) : null;

        requestData.remove("page");
        requestData.remove("size");

        Page<User> userPage = userService.search(requestData, page, size, ageMin, ageMax);
        return R.paginate(userPage);
    }


    @PostMapping("/search-servant")
    public R searchServant(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<User> userPage = userService.searchServant(requestData, page, size);
        return R.paginate(userPage);
    }

    @PostMapping("/user-config")
    public R getConfigInfo(@RequestBody Map<String, String> dto) {
        String code = dto.get("code");
        int role = Integer.parseInt(dto.get("role"));
        return R.ok().put("data", userService.getConfigInfo(code, role));
    }

    @PostMapping("/referral-code-validation")
    public R referralCodeValidation(@RequestBody Map<String, String> dto) {
        String referralCode = dto.get("referralCode");
        String role = dto.get("role");
        return R.ok().put("data", userService.referralCodeValidation(referralCode, role));
    }

    @PostMapping("/identity-validation")
    public R identityValidation(@RequestBody Map<String, String> dto) {
        Long id = Long.parseLong(dto.get("id").toString());
        int role = Integer.parseInt(dto.get("role"));
        String name = dto.get("name");
        String idCardNumber = dto.get("idCardNumber");
        return R.ok().put("data", userService.identityValidation(id, role, name, idCardNumber));
    }

    @PostMapping("/sms-validation")
    public R smsValidation(@RequestBody Map<String, String> dto) {
        String mobile = dto.get("mobile");
        String code = dto.get("code");
        return R.ok().put("data", userService.smsValidation(mobile,code));
    }

    /*U*/
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


    /*D*/
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