package com.enchanted.constant;

public class TransactionConstant {

    // Transaction types
    public static final int DEDUCTION = 0;
    public static final int ADDITION = 1;

    // Currency types
    public static final int BALANCE_CURRENCY = 0;
    public static final int LOOKING_COIN_CURRENCY = 1;

    // Description
    public static final String WITHDRAW = "Withdraw";
    public static final String WITHDRAW_CN = "提现";
    public static final String DEPOSIT = "Deposit";
    public static final String DEPOSIT_CN = "存款";
    // - Client > ADDITION
    public static final String CLIENT_REFERRER_COMMISSION = "Referrer commission";
    public static final String CLIENT_REFERRER_COMMISSION_CN = "订单佣金";
    public static final String CLIENT_CANCEL_ORDER_REFUND = "Order cancel refund";
    public static final String CLIENT_CANCEL_ORDER_REFUND_CN = "订单取消退款";
    public static final String CLIENT_NO_RESPONSE_GIFT_REFUND = "Refund due to no response to gifted message";
    public static final String CLIENT_NO_RESPONSE_GIFT_REFUND_CN = "未回复礼物退款";
    // - Client > DEDUCTION
    public static final String CLIENT_ORDER_PAYMENT = "Order payment";
    public static final String CLIENT_ORDER_PAYMENT_CN = "订单付款";
    public static final String CLIENT_CONVERSATION_GIFT = "Conversation gift";
    public static final String CLIENT_CONVERSATION_GIFT_CN = "聊天礼物";

    // - Servant > ADDITION
    public static final String SERVANT_REFERRER_COMMISSION = "Referrer commission";
    public static final String SERVANT_REFERRER_COMMISSION_CN = "订单佣金";
    public static final String SERVANT_INITIAL_PAYMENT = "Order initial income";
    public static final String SERVANT_INITIAL_PAYMENT_CN = "订单初始收益";
    public static final String SERVANT_PERFORMANCE_PAYMENT = "Order rating income";
    public static final String SERVANT_PERFORMANCE_PAYMENT_CN = "订单评价收益";

    public static final String SERVANT_CONVERSATION_GIFT = "User gift";
    public static final String SERVANT_CONVERSATION_GIFT_CN = "用户赠礼";

    // - Servant > DEDUCTION
    public static final String SERVANT_RESPONSE_DELAY_PENALTY = "Penalty for delayed response beyond 1 minute";
    public static final String SERVANT_RESPONSE_DELAY_PENALTY_CN = "超时未回复惩罚";
    public static final String SERVANT_NO_RESPONSE_GIFT_SHARE_DEDUCTION = "Deduction of gift share due to no response";
    public static final String SERVANT_NO_RESPONSE_GIFT_SHARE_DEDUCTION_CN = "未回复礼物分成扣除";

}
