package com.enchanted.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Message;
import com.enchanted.service.IMessageService;
import com.enchanted.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource; // Added: Needed for ResponseEntity<Resource>
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @PostMapping("/search")
    public R search(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 20;

        requestData.remove("page");
        requestData.remove("size");

        Page<Message> messagePage = messageService.search(requestData, page, size);
        return buildPaginatedResponse(messagePage);
    }

    @PostMapping("/search-contacts")
    public R searchContacts(@RequestBody Map<String, Object> requestData) {
        int page = requestData.get("page") != null ? Integer.parseInt(requestData.get("page").toString()) : 1;
        int size = requestData.get("size") != null ? Integer.parseInt(requestData.get("size").toString()) : 20;

        requestData.remove("page");
        requestData.remove("size");

        Page<Message> messagePage = messageService.searchContacts(requestData, page, size);
        return buildPaginatedResponse(messagePage);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("/path/to/upload/directory/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public R save(@RequestBody Message message) {
        boolean isSaved = messageService.save(message);
        if (isSaved) {
            return R.ok("Message sent successfully");
        } else {
            return R.error("Failed to send message");
        }
    }

    @PostMapping("/send")
    public R sendMessage(
        @RequestParam("senderId") Long senderId,
        @RequestParam("recipientId") Long recipientId,
        @RequestParam(value = "content", required = false) String content,
        @RequestParam(value = "mediaType", required = false) Integer mediaType,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setStatus(0); // Sending
        message.setIsRead(false);
        message.setCreatedAt(new Date());

        if (mediaType == null || mediaType == 0) {
            // Text message
            message.setContent(content);
            message.setMediaType(0);
        } else {
            // Media message
            if (file == null || file.isEmpty()) {
                return R.error("File is required for media messages");
            }

            // Save the file and get the URL
            String mediaUrl = messageService.saveMediaFile(file);
            if (mediaUrl == null) {
                return R.error("Failed to upload file");
            }
            message.setMediaType(mediaType);
            message.setMediaUrl(mediaUrl);
        }

        boolean isSaved = messageService.save(message);
        if (isSaved) {
            // Update status to sent
            message.setStatus(1);
            messageService.updateById(message);
            return R.ok("Message sent successfully").put("message", message);
        } else {
            return R.error("Failed to send message");
        }
    }

    @PostMapping("/update")
    public R update(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        requestData.remove("id");

        boolean isUpdated = messageService.update(id, requestData);
        if (isUpdated) {
            return R.ok("Message updated successfully");
        } else {
            return R.error("Failed to update message");
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> requestData) {
        Long id = Long.parseLong(requestData.get("id").toString());
        boolean isDeleted = messageService.delete(id);
        if (isDeleted) {
            return R.ok("Message deleted successfully");
        } else {
            return R.error("Failed to delete message");
        }
    }

    private R buildPaginatedResponse(Page<Message> messagePage) {
        return R.ok()
            .put("messageList", messagePage.getRecords())
            .put("total", messagePage.getTotal())
            .put("pages", messagePage.getPages())
            .put("current", messagePage.getCurrent());
    }
}
