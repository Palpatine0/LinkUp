package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Transaction;
import com.enchanted.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.enchanted.vo.R;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    /*C*/
    @PostMapping("/save")
    public R save(@RequestBody Transaction transaction) {
        boolean isSaved = transactionService.save(transaction);
        if (isSaved) {
            return R.ok("Transaction added successfully");
        } else {
            return R.error("Failed to add transaction");
        }
    }


    /*R*/
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<Transaction> transactionPage = transactionService.search(requestData, page, size);
        return R.paginate(transactionPage);
    }

    /*U*/
    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = transactionService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Transaction updated successfully");
        } else {
            return R.error("Failed to update transaction");
        }
    }

    /*D*/
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = transactionService.delete(id);
        if (isDeleted) {
            return R.ok("Transaction deleted successfully");
        } else {
            return R.error("Failed to delete transaction");
        }
    }
}
