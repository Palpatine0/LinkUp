    package com.enchanted.constant;

import io.github.cdimascio.dotenv.Dotenv;

public class ThirdPartyConstant {
    private static final Dotenv dotenv = Dotenv.configure().filename(".env").load();

    public static final String ID_CARD_AUTH_URL = "https://eid.shumaidata.com/eid/check";
    public static final String BANK_CARD_AUTH_URL = "https://ckid.market.alicloudapi.com/lundear/verifyBankThree";
    public static final String SMS_CARD_AUTH_URL = "https://gyytz.market.alicloudapi.com/sms/smsSend";

    public static final String ID_CARD_AUTH_KEY = dotenv.get("ID_CARD_AUTH_KEY");
    public static final String ID_CARD_AUTH_CODE = dotenv.get("ID_CARD_AUTH_CODE");

    public static final String BANK_CARD_AUTH_KEY = dotenv.get("BANK_CARD_AUTH_KEY");
    public static final String BANK_CARD_AUTH_CODE = dotenv.get("BANK_CARD_AUTH_CODE");

    public static final String SMS_AUTH_KEY = dotenv.get("SMS_AUTH_KEY");
    public static final String SMS_AUTH_CODE = dotenv.get("SMS_AUTH_CODE");
    public static final String SMS_AUTH_TEMPLATE_ID = dotenv.get("SMS_AUTH_TEMPLATE_ID");
    public static final String SMS_AUTH_SMS_SIGN_ID = dotenv.get("SMS_AUTH_SMS_SIGN_ID");
}
