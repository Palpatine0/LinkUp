<template>
<div class="page" style="background-color: #f3f2f6;">

    <!-- User Info Section -->
    <div class="user-info">
        <div class="profile-picture">
            <image :src="user.avatar" mode="aspectFill" class="profile-img"></image>
        </div>
        <div class="user-details">
            <text class="user-name">{{ user.nickname }}</text>
            <div class="points-balance">
                <image src="/static/page/balance/coins.svg" class="points-icon"></image>
                <text class="user-points">{{ user.credit }}</text>
            </div>
        </div>
    </div>

    <!-- Wallet and Balance Section -->
    <div class="wallet-balance">
        <div class="wallet-info">
            <div class="balance-card">
                <div class="balance-details">
                    <text style="font-size: 14px">{{$t('profile>balance.balance')}}</text>
                    <text class="balance-amount">¥ {{ $common.toNumber(user.balance, 'bigdecimal').toFixed(2) }}</text>
                </div>
                <div class="pay-button" @click="topUpToggle()">{{$t('profile>balance.deposit')}}</div>
            </div>
        </div>
    </div>

    <!-- Orders and Share QR Code Section -->
    <!--<div class="myorders-shareqrcode">
        <div class="wrapper" @click="orderHistoryRedirect">
            <div class="content">
                <text style="font-weight: bold;">我的订单</text>
                <br>
                <text class="div-more">点击查看</text>
            </div>
            <div class="icon-wrapper">
                <image class="icon" src="/static/tab/history.jpg"></image>
            </div>
        </div>

        &lt;!&ndash; Vertical Divider Line &ndash;&gt;
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
    </div>-->

    <!--widgets-->
    <TopUp v-if="topUpVisible" :userInfo="user"></TopUp>
    <!--<QrShare v-if="qeShareVisible" :userInfo="user"></QrShare>-->
</div>
</template>

<script>
// import QrShare from "../../components/qrShare.vue";
import TopUp from "../../../components/page/balance/top-up.vue";

export default {
    components: {
        TopUp,
        // QrShare,
    },
    data() {
        return {
            user: {},

            userBalance: 0.00,
            qeShareVisible: false,
            topUpVisible: false,
        };
    },
    onLoad() {
        this.getUser();
    },
    methods: {
        getUser() {
            uni.request({
                url: getApp().globalData.data.requestUrl + '/user/search',
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
        topUpToggle() {
            this.topUpVisible = !this.topUpVisible
        }
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
}

.profile-picture {
    margin-right: 20rpx;
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
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
}

.points-balance {
    display: flex;
    align-items: center;
    margin-top: 10rpx;
}

.points-icon {
    width: 30rpx;
    height: 30rpx;
    margin-right: 10rpx;
}

.user-points {
    font-size: 36rpx;
    color: #f8be23;
}

/* Wallet and Balance Section */
.wallet-balance {
    background-color: #fac322;
    padding: 20rpx;
    border-radius: 45rpx;
    margin-bottom: 20rpx;
    height: 160px;
}

.balance-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 70px;
    padding: 18px;
}

.balance-details {
    display: flex;
    flex-direction: column;
}

.balance-amount {
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
    margin-top: 10rpx;
}

.pay-button {
    background-color: #fbd83f;
    color: white;
    padding: 10rpx 30rpx;
    border-radius: 50rpx;
    font-size: 44rpx;
    margin-top: 20rpx;
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