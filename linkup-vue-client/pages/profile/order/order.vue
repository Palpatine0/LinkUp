<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('profile>order.myOrders') }}</app-title>
        <img src="/static/common/create.svg" style="width: 28px; height: 28px;" @click="orderInitiateRedirect"/>
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
        <div
            class="app-container"
            v-for="order in orderList"
            :key="order.id"
            @click="orderDetailRedirect(order.id)"
        >
            <div class="order-content">
                <div style="width: 100%;">
                    <div style="display: flex; align-items: center;">
                        <app-title bold="true" type="h3" style="width: 330px;">
                            {{ language != "zh-Hans" ? order.title : order.titleCn }}
                        </app-title>
                    </div>
                    <div class="order-detail">
                        <div class="highlight-blue">
                            {{ $t('profile>order.candidates') }}: {{ order.candidateCount }}
                        </div>
                        <span style="font-size: 14px; color: gray;">{{ order.createdAt }}</span>
                        <div style="display:flex;justify-content: space-between;">
                            <span style="font-size: 14px; color: gray;">{{ order.identifier }}</span>
                            <div v-if="order.status==0" class="flex">
                                <span class="status-dot yellow-dot"></span>
                                <div class="status-text">{{ $t('profile>order.pending') }}</div>
                            </div>
                            <div v-if="order.status==1" class="flex">
                                <span class="status-dot green-dot"></span>
                                <div class="status-text">{{ $t('profile>order.confirmed') }}</div>
                            </div>
                            <div v-if="order.status==2" class="flex">
                                <span class="status-dot gray-dot"></span>
                                <div class="status-text">{{ $t('profile>order.completed') }}</div>
                            </div>
                            <div v-if="order.status==3" class="flex">
                                <div class="status-dot red-dot"></div>
                                <div class="status-text">{{ $t('profile>order.canceled') }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="loading" class="loading-text">{{ $t('pub.page.loading') }}</div>
        <div v-else-if="!hasMore" class="no-more-data-container-list">{{ $t('pub.page.noMoreData') }}</div>
    </scroll-view>
</div>
</template>


<script>
import orderDetail from './order-detail/order-detail.vue';

export default {
    data() {
        return {
            orderList: [],
            searchKeyword: '',
        };
    },
    onShow() {
        this.reload()
    },
    methods: {
        reload(){
            this.resetPagination();
            this.getDataList();
        },
        buildApiParams() {
            let url = getApp().globalData.data.requestUrl + this.$API.order.search;
            let method = 'POST';
            let baseData = {
                clientId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                page: this.page,
                size: this.pageSize,
            };
            let data = {};

            if (this.searchKeyword && this.searchKeyword.trim() !== '') {
                data = {
                    ...baseData,
                    keyword: this.searchKeyword,
                };
            } else {
                data = {
                    ...baseData,
                };
            }
            return {url, method, data};
        },
        onSearchInput() {
            this.reload();
        },
        getDataList() {
            if (this.loading || !this.hasMore||this.$common.isEmpty(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)) return;

            this.loading = true;

            const {url, method, data} = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    const orders = res.data.list;
                    if (this.page === 1) {
                        this.orderList = [];
                    }
                    if (orders.length < this.pageSize) {
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

        // Redirects
        orderInitiateRedirect() {
            uni.navigateTo({
                url: '/pages/profile/order/order-servant-selection/order-servant-selection',
            });
        },
        orderDetailRedirect(orderId) {
            uni.navigateTo({
                url: '/pages/profile/order/order-detail/order-detail?orderId=' + orderId,
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

.order-detail {
    margin-top: 5px;
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
    margin-right: 12px;
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

.order-status {
    margin-right: 10px;
}

.highlight-blue {
    color: white;
    background-color: #007aff;
    border-radius: 5px;
    font-weight: bold;
    padding: 2px;
    width: 100px;
    font-size: 14px;
    margin-bottom: 4px;
    align-items: center;
    display: f;
}

</style>
