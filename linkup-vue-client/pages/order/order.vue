<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">我的订单</app-title>
        <img src="/static/order/add.svg" style="width: 28px; height: 28px;" @click="orderInitiateRedirect"/>
    </div>

    <!-- Search input -->
    <app-input mode="text" placeholder="搜索" col="12" class="mb-2" />
    <button @click="test">TEST</button>

    <!-- Orders Container using app-container -->
    <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh" class="mt-4">
        <div class="container" v-for="order in orders" :key="order.id" @click="orderDetailRedirect(order.id)">
            <div class="order-content">
                <div style="width: 100%;">
                    <div style="display: flex; align-items: center; justify-content: space-between;">
                        <app-title bold="true" type="h3">{{ order.title }}</app-title>
                        <span :class="['status-dot', order.completeStatus === 1 ? 'green-dot' : 'red-dot']"></span>
                    </div>
                    <div class="order-info">
                        <div class="respondent-count">
                            抢单人数: {{ order.respondentCount }}
                        </div>
                        <span style="font-size: 14px;color: gray;">{{ order.createTime }}</span>
                    </div>
                </div>
            </div>
        </div>
    </scroll-view>
</div>
</template>

<script>
import orderDetail from "./order-detail/order-detail.vue";

export default {
    data() {
        return {
            userProfileAvailable: false,
            orders: [
                {id: 1, title: 'VDqef', respondentCount: 143, createTime: '2024-10-10', completeStatus: 0},
                {id: 2, title: 'Gqef', respondentCount: 89, createTime: '2024-06-19', completeStatus: 1},
                {id: 3, title: 'JGwefew', respondentCount: 89, createTime: '2024-06-19', completeStatus: 1},
                {id: 4, title: 'Fwdw', respondentCount: 89, createTime: '2024-06-19', completeStatus: 0},
                {id: 5, title: 'Bwqeaefcdc', respondentCount: 89, createTime: '2024-06-19', completeStatus: 1},
                {id: 6, title: 'Jrrbhv', respondentCount: 89, createTime: '2024-06-19', completeStatus: 0},
                {id: 7, title: 'Vhncxb', respondentCount: 89, createTime: '2024-06-19', completeStatus: 0},
            ]
        };
    },
    onLoad() {
        this.checkUserInfo()
    },
    computed: {
        orderDetail() {
            return orderDetail
        }
    },
    methods: {
        getUserInfo(e) {
            var openid = "";
            uni.login({
                provider: 'weixin',
                success: (loginRes) => {
                    const {code} = loginRes;
                    uni.request({
                        url: getApp().globalData.requestUrl + '/user/saveUserAuthInfo',
                        method: 'POST',
                        data: {
                            code: code
                        },
                        success: (res) => {
                            const dataSet = res.data.userInfo;
                            if (dataSet.openid == null) {
                                uni.showToast({title: '授权失败', icon: 'none'});
                            } else {
                                uni.setStorageSync('openid', dataSet.openid);
                                uni.setStorageSync('sessionKey', dataSet.openid.session_key);
                                uni.setStorageSync('unionid', dataSet.unionid);
                                uni.setStorageSync('uid', dataSet.id);
                                uni.getUserInfo({
                                    success: function (res) {
                                        var userInfo = res.userInfo
                                        var nickname = userInfo.nickName
                                        var avatar = userInfo.avatarUrl
                                        uni.request({
                                            url: getApp().globalData.requestUrl + '/client/saveUserInfo',
                                            method: 'POST',
                                            data: {
                                                openid: dataSet.openid,
                                                nickname: nickname,
                                                avatar: avatar
                                            },
                                            success: (res) => {
                                                uni.setStorageSync('nickname', nickname);
                                                uni.setStorageSync('avatar', avatar);
                                                const storageInfo = uni.getStorageInfoSync();
                                                this.hasUserInfo = true
                                            },
                                        });
                                    }
                                })
                                this.userProfileAvailable = true
                                uni.showToast({title: '授权成功', icon: 'none'});
                            }
                        },
                        fail: () => {
                            uni.showToast({title: '请求失败', icon: 'none'});
                        }
                    });
                },
                fail: (err) => {
                    uni.showToast({title: '登录失败', icon: 'none'});
                }
            });

        },

        orderInitiateRedirect() {
            uni.navigateTo({
                url: '/pages/order/order-servant-selection/order-servant-selection'
            });
        },
        orderDetailRedirect(oid) {
            uni.navigateTo({
                url: '/pages/order/order-detail/order-detail?oid=' + oid
            });
        },

        test() {
            uni.request({
                url: getApp().globalData.requestUrl + '/client/save',
                method: 'POST',
                data: {
                    openid: "openid",
                    nickname: "nickname"
                },
                success: (res) => {

                },
                fail: () => {
                    uni.showToast({title: '请求失败', icon: 'none'});
                }
            });
        }
    }
};
</script>

<style scoped>
.order-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.order-icon {
    width: 40px;
    height: 40px;
    margin-right: 10px;
}

.order-info {
    margin-top: 5px;
}

.status-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-left: 10px;
}

.green-dot {
    background-color: #27b459;
}

.red-dot {
    background-color: red;
}

.order-status {
    margin-right: 10px;
}

.respondent-count {
    color: white;
    background-color: #007aff;
    border-radius: 5px;
    font-weight: bold;
    padding: 2px;
    width: 92px;
    font-size: 14px;
    margin-bottom: 4px;
}
</style>