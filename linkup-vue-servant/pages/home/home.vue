<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('home.nearby') }}</app-title>
    </div>

    <!-- Search input -->
    <app-input mode="text" :placeholder="$t('pub.page.search')" col="12" class="mb-2" v-model="searchKeyword" @input="onSearchInput"/>
    <scroll-view :scroll-top="0" scroll-x="true" class="order-type-selection" @scrolltoupper="reload" @scrolltolower="onReachBottom">
        <div class="button-container">
            <div :class="{ active: orderStatusType === -1 }" @click="setOrderStatusType(-1)"><span>{{ $t('home.orderStatusType.all') }}</span></div>
            <div :class="{ active: orderStatusType === 1 }" @click="setOrderStatusType(1)"><span>{{ $t('home.orderStatusType.accepted') }}</span></div>
            <div :class="{ active: orderStatusType === 2 }" @click="setOrderStatusType(2)"><span>{{ $t('home.orderStatusType.invalided') }}</span></div>
        </div>
    </scroll-view>

    <!-- Orders Container using app-container -->
    <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh" @scrolltoupper="reload" @scrolltolower="onReachBottom">
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
                <app-title bold="true" type="h3">{{ language != "zh-Hans" ? order.serviceType.name + ' Service' : order.serviceType.nameCn + "服务" }}</app-title>
                <app-title type="h3">¥{{ order.price }}</app-title>
            </div>
            <div class="order-address">
                <div>{{ order.address.addressName }}</div>
                <div>{{ order.address.detail }}</div>
                <div>
                    <p class="candidates">{{ $t('home.candidates') + ': ' + order.candidateCount }}</p>
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
    name: "home",
    computed: {
        $common() {
            return $common
        }
    },
    data() {
        return {
            user: uni.getStorageSync(getApp().globalData.data.userInfoKey),
            orderList: [],
            searchKeyword: '',
            addressMap: {},
            orderStatusType: -1,
        };
    },
    onShow(param) {
        this.reload();
        this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey);
        if(!this.$common.isEmpty(param) && !this.$common.isEmpty(param.referrerId)) {
            uni.setStorageSync('referrerId', param.referrerId);
        }
    },
    methods: {
        async reload() {
            this.resetPagination();
            this.getDataList();
        },
        buildApiParams() {
            let method = 'POST';
            let baseData = {
                page: this.page,
                size: this.pageSize,
            };

            if (this.orderStatusType === -1) {
                // Case 1: All Orders
                let url = getApp().globalData.data.requestUrl + this.$API.order.search;
                baseData.status = 0; // Pending status
                baseData.servantId = null; // Not assigned
                baseData.userGender = this.user.gender;
                baseData.userAge = this.user.age;
                if (this.searchKeyword.trim() !== '') {
                    baseData.keyword = this.searchKeyword.trim();
                }
                return { url, method, data: baseData };
            } else if (this.orderStatusType === 1 || this.orderStatusType === 2) {
                // Case 2 and 3: Use new API endpoint
                let url = getApp().globalData.data.requestUrl + this.$API.order.servantOrders;
                baseData.servantId = this.user.id;
                baseData.statusType = this.orderStatusType;
                if (this.searchKeyword.trim() !== '') {
                    baseData.keyword = this.searchKeyword.trim();
                }
                return { url, method, data: baseData };
            }
        },
        onSearchInput() {
            this.reload();
        },
        async getDataList() {
            if (this.loading || !this.hasMore || this.$common.isEmpty(this.user.id)) return;

            this.loading = true;
            const { url, method, data } = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: async (res) => {
                    const orders = res.data.list;
                    if (this.page === 1) this.orderList = [];

                    if (orders.length < this.pageSize) {
                        this.hasMore = false;
                    }

                    // Process orders if needed
                    orders.forEach((order) => {
                        order.createdAt = order.createdAt ? this.$common.stampToTime(order.createdAt) : '';
                    });

                    this.orderList = this.orderList.concat(orders);
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },
        setOrderStatusType(type) {
            this.orderStatusType = type;
            this.reload();
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
.order-icon {
    width: 40px;
    height: 40px;
    margin-right: 10px;
}

.order-schedule {
    font-weight: bold;
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

.order-address {
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

.order-status {
    display: flex;
    margin-top: 4px;
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

.candidates {
    width: 30%;
    color: #000;
    background-color: #FFF;
    border-radius: 5px;
    font-weight: bold;
    padding: 2px;
    font-size: 14px;
    align-items: center;
    display: flex;
    margin: 2px 0;
}

.order-type-selection {
    display: flex;
    border-radius: 19px;
    height: 38px;
    overflow-x: auto; /* Allow horizontal scrolling */
}

.button-container {
    display: flex;
    flex-grow: 1; /* Allow buttons to grow and fill space */
}

.order-type-selection div {
    padding: 4px;
    border-radius: 50px;
    font-weight: bold;
    cursor: pointer;
}

.order-type-selection div.active {
    background-color: #0A2342;
    color: #FFF;
}

.order-type-selection div:not(.active) {
    color: #0A2342;
}

.order-type-selection span {
    padding: 0 8px;
}
</style>
