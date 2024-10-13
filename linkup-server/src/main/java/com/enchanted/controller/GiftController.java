package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Gift;
import com.enchanted.service.IGiftService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/gift")
public class GiftController {

    @Autowired
    private IGiftService giftService;

    /* C */
    @PostMapping("/save")
    public R save(@RequestBody Gift gift) {
        boolean isSaved = giftService.save(gift);
        if (isSaved) {
            return R.ok("Gift added successfully");
        } else {
            return R.error("Failed to add gift");
        }
    }

    @PostMapping("/purchase")
    public R purchaseGift(@RequestBody Map<String, Object> requestData) {
        Long senderId = Long.parseLong(requestData.get("senderId").toString());
        Long recipientId = Long.parseLong(requestData.get("recipientId").toString());
        Long giftId = Long.parseLong(requestData.get("giftId").toString());

        // Process the gift purchase
        boolean success = giftService.purchase(senderId, recipientId, giftId);
        if (success) {
            return R.ok("Gift purchased successfully");
        } else {
            return R.error("Gift purchase failed");
        }
    }


    /* R */
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<Gift> giftPage = giftService.search(requestData, page, size);
        return R.paginate(giftPage);
    }

    /* U */
    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = giftService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Gift updated successfully");
        } else {
            return R.error("Failed to update gift");
        }
    }

    /* D */
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = giftService.delete(id);
        if (isDeleted) {
            return R.ok("Gift deleted successfully");
        } else {
            return R.error("Failed to delete gift");
        }
    }


}
