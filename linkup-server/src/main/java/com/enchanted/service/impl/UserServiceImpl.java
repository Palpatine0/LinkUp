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
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    public boolean save(User user) {
        int insert = userMapper.insert(user);
        return retBool(insert);
    }

    @Override
    public User saveAuthInfo(String code,int role) {
        User user = new User();

        JSONObject object = WeChatUtil.getOpenId(code);
        String openid = object.get("openid").toString();
        String sessionkey = object.get("session_key").toString();
        String unionId = "";
        String id = "";
        if (object.get("unionid") != null) {
            unionId = object.get("unionid").toString();
        }

        user.setOpenid(openid);
        user.setSessionKey(sessionkey);
        user.setUnionid(unionId);
        user.setCreatedAt(new Date());

        User existingUser = userMapper.selectOne(new QueryWrapper<User>().eq("openid", openid));
        if (existingUser != null) {
            existingUser.setSessionKey(sessionkey);
            existingUser.setUnionid(unionId);
            userMapper.updateById(existingUser);
            user.setId(existingUser.getId());
        } else {
            userMapper.insert(user);
            user.setId(user.getId());
            user.setRole(role);
            // generate qr code
            String qrCode = genQrCodeImage(this.getAccessToken(), String.valueOf(user.getId()));
            user.setReferralQrCode(qrCode);
            // generate user_client record
            if (role == 1) {
                UserClient userClient = new UserClient();
                userClient.setUserId(user.getId());
                userClientMapper.insert(userClient);
            } else if (role == 2) {
                UserServant userServant = new UserServant();
                userServant.setUserId(user.getId());
                userServantMapper.insert(userServant);
            }
            userMapper.updateById(user);
        }
        return user;
    }

    private String getAccessToken() {
        String url = WeChatConstant.ACCESS_TOKEN_URL + "?grant_type=client_credential&appid=" + WeChatConstant.APP_ID + "&secret=" + WeChatConstant.SECRET;
        String token = httpClientUtil.sendHttpGet(url);
        JSONObject jsonObject = JSON.parseObject(token);
        return jsonObject.get("access_token").toString();
    }

    private String genQrCodeImage(String accessToken, String referrerId) {
        Map<String, Object> body = new HashMap<>();
        body.put("path", "pages/home/home?referrerId=" + referrerId);
        String url = WeChatConstant.QR_CODE_Url + "?access_token=" + accessToken;
        byte[] qrCodeBytes = getWechatQrcodeByHttpClient(url, body);
        String base64QRCode = Base64.getEncoder().encodeToString(qrCodeBytes);
        return base64QRCode;
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
