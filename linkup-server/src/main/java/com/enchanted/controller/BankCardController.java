package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.BankCard;
import com.enchanted.service.IBankCardService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/bank_card")
public class BankCardController {

    @Autowired
    private IBankCardService bankCardService;

    /* C */
    @PostMapping("/save")
    public R save(@RequestBody BankCard bankCard) {
        boolean isSaved = bankCardService.save(bankCard);
        if (isSaved) {
            return R.ok("Bank card added successfully");
        } else {
            return R.error("Failed to add bank card");
        }
    }

    /* R */
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<BankCard> bankCardPage = bankCardService.search(requestData, page, size);
        return R.paginate(bankCardPage);
    }

    /* U */
    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = bankCardService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Bank card updated successfully");
        } else {
            return R.error("Failed to update bank card");
        }
    }

    /* D */
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = bankCardService.delete(id);
        if (isDeleted) {
            return R.ok("Bank card deleted successfully");
        } else {
            return R.error("Failed to delete bank card");
        }
    }
}
