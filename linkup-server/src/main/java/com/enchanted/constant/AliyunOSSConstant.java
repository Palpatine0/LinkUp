package com.enchanted.constant;

import io.github.cdimascio.dotenv.Dotenv;

public class AliyunOSSConstant {
    private static final Dotenv dotenv = Dotenv.configure().filename(".env").load();
    public static final String ALIYUN_OSS_ENDPOINT = dotenv.get("ALIYUN_OSS_ENDPOINT");
    public static final String ALIYUN_OSS_ACCESS_KEY_ID = dotenv.get("ALIYUN_OSS_ACCESS_KEY_ID");
    public static final String ALIYUN_OSS_ACCESS_KEY_SECRET = dotenv.get("ALIYUN_OSS_ACCESS_KEY_SECRET");
    public static final String ALIYUN_OSS_BUCKET_NAME = dotenv.get("ALIYUN_OSS_BUCKET_NAME");
}
