package com.enchanted.constant;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class OrderConstant {
    // Free posting quota
    public static final int FREE_POSTING_QUOTA = 2;

    // Payment method
    public static final String BALANCE = "0";
    public static final String WECHAT = "1";

    // Order status constants
    public static final int PENDING = 0;
    public static final int PROCESSING = 1;
    public static final int COMPLETED = 2;
    public static final int CANCELED = 3;

    // Order performance ratings
    public static final int LIMITED = 0;
    public static final int FAIR = 1;
    public static final int GOOD = 2;

    // Sharing rates
    public static final BigDecimal CLIENT_REFERRER_SHARE = BigDecimal.valueOf(0.05);
    public static final BigDecimal SERVANT_REFERRER_SHARE = BigDecimal.valueOf(0.05);

    // Servant commission rates
    public static final BigDecimal MAX_SERVANT_COMMISSION = BigDecimal.valueOf(0.70);
    public static final BigDecimal INITIAL_SERVANT_PAYMENT_RATE = BigDecimal.valueOf(0.80); // 80% of 70%
    public static final BigDecimal ADDITIONAL_PAYMENT_RATE_LIMITED = BigDecimal.ZERO;      // 0%
    public static final BigDecimal ADDITIONAL_PAYMENT_RATE_FAIR = BigDecimal.valueOf(0.10); // 10% of 70%
    public static final BigDecimal ADDITIONAL_PAYMENT_RATE_GOOD = BigDecimal.valueOf(0.20); // 20% of 70%

    // Count down timing
    public static final int AUTO_REFUND_MONITOR_DELAY = (int) TimeUnit.MINUTES.toMillis(30);
    //    public static final int AUTO_REFUND_MONITOR_DELAY = (int) TimeUnit.SECONDS.toMillis(15);
    public static final int SERVANT_SELECTION_MONITOR_DELAY = (int) TimeUnit.MINUTES.toMillis(10);
    public static final int AUTO_RATING_MONITOR_DELAY = (int) TimeUnit.DAYS.toMillis(1);
}
