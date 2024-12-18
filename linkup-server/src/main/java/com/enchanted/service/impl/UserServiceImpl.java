package com.enchanted.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.enchanted.constant.ThirdPartyConstant;
import com.enchanted.constant.UserConstant;
import com.enchanted.constant.WeChatConstant;
import com.enchanted.entity.*;
import com.enchanted.mapper.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enchanted.service.IFileService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IFileService fileService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserClientMapper userClientMapper;

    @Autowired
    private UserServantMapper userServantMapper;

    @Autowired
    private HttpClientUtil httpClientUtil;

    // TODO: update to Redis approach
    private ConcurrentHashMap<String, Long> lastSmsRequestTime = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Map<String, Object>> verificationCodes = new ConcurrentHashMap<>();

    /*C*/
    public Map<String, String> saveInfo(User user) {
        HashMap<String, String> map = new HashMap<>();
        int insert = userMapper.insert(user);
        genQrCodeImage(this.getAccessToken(user.getRole()), String.valueOf(user.getId()), user.getIdentifier());
        if (user.getRole() == UserConstant.CLIENT) {
            UserClient userClient = new UserClient();
            userClient.setUserId(user.getId());
            userClientMapper.insert(userClient);
        } else if (user.getRole() == UserConstant.SERVANT) {
            UserServant userServant = new UserServant();
            userServant.setUserId(user.getId());
            userServantMapper.insert(userServant);
        }
        map.put("id", user.getId().toString());
        return map;
    }

    /*R*/
    @Override
    public Page<User> search(Map<String, Object> params, int page, int size, Integer ageMin, Integer ageMax) {
        if (ageMin != null && ageMax != null) {
            params.put("ageMin", ageMin);
            params.put("ageMax", ageMax);
        }
        IPage<User> userPage = new Page<>(page, size);
        userPage = userMapper.search(userPage, params);
        return (Page<User>) userPage;
    }


    private String getAccessToken(int role) {
        String url = "";
        if (role == UserConstant.CLIENT) {
            url = WeChatConstant.ACCESS_TOKEN_URL + "?grant_type=client_credential&appid=" + WeChatConstant.APP_ID_CLIENT + "&secret=" + WeChatConstant.SECRET_CLIENT;
        } else if (role == UserConstant.SERVANT) {
            url = WeChatConstant.ACCESS_TOKEN_URL + "?grant_type=client_credential&appid=" + WeChatConstant.APP_ID_SERVANT + "&secret=" + WeChatConstant.SECRET_SERVANT;
        }
        String token = httpClientUtil.sendHttpGet(url);
        JSONObject jsonObject = JSON.parseObject(token);
        return jsonObject.get("access_token").toString();
    }

    private void genQrCodeImage(String accessToken, String referrerId, String identifier) {
        Map<String, Object> body = new HashMap<>();
        body.put("path", "pages/home/home?referrerId=" + referrerId);
        String url = WeChatConstant.QR_CODE_URL + "?access_token=" + accessToken;
        byte[] qrCodeBytes = getWechatQrcodeByHttpClient(url, body);
        String filePath = "public/user/" + identifier + "/referral-qr-code/";
        String fileName = "referral-qr-code.png";
        String referralQRCode = fileService.upload(qrCodeBytes, filePath, fileName);
        User user = new User();
        user.setReferralQrCode(referralQRCode);
        user.setId(Long.parseLong(referrerId));
        userMapper.updateById(user);
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
    public Map<String, String> getConfigInfo(String code, int role) {
        HashMap<String, String> map = new HashMap<>();
        JSONObject object = WeChatUtil.getUserConfigInfo(code, role);
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
    public Map referralCodeValidation(String referralCode, String role) {
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
    public Map<String, Object> identityValidation(Long id, int role, String name, String idCardNumber) {
        Map<String, Object> resultMap = new HashMap<>();
        String url = ThirdPartyConstant.ID_CARD_AUTH_URL;
        String appCode = ThirdPartyConstant.ID_CARD_AUTH_CODE;

        Map<String, String> params = new HashMap<>();
        params.put("idcard", idCardNumber);
        params.put("name", name);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appCode);

        String response = httpClientUtil.sendHttpPost(url, params, headers);

        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject resultObject = jsonObject.getJSONObject("result");
        if (jsonObject.getInteger("code") == 0) {
            String resultRes = resultObject.getString("res");
            if (!resultRes.equals("1")) {
                throw new IllegalArgumentException("Match failed");
            } else {
                if (resultRes.equals("3")) {
                    throw new IllegalArgumentException("No record");
                } else {
                    resultMap.put("result", "Success");
                }
            }
        } else {
            throw new IllegalArgumentException(jsonObject.getInteger("code").toString());
        }

        String resultIdCardNumber = resultObject.getString("idcard");
        String resultName = resultObject.getString("name");
        String resultSex = resultObject.getString("sex");
        String resultBirthday = resultObject.getString("birthday");
        String resultAddress = resultObject.getString("address");

        User user = userMapper.selectById(id);
        user.setIdCardNumber(resultIdCardNumber);
        if (role == UserConstant.CLIENT) {
            user.setIdCardName(resultName);
        } else if (role == UserConstant.SERVANT) {
            user.setIdCardName(resultName);
            user.setNickname(resultName);
        }
        if (resultSex == "男") {
            user.setIdCardGender(0);
            user.setGender(0);
        } else {
            user.setIdCardGender(1);
            user.setGender(1);
        }
        try {
            Date idCardBirthday = new SimpleDateFormat("yyyyMMdd").parse(resultBirthday);
            user.setIdCardBirthday(idCardBirthday);
            LocalDate birthDate = idCardBirthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = LocalDate.now();
            int age = (int) ChronoUnit.YEARS.between(birthDate, currentDate);
            user.setAge(age);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format for resultBirthday: " + resultBirthday, e);
        }
        user.setIdCardAddress(resultAddress);
        user.setIsIdentityVerified(1);
        userMapper.updateById(user);

        return resultMap;
    }

    @Override
    public Map sms(String mobile) {
        Map<String, Object> resultMap = new HashMap<>();

        // Rate limiting: prevent multiple requests within 1 minute
        Long currentTime = System.currentTimeMillis();
        Long lastRequestTime = lastSmsRequestTime.get(mobile);

        if (lastRequestTime != null && (currentTime - lastRequestTime < TimeUnit.MINUTES.toMillis(1))) {
            resultMap.put("status", 400);
            resultMap.put("message", "Please wait for 1 minute before requesting another SMS code");
            return resultMap;
        }

        // Generate 4-digit code
        String code = String.format("%04d", new Random().nextInt(10000));

        // Store the code with timestamp
        Map<String, Object> codeData = new HashMap<>();
        codeData.put("code", code);
        codeData.put("timestamp", currentTime);
        verificationCodes.put(mobile, codeData);

        // Send SMS via third-party API
        String url = ThirdPartyConstant.SMS_CARD_AUTH_URL;
        String appCode = ThirdPartyConstant.SMS_AUTH_CODE;
        String templateId = ThirdPartyConstant.SMS_AUTH_TEMPLATE_ID;
        String smsSignId = ThirdPartyConstant.SMS_AUTH_SMS_SIGN_ID;
        String smsContent = "**code**:" + code + ",**minute**:5";

        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("templateId", templateId);
        params.put("smsSignId", smsSignId);
        params.put("param", smsContent);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appCode);

        // Send the SMS
        String response = httpClientUtil.sendHttpPost(url, params, headers);

        // Handle response and errors as needed

        // Update the last request time
        lastSmsRequestTime.put(mobile, currentTime);

        resultMap.put("status", 200);
        resultMap.put("message", "SMS sent successfully");
        return resultMap;
    }

    @Override
    public Map<String, Object> smsValidation(String mobile, String code) {
        Map<String, Object> resultMap = new HashMap<>();

        Map<String, Object> codeData = verificationCodes.get(mobile);
        if (codeData == null) {
            resultMap.put("status", 400);
            resultMap.put("message", "No code sent to this mobile");
            return resultMap;
        }

        String storedCode = (String) codeData.get("code");
        Long timestamp = (Long) codeData.get("timestamp");
        Long currentTime = System.currentTimeMillis();

        if (currentTime - timestamp > TimeUnit.MINUTES.toMillis(5)) {
            verificationCodes.remove(mobile);
            resultMap.put("status", 400);
            resultMap.put("message", "Verification code expired");
            return resultMap;
        }

        if (storedCode.equals(code)) {
            verificationCodes.remove(mobile);
            resultMap.put("status", 200);
            resultMap.put("message", "Verification successful");
        } else {
            resultMap.put("status", 400);
            resultMap.put("message", "Verification code does not match");
        }

        return resultMap;
    }

    @Override
    public Page<User> searchServant(Map<String, Object> params, int page, int size) {
        IPage<User> userPage = new Page<>(page, size);
        userPage = userMapper.searchServant(userPage, params);
        return (Page<User>) userPage;
    }

    /*D*/
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

    /*U*/
    @Override
    public boolean delete(Long id) {
        UserServant userServant = userServantMapper.selectById(id);
        userServant.setIsDeleted(1);
        int updated = userServantMapper.updateById(userServant);
        return updated > 0;
    }
}
