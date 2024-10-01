package com.enchanted.constant;

public class TransactionConstant {

    // Transaction types
    public static final int DEDUCTION = 0;
    public static final int ADDITION = 1;

    // Description
    // - Client add money
    public static final String CLIENT_REFERRER_COMMISSION = "CLT_RC";
    public static final String CLIENT_CANCEL_ORDER_REFUND = "CLT_COR";
    // - Client lose money
    public static final String CLIENT_ORDER_PAYMENT = "CLT_OP";

    // - Servant add money
    public static final String SERVANT_REFERRER_COMMISSION = "SVT_RC";
    public static final String SERVANT_INITIAL_PAYMENT = "SVT_IP";
    public static final String SERVANT_PERFORMANCE_PAYMENT = "SVT_PP";

    // - Servant lose money

}
