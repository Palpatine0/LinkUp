package com.enchanted.constant;

import io.github.cdimascio.dotenv.Dotenv;

public class WeChatConstant {
    private static final Dotenv dotenv = Dotenv.load();
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    public static final String QR_CODE_Url = "https://api.weixin.qq.com/wxa/getwxacode";
    public static final String APP_ID = dotenv.get("WECHAT_APP_ID");
    public static final String SECRET = dotenv.get("WECHAT_SECRET");
}
