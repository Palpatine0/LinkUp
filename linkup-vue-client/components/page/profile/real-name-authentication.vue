<template>
<div class="payment-container">
    <div class="mask" @click="close()"></div>
    <div class="widget-center coup-anim">
        <div class="title">{{ $t('component>profile>realNameAuthentication.realNameAuthentication') }}</div>
        <app-input
            mode="text"
            col="12"
            class="mb-2"
            :placeholder="$t('component>profile>realNameAuthentication.realName')"
            v-model="realName"
        />
        <app-input
            mode="text"
            col="12"
            class="mb-2"
            :placeholder="$t('component>profile>realNameAuthentication.idCard')"
            v-model="idCardNumber"
        />
        <app-button>{{ $t('pub.button.confirm') }}</app-button>

    </div>
</div>
</template>

<script>
export default {
    name: "real-name-authentication",
    props: {
        user: Object,
        realName: '',
        idCardNumber: '',
    },
    methods: {
        close() {
            this.$parent.realNameAuthenticationToggle();
            this.$parent.formSubmit()
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

.title {
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