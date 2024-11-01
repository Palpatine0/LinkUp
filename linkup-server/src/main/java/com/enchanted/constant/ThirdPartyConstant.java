    package com.enchanted.constant;

import io.github.cdimascio.dotenv.Dotenv;

public class ThirdPartyConstant {
    private static final Dotenv dotenv = Dotenv.configure().filename(".env").load();

    public static final String ID_CARD_AUTH_URL = "https://eid.shumaidata.com/eid/check";

    public static final String ID_CARD_AUTH_KEY = dotenv.get("ID_CARD_AUTH_KEY");
    public static final String ID_CARD_AUTH_CODE = dotenv.get("ID_CARD_AUTH_CODE");
}
