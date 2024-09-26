package com.enchanted.util;

import com.alibaba.fastjson.JSONObject;

import com.enchanted.constant.WeChatConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Slf4j(topic = "WechatUtils")
@Component
public class WeChatUtil {
    public static JSONObject getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WeChatConstant.APP_ID + "&secret=" + WeChatConstant.SECRET + "&js_code=" + code + "&grant_type=authorization_code";
        PrintWriter out = null;
        BufferedReader in = null;
        String line;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("contentType", "text/plain");
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.setDoOutput(true);
            conn.connect();
            out = new PrintWriter(conn.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line);
            }
            JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
