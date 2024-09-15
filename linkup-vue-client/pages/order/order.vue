<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">我的订单</app-title>
        <img src="/static/order/add.svg" style="width: 28px; height: 28px;" @click="orderInitiateRedirect" />
    </div>

    <!-- Search input -->
    <app-input
        mode="text"
        placeholder="搜索"
        col="12"
        class="mb-2"
        v-model="searchKeyword"
        @input="onSearchInput"
    />

    <!-- Orders Container using app-container -->
    <scroll-view
        :scroll-top="0"
        scroll-y="true"
        style="height: 80vh"
        class="mt-4"
        @scrolltolower="onReachBottom"
    >
        <div
            class="app-container"
            v-for="order in orderList"
            :key="order.id"
            @click="orderDetailRedirect(order.id)"
        >
            <div class="order-content">
                <div style="width: 100%;">
                    <div style="display: flex; align-items: center; justify-content: space-between;">
                        <app-title bold="true" type="h3">{{ order.title }}</app-title>
                        <span :class="['status-dot', order.status === 1 ? 'green-dot' : 'red-dot']"></span>
                    </div>
                    <div class="order-info">
                        <div class="respondent-count">
                            抢单人数: {{ order.candidateCount }}
                        </div>
                        <span style="font-size: 14px; color: gray;">{{ order.createdAt }}</span>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="loading" style="color: gainsboro; margin-left: 10px;">加载中...</div>
        <!-- No More Data Message -->
        <div v-else-if="!hasMore" style="color: gainsboro; margin-left: 10px;">已加载全部数据</div>
    </scroll-view>
</div>
</template>

<script>
import orderDetail from './order-detail/order-detail.vue';
import common from '../../utils/common';
import paginationMixin from '../../utils/paginationMixin'; // Adjust the path as necessary

export default {
    mixins: [paginationMixin],
    data() {
        return {
            userProfileAvailable: false,
            orderList: [],
            searchKeyword: '',
        };
    },
    onLoad() {
        this.checkUserInfo();
        this.resetPagination();
        this.getOrderList();
    },
    computed: {
        orderDetail() {
            return orderDetail;
        },
    },
    methods: {
        // User handling
        checkUserInfo() {
            const openid = uni.getStorageSync('openid');
            if (common.isEmpty(openid)) {
                this.userProfileAvailable = false;
                uni.showModal({
                    title: '授权',
                    content: '请授权您的个人信息以使用完整服务',
                    showCancel: true,
                    confirmText: '授权',
                    success: (res) => {
                        if (res.confirm) {
                            this.getUserInfo();
                        }
                    },
                });
            } else {
                this.userProfileAvailable = true;
            }
        },
        async getUserInfo(e) {
            uni.showLoading({ title: '加载中' });
            const getUserLoginCode = () => {
                return new Promise((resolve) =>
                    uni.login({
                        provider: 'weixin',
                        success: (res) => {
                            resolve(res.code);
                        },
                        fail: () => {
                            uni.showToast({ title: '用户Code获取失败', icon: 'none' });
                        },
                    })
                );
            };
            const userInfoCode = await getUserLoginCode();
            const getUserAccountData = () => {
                return new Promise((resolve) => {
                    uni.request({
                        url: getApp().globalData.requestUrl + '/user/save-auth-info',
                        method: 'POST',
                        data: {
                            code: userInfoCode,
                        },
                        success: (res) => {
                            if (res.data.auth == null) {
                                uni.showToast({ title: '授权失败', icon: 'none' });
                            } else {
                                resolve(res.data.auth);
                            }
                        },
                        fail: () => {
                            uni.showToast({ title: '授权请求失败', icon: 'none' });
                        },
                    });
                });
            };

            const userAccountData = await getUserAccountData();
            const getUserData = () => {
                return new Promise((resolve) => {
                    uni.getUserInfo({
                        success: function (res) {
                            resolve(res.userInfo);
                        },
                    });
                });
            };

            const userData = await getUserData();
            uni.request({
                url: getApp().globalData.requestUrl + '/user/update',
                method: 'POST',
                data: {
                    // userAccountData
                    id: userAccountData.id,
                    openid: userAccountData.openid,
                    sessionKey: userAccountData.openid.sessionKey,
                    unionid: userAccountData.unionid,
                    // userData
                    role: 1,
                    nickname: userData.nickName,
                    gender: userData.gender,
                    avatar: userData.avatarUrl,
                },
                success: () => {
                    // userAccountData
                    uni.setStorageSync('userId', userAccountData.id);
                    uni.setStorageSync('openid', userAccountData.openid);
                    uni.setStorageSync('sessionKey', userAccountData.openid.session_key);
                    uni.setStorageSync('unionid', userAccountData.unionid);
                    // userData
                    uni.setStorageSync('nickname', userData.nickName);
                    uni.setStorageSync('avatar', userData.avatarUrl);
                    uni.setStorageSync('gender', userData.gender);
                    this.userProfileAvailable = true;
                    uni.hideLoading();
                    uni.showToast({ title: '授权成功', icon: 'none' });
                },
                fail: () => {
                    uni.showToast({ title: '授权失败', icon: 'none' });
                },
            });
        },

        // Fetch order list
        getOrderList() {
            if (this.loading || !this.hasMore) return;

            this.loading = true;

            const { url, method, data } = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    const orders = res.data.orderList;
                    if (this.page === 1) {
                        this.orderList = [];
                    }
                    if (orders.length < this.size) {
                        this.hasMore = false;
                    }
                    // Append new orders to the list
                    this.orderList = this.orderList.concat(orders);
                    // Process 'createdAt' fields
                    this.orderList.forEach((order) => {
                        order.createdAt = order.createdAt ? this.$common.stampToTime(order.createdAt) : '';
                    });
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },

        // Build API parameters based on search keyword
        buildApiParams() {
            let url = '';
            let method = 'GET';
            let data = {};

            if (this.searchKeyword && this.searchKeyword.trim() !== '') {
                // Use the search endpoint
                url = getApp().globalData.requestUrl + '/order/search';
                data = {
                    userId: uni.getStorageSync('userId'),
                    keyword: this.searchKeyword,
                    page: this.page,
                    size: this.size,
                };
            } else {
                // Use the get-all-by-user-id endpoint
                url = getApp().globalData.requestUrl + '/order/search';
                method = 'POST';
                data = {
                    userId: uni.getStorageSync('userId'),
                    page: this.page,
                    size: this.size,
                };
            }

            return { url, method, data };
        },

        // Handle search input changes
        onSearchInput() {
            console.log("onSearchInput() {onSearchInput() {onSearchInput() {")
            this.resetPagination();
            this.getOrderList();
        },

        // Redirects
        orderInitiateRedirect() {
            uni.navigateTo({
                url: '/pages/order/order-servant-selection/order-servant-selection',
            });
        },
        orderDetailRedirect(orderId) {
            uni.navigateTo({
                url: '/pages/order/order-detail/order-detail?orderId=' + orderId,
            });
        },
    },
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
