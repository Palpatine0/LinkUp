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

                        <div style="display:flex;justify-content: space-between;">
                            <span style="font-size: 14px; color: gray;">{{ order.createdAt }}</span>
                            <div v-if="order.status==0" class="flex">
                                <span class="status-dot yellow-dot"></span>
                                <div class="status-text">{{ $t('profile>order.pending') }}</div>
                            </div>
                            <div v-if="order.status==1&&isServiceInProgressState(order)" class="flex">
                                <span class="status-dot green-dot"></span>
                                <div class="status-text">{{ $t('profile>order.processing') }}</div>
                            </div>
                            <div v-if="order.status==1&&!isServiceInProgressState(order)" class="flex">
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
export default {
    data() {
        return {
            orderList: [],
            searchKeyword: '',
            countdowns:{}
        };
    },
    onShow() {
        this.reload()
    },
    methods: {
        reload() {
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

            if(this.searchKeyword && this.searchKeyword.trim() !== '') {
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
            const {url, method, data} = this.buildApiParams();
            this.loading = true;

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    const orders = res.data.list;

                    // If on first page, reset the list
                    if (this.page === 1) {
                        this.orderList = [];
                    }
                    this.orderList = this.orderList.concat(orders);

                    // Process the 'createdAt' and start the countdown
                    this.orderList.forEach((order) => {
                        order.createdAt = order.createdAt ? this.$common.stampToTime(order.createdAt) : '';

                        if (order.serviceScheduleStart && order.status === 1 && !this.isServiceInProgressState(order)) {
                            this.startCountdown(order);  // This will now refer to the component's method correctly
                        }
                    });

                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },
        startCountdown(order) {
            const serviceStartTime = new Date(order.serviceScheduleStart).getTime();
            const currentTime = new Date().getTime();

            // Only start countdown if current time is less than the service start time
            if(currentTime < serviceStartTime) {
                this.$common.calculateCountdown(currentTime, serviceStartTime, (formattedTime) => {
                    this.$set(this.countdowns, order.id, formattedTime);
                });
            } else {
                this.$set(this.countdowns, order.id, "00:00:00");
            }
        },
        isServiceInProgressState(order) {
            if(order.serviceScheduleStart && order.serviceScheduleEnd) {
                const currentTime = new Date().getTime();
                const serviceStartTime = new Date(order.serviceScheduleStart).getTime();
                const serviceEndTime = new Date(order.serviceScheduleEnd).getTime();
                if(currentTime >= serviceStartTime && currentTime <= serviceEndTime) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        },

        // Redirects
        orderInitiateRedirect() {
            uni.navigateTo({
                url: './order-servant-selection/order-servant-selection',
            });
        },
        orderDetailRedirect(orderId) {
            uni.navigateTo({
                url: './order-detail/order-detail?orderId=' + orderId,
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
