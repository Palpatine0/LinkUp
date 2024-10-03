package com.enchanted.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enchanted.constant.WeChatConstant;
import com.enchanted.entity.User;
import com.enchanted.entity.UserClient;
import com.enchanted.entity.UserServant;
import com.enchanted.mapper.UserClientMapper;
import com.enchanted.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.mapper.UserServantMapper;
import com.enchanted.service.IUserService;
import com.enchanted.util.ConversionUtils;
import com.enchanted.util.HttpClientUtil;
import com.enchanted.util.WeChatUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserClientMapper userClientMapper;

    @Autowired
    private UserServantMapper userServantMapper;

    @Autowired
    private HttpClientUtil httpClientUtil;


    public Map<String, String> saveInfo(User user) {
        HashMap<String, String> map = new HashMap<>();
        int insert = userMapper.insert(user);
        // generate qr code
        genQrCodeImage(this.getAccessToken(), String.valueOf(user.getId()));
        if (user.getRole() == 1) {
            UserClient userClient = new UserClient();
            userClient.setUserId(user.getId());
            userClientMapper.insert(userClient);
        } else if (user.getRole() == 2) {
            UserServant userServant = new UserServant();
            userServant.setUserId(user.getId());
            userServantMapper.insert(userServant);
        }
        map.put("id", user.getId().toString());
        return map;
    }

    private String getAccessToken() {
        String url = WeChatConstant.ACCESS_TOKEN_URL + "?grant_type=client_credential&appid=" + WeChatConstant.APP_ID + "&secret=" + WeChatConstant.SECRET;
        String token = httpClientUtil.sendHttpGet(url);
        JSONObject jsonObject = JSON.parseObject(token);
        return jsonObject.get("access_token").toString();
    }

    private void genQrCodeImage(String accessToken, String referrerId) {
        Map<String, Object> body = new HashMap<>();
        body.put("path", "pages/home/home?referrerId=" + referrerId);
        String url = WeChatConstant.QR_CODE_Url + "?access_token=" + accessToken;
        byte[] qrCodeBytes = getWechatQrcodeByHttpClient(url, body);
        String referralQRCode = Base64.getEncoder().encodeToString(qrCodeBytes);
        User user = new User();
        user.setReferralQrCode(referralQRCode);
        user.setId(Long.parseLong(referrerId));
        userMapper.updateById(user);
//        user.setReferralQrCode(referralQRCode);
    }

    private byte[] getWechatQrcodeByHttpClient(String url, Map<String, Object> body) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity entity = new StringEntity(JSONObject.toJSONString(body));
            entity.setContentType("image/png");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            try (InputStream inputStream = response.getEntity().getContent();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                return out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Page<User> search(Map<String, Object> params, int page, int size) {
        IPage<User> userPage = new Page<>(page, size);
        userPage = userMapper.search(userPage, params);
        return (Page<User>) userPage;
    }

    @Override
    public Page<User> searchServant(Map<String, Object> params, int page, int size) {
        IPage<User> userPage = new Page<>(page, size);
        userPage = userMapper.searchServant(userPage, params);
        return (Page<User>) userPage;
    }

    @Override
    public Map<String, String> getConfigInfo(String code, String role) {
        HashMap<String, String> map = new HashMap<>();
        JSONObject object = WeChatUtil.getUserConfigInfo(code);
        map.put("openid", object.get("openid").toString());
        map.put("sessionKey", object.get("session_key").toString());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", object.get("openid").toString());
        wrapper.eq("role", role);
        User existingUser = userMapper.selectOne(wrapper);
        if (existingUser != null) {
            map.put("isNewUser", "0");
            map.put("id", String.valueOf(existingUser.getId()));
        } else {
            map.put("isNewUser", "1");
        }
        return map;
    }

    @Override
    public Map referralCodeValidation(String referralCode,String role) {
        HashMap<String, String> map = new HashMap<>();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("referral_code", referralCode);
        wrapper.eq("role", role);
        wrapper.eq("is_deleted", "0");
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            map.put("referrerId", String.valueOf(user.getId()));
            map.put("validRC", "1");
        } else {
            map.put("validRC", "0");
        }
        return map;
    }


    @Override
    public boolean update(Long id, Map<String, Object> changes) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return false;
        }

        changes.forEach((field, value) -> {
            Field classField = ReflectionUtils.findField(User.class, field);
            if (classField != null) {
                classField.setAccessible(true);
                // Check for type mismatch and convert if necessary
                if (!classField.getType().isAssignableFrom(value.getClass())) {
                    Object convertedValue = ConversionUtils.convertValueToRequiredType(value, classField.getType());
                    ReflectionUtils.setField(classField, user, convertedValue);
                } else {
                    ReflectionUtils.setField(classField, user, value);
                }
            }
        });

        int updated = userMapper.updateById(user);
        return retBool(updated);
    }

    @Override
    public boolean delete(Long id) {
        int deleted = userMapper.deleteById(id);
        return retBool(deleted);
    }
}
