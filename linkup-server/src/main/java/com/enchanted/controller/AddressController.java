package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Address;
import com.enchanted.service.IAddressService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private IAddressService addressService;

    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<Address> addressPage = addressService.search(requestData, page, size);
        return R.paginate(addressPage);
    }

    @PostMapping("/save")
    public R save(@RequestBody Address address) {
        boolean isSaved = addressService.save(address);
        if (isSaved) {
            return R.ok("Address added successfully");
        } else {
            return R.error("Failed to add address");
        }
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = addressService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Address updated successfully");
        } else {
            return R.error("Failed to update address");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = addressService.delete(id);
        if (isDeleted) {
            return R.ok("Address deleted successfully");
        } else {
            return R.error("Failed to delete address");
        }
    }
}
