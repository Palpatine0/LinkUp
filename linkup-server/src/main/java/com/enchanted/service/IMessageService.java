package com.enchanted.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enchanted.entity.Message;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.util.List;
import java.util.Map;

public interface IMessageService extends IService<Message> {

    /*C*/
    boolean save(Message message);

    String saveMediaFile(MultipartFile file);

    /*R*/
    Page<Message> search(Map<String, Object> params, int page, int size);

    Page<Message> searchContacts(Map<String, Object> params, int page, int size);

    /*U*/
    boolean update(Long id, Map<String, Object> changes);

    boolean markAsRead(List<Long> messageIds);

    /*D*/
    boolean delete(Long id);
}
