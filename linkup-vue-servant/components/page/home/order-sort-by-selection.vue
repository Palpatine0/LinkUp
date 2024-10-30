<template>
<div class="payment-container">
    <div class="mask" @click="close()"></div>
    <div class="widget-center coup-anim">
        <app-button shaped size="large" @click="setOrderSortBy(0)">{{$t('home.orderSortBy.distance')}}</app-button>
        <app-button shaped size="large" @click="setOrderSortBy(1)">{{$t('home.orderSortBy.price')}}</app-button>
    </div>
</div>
</template>

<script>
export default {
    props: {user: Object, balanceAdequate: Boolean},
    methods: {
        close() {
            this.$parent.paymentMethodSelectionToggle(false);
        },

        setOrderSortBy(type) {
            this.$parent.setOrderSortBy(type);
            this.$parent.orderSortBySelectionToggle();
        },
    },
};
</script>

<style scoped>
.payment-container {
    z-index: 1000;
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