package com.enchanted.constant;

public class SystemConstant {

    /**
     * token
     */
    public static final int JWT_ERRCODE_NULL = 4000;
    public static final int JWT_ERRCODE_EXPIRE = 4001;
    public static final int JWT_ERRCODE_FAIL = 4002;

    /**
     * JWT
     */
    public static final String JWT_SECERT = "8677df7fc3a34e26a61c034d5ec8245d";
    public static final long JWT_TTL = 24 * 60 * 60 * 1000;
}
