<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('profile>order.myOrders') }}</app-title>
    </div>

    <!-- Search input -->
    <app-input
        mode="text"
        :placeholder="$t('pub.page.search')"
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
        @scrolltoupper="reload"
        @scrolltolower="onReachBottom"
    >
        <div class="app-container" v-for="order in orderList" :key="order.id" @click="orderDetailRedirect(order.id)" style="display: flex;flex-direction: column;align-items: center">
            <div class="order-schedule">
                <div>{{ language != "zh-Hans" ? 'Service Schedule: ' : "服务时间" }}</div>
                <div>
                    {{ $common.stampToTime(order.serviceScheduleStart, {yyyy: false, ss: false}) }}
                    -
                    {{ $common.stampToTime(order.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false}) }}
                </div>
            </div>
            <div class="service-type-price">
                <app-title bold="true" type="h3">{{ language != "zh-Hans" ? order.serviceTypeName + ' Service' : order.serviceTypeNameCn + "服务" }}</app-title>
                <app-title type="h3">¥{{ order.price }}</app-title>
            </div>
            <div class="order-address">
                <div>{{order.address.addressName}}</div>
                <div>{{order.address.detail}}</div>
            </div>
            <div class="chat-user-order-status">
                <img style="width: 28px;height: 22px" src="/static/page/order/messages.svg" @click="chatWindowRedirect(order.clientId)">
                <div v-if="order.status == 0" class="order-status">
                    <span class="status-dot yellow-dot"></span>
                    <div class="status-text">{{ $t('profile>order.pending') }}</div>
                </div>
                <div v-if="order.status == 1 && isServiceInProgressState(order)" class="order-status">
                    <span class="status-dot green-dot"></span>
                    <div class="status-text">{{ $t('profile>order.processing') }}</div>
                </div>
                <div v-if="order.status == 1 && !isServiceInProgressState(order)" class="order-status">
                    <span class="status-dot green-dot"></span>
                    <div class="status-text">{{ $t('profile>order.confirmed') }}</div>
                </div>
                <div v-if="order.status == 2" class="order-status">
                    <span class="status-dot gray-dot"></span>
                    <div class="status-text">{{ $t('profile>order.completed') }}</div>
                </div>
                <div v-if="order.status == 3" class="order-status">
                    <div class="status-dot red-dot"></div>
                    <div class="status-text">{{ $t('profile>order.canceled') }}</div>
                </div>
            </div>
        </div>
        <div v-if="loading" class="loading-text">{{ $t('pub.page.loading') }}</div>
        <div v-else-if="!hasMore" class="no-more-data-container-list">{{ $t('pub.page.noMoreData') }}</div>
    </scroll-view>
</div>
</template>


<script>

import $common from "../../utils/common";

export default {
    name: "order",
    computed: {
        $common() {
            return $common
        }
    },
    data() {
        return {
            orderList: [],
            searchKeyword: '',
            serviceTypeMap: {},
            addressMap: {},
        };
    },
    onShow() {
        this.reload();
    },
    methods: {
        async reload() {
            this.resetPagination();
            await this.getServantTypeList();
            this.getDataList();
        },
        resetPagination() {
            this.page = 1;
            this.hasMore = true;
            this.orderList = [];
        },
        buildApiParams() {
            let url = getApp().globalData.data.requestUrl + this.$API.order.search;
            let method = 'POST';
            let baseData = {
                servantId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                page: this.page,
                size: this.pageSize,
            };
            let data = this.searchKeyword.trim() !== '' ? {...baseData, keyword: this.searchKeyword} : baseData;
            return {url, method, data};
        },
        onSearchInput() {
            this.reload();
        },
        async getDataList() {
            if(this.loading || !this.hasMore || this.$common.isEmpty(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)) return;

            this.loading = true;
            const {url, method, data} = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    const orders = res.data.list;
                    if(this.page === 1) this.orderList = [];

                    // Check if there are more pages
                    if(orders.length < this.pageSize) {
                        this.hasMore = false;
                    }

                    // Append new orders to the list and map servant type names
                    orders.forEach((order) => {
                        // Convert timestamp to readable time
                        order.createdAt = order.createdAt ? this.$common.stampToTime(order.createdAt) : '';

                        // Add service type name and nameCn to the order
                        if(this.serviceTypeMap[order.requiredServantType]) {
                            order.serviceTypeName = this.serviceTypeMap[order.requiredServantType].name;
                            order.serviceTypeNameCn = this.serviceTypeMap[order.requiredServantType].nameCn;
                        }

                    });

                    // Update the order list
                    this.orderList = this.orderList.concat(orders);

                    // Increment the page number for the next call
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },
        async getServantTypeList() {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.serviceType.search,
                    method: 'POST',
                    data: {},
                    success: (res) => {
                        res.data.list.forEach((serviceType) => {
                            // Populate the serviceTypeMap
                            this.serviceTypeMap[serviceType.id] = {
                                name: serviceType.name,
                                nameCn: serviceType.nameCn,
                            };
                        });
                        resolve(); // Resolve the promise after fetching the data
                    },
                    fail: (error) => {
                        console.error("Failed to fetch servant type list:", error);
                        reject(error); // Reject the promise in case of failure
                    },
                });
            });
        },
        isServiceInProgressState(order) {
            if(order.serviceScheduleStart && order.serviceScheduleEnd) {
                const currentTime = new Date().getTime();
                const serviceStartTime = new Date(order.serviceScheduleStart).getTime();
                const serviceEndTime = new Date(order.serviceScheduleEnd).getTime();
                return currentTime >= serviceStartTime && currentTime <= serviceEndTime;
            }
            return false;
        },

        // Redirects
        orderDetailRedirect(orderId) {
            uni.navigateTo({
                url: './order-detail/order-detail?orderId=' + orderId,
            });
        },
        chatWindowRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/chat/chat-window/chat-window?contactId=' + userId
            });
        },
    },
};
</script>


<style scoped>
.order-icon {
    width: 40px;
    height: 40px;
    margin-right: 10px;
}

.order-schedule {
    width: 100%;
    padding: 2px 8px;
    margin: 0 0 8px 0;
    background-color: white;
    border-radius: 50px;
    justify-content: space-between;
    display: flex;
}

.service-type-price {
    width: 100%;
    display: flex;
    justify-content: space-between
}

.order-address{
    width: 100%;
    display: flex;
    flex-direction: column;
    background-color: #2676f7;
    border-radius: 10px;
    padding: 6px;
    color: white;
    font-weight: bold;
    margin: 8px 0;
}

.chat-user-order-status {
    margin-top: 5px;
    display: flex;
    width: 100%;
    justify-content: space-between;
}

.status-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-left: 10px;
    margin-top: 2px;
}

.status-text {
    position: relative;
    bottom: 4px;
    left: 5px;
}

.order-status{
    display: flex;
    margin-top: 4px;
}

.yellow-dot {
    background-color: #feb327;
}

.green-dot {
    background-color: #27b459;
}

.red-dot {
    background-color: red;
}

.gray-dot {
    background-color: #8c8c8c;
}

.highlight-blue {
    color: white;
    background-color: #007aff;
    border-radius: 5px;
    font-weight: bold;
    padding: 2px;
    font-size: 14px;
    margin-bottom: 4px;
    align-items: center;
    display: flex;
}

</style>
