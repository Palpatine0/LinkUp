package com.enchanted.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enchanted.entity.Message;
import com.enchanted.mapper.MessageMapper;
import com.enchanted.service.IMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    /*C*/
    @Override
    public boolean save(Message message) {
        return messageMapper.insert(message) > 0;
    }

    @Override
    public String saveMediaFile(MultipartFile file) {
        // Implement file storage logic here
        // For example, save to local file system or cloud storage
        // Return the URL or path of the stored file

        // Example with local file system (not recommended for production)
        try {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir = "/path/to/upload/directory/";

            // Ensure the directory exists
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Save the file
            File serverFile = new File(uploadDir + filename);
            file.transferTo(serverFile);

            // Generate the file URL
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(filename)
                .toUriString();

            return fileUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*R*/
    @Override
    public Page<Message> search(Map<String, Object> params, int page, int size) {
        IPage<Message> messagePage = new Page<>(page, size);
        messagePage = messageMapper.search(messagePage, params);
        return (Page<Message>) messagePage;
    }

    @Override
    public Page<Message> searchContacts(Map<String, Object> params, int page, int size) {
        IPage<Message> messagePage = new Page<>(page, size);
        messagePage = messageMapper.searchContacts(messagePage, params);
        return (Page<Message>) messagePage;
    }

    /*U*/
    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(Message.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, message, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, message, value);
                }
            }
        });

        int updated = messageMapper.updateById(message);
        return retBool(updated);
    }

    @Override
    public boolean markAsRead(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return false;
        }
        int updated = messageMapper.markMessagesAsRead(messageIds);
        return updated > 0;
    }

    /*D*/
    @Override
    public boolean delete(Long id) {
        return messageMapper.deleteById(id) > 0;
    }
}
