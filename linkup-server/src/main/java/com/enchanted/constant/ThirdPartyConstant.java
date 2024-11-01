    package com.enchanted.constant;

import io.github.cdimascio.dotenv.Dotenv;

public class ThirdPartyConstant {
    private static final Dotenv dotenv = Dotenv.configure().filename(".env").load();

    public static final String ID_CARD_AUTH_URL = "https://eid.shumaidata.com/eid/check";
    public static final String BANK_CARD_AUTH_URL = "https://ckid.market.alicloudapi.com/lundear/verifyBankThree";

    public static final String ID_CARD_AUTH_KEY = dotenv.get("ID_CARD_AUTH_KEY");
    public static final String ID_CARD_AUTH_CODE = dotenv.get("ID_CARD_AUTH_CODE");

    public static final String BANK_CARD_AUTH_KEY = dotenv.get("BANK_CARD_AUTH_KEY");
    public static final String BANK_CARD_AUTH_CODE = dotenv.get("BANK_CARD_AUTH_CODE");
}
