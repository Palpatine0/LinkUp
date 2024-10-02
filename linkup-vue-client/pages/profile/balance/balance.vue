<template>
<div class="page">

    <!-- User Info Section -->
    <div class="user-info">
        <div class="user-details">
            <div class="user-name">Hi, <span style="font-weight: bold">{{ user.nickname }}</span></div>
        </div>
        <image :src="user.avatar" mode="aspectFill" class="profile-img"></image>
    </div>

    <!-- Wallet and Balance Section -->
    <div class="wallet-balance">
        <div class="balance-info">
            <div style="font-size: 24px;font-weight: bold">{{ $t('profile>balance.balance') }}</div>
            <div class="balance-amount">{{ $common.toNumber(user.balance, 'bigdecimal').toFixed(2) }}</div>
        </div>
        <div class="balance-opts">
            <div class="pay-button" @click="depositToggle()">
                <div style="width: 70px">{{ $t('profile>balance.deposit') }}</div>
                <img class="pay-button-icon" src="/static/page/balance/arrow-down-left.svg">
            </div>
            <div class="pay-button" @click="withdrawToggle()">
                <div style="width: 70px">{{ $t('profile>balance.withdraw') }}</div>
                <img class="pay-button-icon" src="/static/page/balance/arrow-up-right.svg">
            </div>
        </div>
    </div>

    <!-- Orders and Share QR Code Section -->
    <div class="myorders-shareqrcode">
        <div class="wrapper" @click="orderHistoryRedirect">
            <div class="content">
                <text style="font-weight: bold;">我的交易记录</text>
                <br>
                <text class="div-more">点击查看</text>
            </div>
            <div class="icon-wrapper">
                <image class="icon" src="/static/tab/history.jpg"></image>
            </div>
        </div>

        <!-- Vertical Divider Line -->
        <div class="vertical-moulding"></div>

        <div class="wrapper" @click="qeShareToggle">
            <div class="content">
                <text style="font-weight: bold;">我的分享码</text>
                <br>
                <text class="div-more">点击查看</text>
            </div>
            <div class="icon-wrapper">
                <image class="icon" src="/static/tab/qrcode.svg"></image>
            </div>
        </div>
    </div>

    <!--widgets-->
    <Deposit v-if="depositVisible" :userInfo="user"></Deposit>
    <Withdraw v-if="withdrawVisible" :userInfo="user"></Withdraw>
    <!--<QrShare v-if="qeShareVisible" :userInfo="user"></QrShare>-->
</div>
</template>

<script>
// import QrShare from "../../components/qrShare.vue";
import Deposit from "../../../components/page/balance/deposit.vue";
import Withdraw from "../../../components/page/balance/withdraw.vue";

export default {
    components: {
        Deposit,
        Withdraw
        // QrShare,
    },
    data() {
        return {
            user: {},
            userBalance: 0.00,
            qeShareVisible: false,
            depositVisible: false,
            withdrawVisible: false,
        };
    },
    onLoad() {
        this.getUser();
    },
    methods: {
        getUser() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.search,
                method: 'POST',
                data: {
                    id: uni.getStorageSync(getApp().globalData.data.userInfoKey).id
                },
                success: (res) => {
                    this.user = res.data.list[0]
                },
            });
        },

        qeShareToggle() {
            this.qeShareVisible = !this.qeShareVisible
        },
        orderHistoryRedirect() {
            uni.navigateTo({
                url: '/pages/order-history/order-history'
            })
        },
        depositToggle() {
            this.depositVisible = !this.depositVisible
        },
        withdrawToggle() {
            this.withdrawVisible = !this.withdrawVisible
        },
    },

}
</script>

<style>
/* User Info Section */
.user-info {
    display: flex;
    align-items: center;
    padding: 20rpx;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
    justify-content: space-between;
}


.profile-img {
    width: 130rpx;
    height: 130rpx;
    border-radius: 50%;
}

.user-details {
    display: flex;
    flex-direction: column;
}

.user-name {
    font-size: 66rpx;
    color: #333;
}

.points-balance {
    display: flex;
    align-items: center;
    margin-top: 10rpx;
}


/* Wallet and Balance Section */
.wallet-balance {
    background-color: #2676f7;
    padding: 20rpx;
    border-radius: 70rpx;
    margin-bottom: 20rpx;
    height: 210px;
    color: white;
}

.balance-opts {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0px 14px;
}

.balance-info {
    padding: 15px;
    color: white;
}

.balance-amount {
    font-size: 82rpx;
    font-weight: bold;
    margin-top: 10rpx;
}

.pay-button {
    display: flex;
    background-color: white;
    color: #2676f7;
    font-weight: bold;
    padding: 23rpx 30rpx;
    border-radius: 50rpx;
    font-size: 35rpx;
    margin-top: 20rpx;
}

.pay-button-icon{
    width: 24px;
    height: 24px;
    margin: 2px 0 0 32px;
}

/* Orders and Favorites Section */
.myorders-shareqrcode {
    display: flex;
    padding: 26px;
    border-radius: 45rpx;
    margin-bottom: 20rpx;
    background-color: #fff;
    box-shadow: 0 5rpx 10rpx rgba(0, 0, 0, 0.05);
    justify-content: space-between;
}

.wrapper {
    justify-content: space-between;
    display: flex;
    width: 48%;
}

.content {
    margin-left: 12px;
}

.icon-wrapper {
    margin-right: 20rpx;
}

.icon {
    width: 40px;
    height: 40px;
}

.div-more {
    color: #888;
    font-size: 26rpx;
}

.vertical-moulding {
    float: left;
    width: 0.5px;
    height: 44px;
    background: #ececec;
}

</style>