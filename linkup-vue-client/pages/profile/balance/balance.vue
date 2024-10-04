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
    <div class="transactions-shareqrcode">
        <div class="wrapper" @click="orderHistoryRedirect">
            <div class="content">
                <text style="font-weight: bold;">{{ $t('profile>balance.transactionHistory') }}</text>
                <br>
                <text class="div-more">{{ $t('profile>balance.viewMore') }}</text>
            </div>
            <div class="icon-wrapper center-v">
                <img class="icon" src="/static/page/profile/balance/money-from-bracket.svg"></img>
            </div>
        </div>

        <!-- Vertical Divider Line -->
        <div class="vertical-moulding"></div>

        <div class="wrapper" @click="qeShareToggle">
            <div class="content">
                <text style="font-weight: bold;">{{ $t('profile>balance.qrCode') }}</text>
                <br>
                <text class="div-more">{{ $t('profile>balance.viewMore') }}</text>
            </div>
            <div class="icon-wrapper center-v">
                <img class="icon" src="/static/page/profile/balance/qrcode.svg"></img>
            </div>
        </div>
    </div>

    <!--widgets-->
    <Deposit v-if="depositVisible" :userInfo="user"></Deposit>
    <Withdraw v-if="withdrawVisible" :userInfo="user"></Withdraw>
    <ReferralQRCode v-if="referralQRVisible" :userInfo="user"></ReferralQRCode>
</div>
</template>

<script>
import ReferralQRCode from "../../../components/page/balance/referralQRCode.vue";
import Deposit from "../../../components/page/balance/deposit.vue";
import Withdraw from "../../../components/page/balance/withdraw.vue";

export default {
    components: {
        Deposit,
        Withdraw,
        ReferralQRCode,
    },
    data() {
        return {
            user: {},
            userBalance: 0.00,

            referralQRVisible: false,
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

        // Toggle
        qeShareToggle() {
            this.referralQRVisible = !this.referralQRVisible
        },
        depositToggle() {
            this.depositVisible = !this.depositVisible
        },
        withdrawToggle() {
            this.withdrawVisible = !this.withdrawVisible
        },

        // Redirect
        orderHistoryRedirect() {
            uni.navigateTo({
                url: '/pages/profile/balance/transaction-history/transaction-history'
            })
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
    padding: 2px;
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
.transactions-shareqrcode {
    display: flex;
    padding: 15px;
    border-radius: 45rpx;
    margin-bottom: 20rpx;
    justify-content: space-between;
    background-color: #f3f2f6;
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
    width: 1px;
    background: #d0d0d0;
    align-self: stretch; /* This makes the vertical line match the parent’s height */
}
</style>