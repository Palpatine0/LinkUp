package com.enchanted.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechat")
@Data
public class WeChatProperties {

    private String accessTokenUrl;

    private String appid; // wechat miniapp appId

    private String secret; // wechat miniapp appSecret

}
