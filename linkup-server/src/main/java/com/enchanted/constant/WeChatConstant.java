package com.enchanted.constant;

import io.github.cdimascio.dotenv.Dotenv;

public class WeChatConstant {
    private static final Dotenv dotenv = Dotenv.configure().filename(".env").load();
    
    public static final String USER_INFO_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=";
    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    public static final String QR_CODE_URL = "https://api.weixin.qq.com/wxa/getwxacode";
    
    public static final String APP_ID_CLIENT = dotenv.get("WECHAT_APP_ID_CLIENT");
    public static final String SECRET_CLIENT = dotenv.get("WECHAT_SECRET_CLIENT");
    
    public static final String APP_ID_SERVANT = dotenv.get("WECHAT_APP_ID_SERVANT");
    public static final String SECRET_SERVANT = dotenv.get("WECHAT_SECRET_SERVANT");
}
