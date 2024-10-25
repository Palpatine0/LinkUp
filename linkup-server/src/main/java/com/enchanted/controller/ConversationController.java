package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Conversation;
import com.enchanted.entity.User;
import com.enchanted.service.IConversationService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private IConversationService conversationService;

    /* C */
    @PostMapping("/save")
    public R save(@RequestBody Conversation conversation) {
        boolean isSaved = conversationService.save(conversation);
        if (isSaved) {
            return R.ok("Conversation started successfully");
        } else {
            return R.error("Failed to start conversation");
        }
    }

    /* R */
    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<Conversation> conversationPage = conversationService.search(requestData, page, size);
        return R.paginate(conversationPage);
    }

    @PostMapping("/search-contacts")
    public R searchContacts(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 10;

        requestData.remove("page");
        requestData.remove("size");

        Page<User> conversationPage = conversationService.searchContacts(requestData, page, size);
        return R.paginate(conversationPage);
    }

    /* U */
    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = conversationService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Conversation updated successfully");
        } else {
            return R.error("Failed to update conversation");
        }
    }

    /* D */
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = conversationService.delete(id);
        if (isDeleted) {
            return R.ok("Conversation deleted successfully");
        } else {
            return R.error("Failed to delete conversation");
        }
    }
}
