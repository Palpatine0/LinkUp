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
                <app-title bold="true" type="h3">{{ language != "zh-Hans" ? order.serviceTypeName + ' Service' : order.serviceTypeNameCn + "服务" }}</app-title>
                <app-title type="h3">¥{{ order.price }}</app-title>
            </div>
            <div class="order-address">
                <div>{{ order.address.addressName }}</div>
                <div>{{ order.address.detail }}</div>
            </div>
            <div class="chat-user-order-status">
                <span style="font-size: 14px; color: gray;">{{ order.createdAt }}</span>
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

            <div v-if="$common.isEmpty(order.servantId)&&order.candidateCount>0&&order.status != 3" style="width: 100%">
                <div class="divider"></div>
                <div class="order-candidate">
                    <div v-for="(servant, index) in topThreeServants(order.servants)" :key="servant.id" class="candidate-item">
                        <div class="candidate-info">
                            <img class="candidate-img" :src="servant.avatar" @click="chatWindowRedirect(servant.id)">
                            <div class="candidate-item-price">¥{{ servant.quotedPrice }}</div>
                        </div>
                    </div>
                    <div v-if="order.candidateCount > 3" class="candidate-item">
                        <div style="background-color: white;padding: 9px 11px;border-radius: 25px;position: relative;top: -12px;">+{{ order.candidateCount - 3 }}</div>
                    </div>
                </div>
            </div>

            <div v-if="!$common.isEmpty(order.servantId)" style="width: 100%">
                <div class="divider"></div>
                <div class="order-candidate">
                    <img style="width: 40px; height: 40px; border-radius: 50%; margin-right: 5px;" :src="order.servant.avatar" @click="chatWindowRedirect(order.servantId)">
                    <div v-if="order.requiredServantType == serviceTypeConstant.TOUR_GUIDE" class="center-v">
                        <div style="font-size: 18px">{{ $t('profile>order.tourGuide') }}: {{ order.servant.nickname }}</div>
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
import $common from "../../utils/common";

export default {
    computed: {
        $common() {
            return $common
        }
    },
    data() {
        return {
            searchKeyword: '',
            orderList: [],
            serviceTypeMap: {},
            countdowns: {},
            serviceTypeConstant: {
                TOUR_GUIDE: 1,
            }
        };
    },
    onShow() {
        if(!this.$common.isEmpty(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)){
            this.reload()
        }
    },
    methods: {
        async reload() {
            this.resetPagination();
            await this.getServantTypeList();
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
                success: async (res) => {
                    const orders = res.data.list;

                    // If on first page, reset the list
                    if(this.page === 1) {
                        this.orderList = [];
                    }
                    this.orderList = this.orderList.concat(orders);

                    // Process the 'createdAt' and start the countdown
                    for (const order of this.orderList) {
                        order.createdAt = order.createdAt ? this.$common.stampToTime(order.createdAt) : '';

                        if(this.serviceTypeMap[order.requiredServantType]) {
                            order.serviceTypeName = this.serviceTypeMap[order.requiredServantType].name;
                            order.serviceTypeNameCn = this.serviceTypeMap[order.requiredServantType].nameCn;
                        }

                        if(!this.$common.isEmpty(order.servantId)) {
                            order.servant = await this.getOrderServant(order.servantId)
                        } else {
                            if(order.candidateCount != 0) {
                                order.servants = await this.getOrderServantList(order.id)
                            }
                        }
                    }

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
        async getOrderServant(servantId) {
            const getServant = () => {
                return new Promise((resolve, reject) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.user.search,
                        method: 'POST',
                        data: {
                            id: servantId
                        },
                        success: (res) => {
                            resolve(res.data.list[0])
                        },
                    });
                })
            }
            return await getServant();
        },
        async getOrderServantList(orderId) {
            const getServantList = () => {
                return new Promise((resolve, reject) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.servants,
                        method: 'POST',
                        data: {
                            orderId: orderId
                        },
                        success: (res) => {
                            let servantList = res.data.list;

                            if(!this.$common.isEmpty(servantList)) {
                                const promises = servantList.map((user) => {
                                    return new Promise((resolve) => {
                                        uni.request({
                                            url: getApp().globalData.data.requestUrl + this.$API.userServant.search,
                                            method: 'POST',
                                            data: {
                                                userId: user.id
                                            },
                                            success: (res) => {
                                                user.servantData = res.data.list[0] || {};
                                                user.servantData.quotedPrice = user.quotedPrice;
                                                resolve();
                                            },
                                            fail: () => {
                                                user.servantData = {};
                                                resolve();
                                            }
                                        });
                                    });
                                });

                                Promise.all(promises).then(() => {
                                    this.$forceUpdate();
                                });
                            }

                            resolve(servantList)
                        },
                        fail: (error) => {
                            console.error("Failed to fetch servant list:", error);
                        }
                    });
                })
            }
            return await getServantList()
        },
        topThreeServants(servants) {
            if(!servants || servants.length === 0) return [];
            const sortedServants = servants.sort((a, b) => a.quotedPrice - b.quotedPrice);
            return sortedServants.slice(0, 3);
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
        chatWindowRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/chat/chat-window/chat-window?contactId=' + userId
            });
        },
    },
};
</script>

<style scoped>
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


.order-icon {
    width: 40px;
    height: 40px;
    margin-right: 10px;
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

.order-candidate {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    height: 40px;
}

.candidate-item {
    margin-right: 8px;
    display: flex;
    align-items: center;
}


.candidate-info {
    display: flex;
    flex-direction: column; /* Stack the image and price vertically */
    align-items: center; /* Align both image and price horizontally in the center */
    position: relative;
}

.candidate-img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-bottom: 5px; /* Add some space between the image and the price */
}

.candidate-item-price {
    color: #FFF;
    font-size: 11px;
    background-color: red;
    border-radius: 8px;
    padding: 0px 4px;
    font-weight: bold;
    position: relative;
    z-index: 100;
    position: relative;
    top: -18px;
    left: 10px;
}

.highlight-blue {
    color: white;
    background-color: #007aff;
    border-radius: 5px;
    font-weight: bold;
    padding: 4px 5px;
    font-size: 14px;
    margin-bottom: 4px;
    display: inline-flex;
    align-items: center;
}

.divider {
    width: 100%;
    height: 1px;
    background-color: #9e9e9e66;
    margin: 3px 0 8px 0;
}

</style>
