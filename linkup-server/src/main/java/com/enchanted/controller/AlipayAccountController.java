package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.AlipayAccount;
import com.enchanted.service.IAlipayAccountService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/alipay-account")
public class AlipayAccountController {

    @Autowired
    private IAlipayAccountService alipayAccountService;

    /* C */
    @PostMapping("/save")
    public R save(@RequestBody AlipayAccount alipayAccount) {
        boolean isSaved = alipayAccountService.save(alipayAccount);
        if (isSaved) {
            return R.ok("Alipay account added successfully");
        } else {
            return R.error("Failed to add Alipay account");
        }
    }

    /* R */
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        Page<AlipayAccount> alipayAccountPage = alipayAccountService.search(requestData, page, size);
        return R.paginate(alipayAccountPage);
    }

    /* U */
    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = alipayAccountService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Alipay account updated successfully");
        } else {
            return R.error("Failed to update Alipay account");
        }
    }

    /* D */
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = alipayAccountService.delete(id);
        if (isDeleted) {
            return R.ok("Alipay account deleted successfully");
        } else {
            return R.error("Failed to delete Alipay account");
        }
    }
}
