package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.WithdrawalRequest;
import com.enchanted.service.IWithdrawalRequestService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/withdrawal-request")
public class WithdrawalRequestController {

    @Autowired
    private IWithdrawalRequestService withdrawalRequestService;

    /* C */
    @PostMapping("/save")
    public R save(@RequestBody WithdrawalRequest withdrawalRequest) {
        boolean isSaved = withdrawalRequestService.save(withdrawalRequest);
        if (isSaved) {
            return R.ok("Withdrawal request submitted successfully");
        } else {
            return R.error("Failed to submit withdrawal request");
        }
    }

    /* R */
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 20;

        requestData.remove("page");
        requestData.remove("size");

        Page<WithdrawalRequest> withdrawalRequestPage = withdrawalRequestService.search(requestData, page, size);
        return R.paginate(withdrawalRequestPage);
    }

    /* U */
    @PostMapping("/update-status")
    public R updateStatus(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = withdrawalRequestService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Withdrawal request status updated successfully");
        } else {
            return R.error("Failed to update withdrawal request status");
        }
    }

    /* D */
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = withdrawalRequestService.delete(id);
        if (isDeleted) {
            return R.ok("Withdrawal request deleted successfully");
        } else {
            return R.error("Failed to delete withdrawal request");
        }
    }
}
