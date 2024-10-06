<template>
<view class="payment-container">
    <view class="mask" @click="close()"></view>
    <view class="widget-center payment-anim">
        <view class="payment-header">选择支付方式</view>

        <!-- Payment Method Selection -->
        <view class="payment-methods">
            <button class="payment-button" style="background-color: #25b671" @click="payWithWeChat">微信支付</button>
            <button class="payment-button" :disabled="!balanceAdequate" @click="payWithBalance">余额支付</button>
            <div class="payment-method-title">
                您的余额:¥{{ user.balance }}
                <div v-if="!balanceAdequate">(余额不足)</div>
            </div>
        </view>

    </view>
</view>
</template>

<script>
export default {
    props: {
        user: Object,
        balanceAdequate: Boolean
    },
    methods: {
        close() {
            this.$parent.paymentMethodSelectionToggle(false);
        },

        payWithWeChat() {
            this.$parent.formSubmit(1);
            this.close()
        },
        payWithBalance() {
            this.$parent.formSubmit(0);
            this.close()
        },

    },
};
</script>

<style scoped>
.payment-container {
    z-index: 1100;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5); /* Dimmed background for focus */
}

.widget-center {
    z-index: 1100;
    background-color: #ffffff;
    border-radius: 20rpx;
    padding: 40rpx 30rpx;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    width: 80%;
    text-align: center;
}

.payment-header {
    font-size: 34rpx;
    font-weight: bold;
    margin-bottom: 20rpx;
}

.payment-methods {
    margin-top: 20rpx;
}

.payment-method-title {
    font-size: 28rpx;
    margin-bottom: 10rpx;
}

.payment-button {
    width: 100%;
    padding: 10rpx 0;
    background-color: #feb327;
    color: #fff;
    border: none;
    border-radius: 30rpx;
    margin-bottom: 10rpx;
    font-size: 32rpx;
}

</style>