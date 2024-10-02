<template>
<div class="page" style="background-color: #f3f2f6">
    <!-- Title -->
    <app-title type="h2" bold="true">{{ order.title }}</app-title>

    <!-- Price and Respondent Container -->
    <div class="app-container" style="background-color: white !important;">
        <div class="price-respondent-container">
            <!-- Price Section -->
            <div class="price-section">
                <app-title bold="true">{{ $t('profile>order>orderDetail.orderInfoBasic.price') }}</app-title>
                <p>¥{{ order.price }}</p>
            </div>

            <!-- Divider -->
            <div class="divider"></div>

            <!-- Respondent Section -->
            <div class="respondent-section">
                <app-title bold="true">{{ $t('profile>order>orderDetail.orderInfoBasic.totalCandidates') }}</app-title>
                <p>{{ order.candidateCount }}</p>
            </div>
        </div>
    </div>

    <!-- Countdown Timer -->
    <div v-if="order.countdownStartAt">
        <div v-if="countdown > 0&&order.status==0" class="app-container" style="background-color: #feb327">
            <div>
                <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.selectedBeforeCountdown') }}</app-title>
                <p>{{ formatTime(countdown) }}</p>
            </div>
            <app-button type="small" color="red" shaped size="small" @click="cancelOrder">
                {{ $t('profile>order>orderDetail.cancelOrder') }}
            </app-button>
        </div>
        <div v-if="!countdown > 0&&order.status==0" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('profile>order>orderDetail.orderExpiredExplanation') }}</p>
            <p>{{ $t('profile>order>orderDetail.refunded') }}</p>
        </div>
        <div v-if="order.status==3" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('profile>order>orderDetail.orderCanceledManuallyExplanation') }}</p>
            <p>{{ $t('profile>order>orderDetail.refunded') }}</p>
        </div>
    </div>
    <div v-else>
        <div v-if="order.status==3" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('profile>order>orderDetail.orderCanceledExplanation') }}</p>
            <p>{{ $t('profile>order>orderDetail.refunded') }}</p>
        </div>
    </div>


    <!-- Respondent Users Title -->
    <div v-if="countdown > 0&&order.status==0" class="mt-4">
        <app-title bold="true">{{ $t('profile>order>orderDetail.candidates') }}</app-title>
        <div v-if="servantList.length > 0">
            <z-swiper v-model="servantList" :options="{slidesPerView: 'auto', centeredSlides: true, spaceBetween: 14}" style="width: 100%">
                <z-swiper-item v-for="(user, index) in servantList" :key="index" :custom-style="{width: '500rpx'}">
                    <demo-item :item="user">
                        <app-container color="#fff" col="12" @click="userDetailRedirect(user.id)">
                            <div class="center_h">
                                <image style="width: 160px; height: 160px; border-radius: 50%; margin: 30px 0" :src="user.avatar" mode="aspectFill"></image>
                            </div>
                            <app-title type="h3" bold="true">{{ user.nickname }}</app-title>
                            <div class="flex" style="margin: 3px 0 30px -6px">
                                <span style="font-size: 27px; margin: 0 10px; position: relative; top: -8px; left: 2px;">
                                    {{ user.gender === 0 ? '👨‍💻' : '👩‍💻' }}
                                </span>
                                <app-title type="h3" bold="true">{{ user.age }}</app-title>
                            </div>
                            <p style="margin-bottom: 10px">{{ user.servantData.bio }}</p>
                        </app-container>
                        <div style="width: 70%;" class="center_h">
                            <app-button type="small" @click="selectServant(user.nickname)" shaped>
                                {{ $t('profile>order>orderDetail.selectCandidate') }}
                            </app-button>
                        </div>
                    </demo-item>
                </z-swiper-item>
            </z-swiper>
        </div>
        <div v-else>
            <div class="no-more-data-text" style="margin-bottom: 60vh;">
                {{ $t('profile>order>orderDetail.noCandidate') }}
            </div>
        </div>
    </div>

    <!-- Order Detail -->
    <div class="mt-4" style="color: grey">
        <div class="order-detail">
            <span>{{ $t('profile>order>orderDetail.orderInfoDetail.orderId') }}:</span> {{ order.identifier }}
        </div>
        <div class="order-detail">
            <span>{{ $t('profile>order>orderDetail.orderInfoDetail.orderTime') }}:</span> {{ common.stampToTime(order.createdAt) }}
        </div>
        <div class="order-detail">
            <span>{{ $t('profile>order>orderDetail.orderInfoDetail.paymentMethod') }}:</span>
            <div v-if="order.paymentMethod==0">
                {{ balanceText }}
            </div>
            <div v-else-if="order.paymentMethod==1">
                {{ weChatText }}
            </div>
        </div>
        <div class="order-detail">
            <span>{{ $t('profile>order>orderDetail.orderInfoDetail.address') }}:</span>
            <div style="flex-direction: column;text-align: end;">
                <div>{{ orderAddress.address }}</div>
                <div>{{ orderAddress.addressName }}</div>
                <div>{{ orderAddress.detail }}</div>
            </div>
        </div>

        <!-- Cancel order (no candidates) -->
        <div v-if="order.status==0&&common.isEmpty(this.servantList)" class="fix-bottom">
            <app-button color="red" shaped @click="cancelOrder">
                {{ $t('profile>order>orderDetail.cancelOrder') }}
            </app-button>
        </div>

        <!-- Repost order -->
        <div v-if="order.status==3" class="fix-bottom">
            <app-button shaped @click="repostOrder">
                {{ $t('profile>order>orderDetail.repostOrder') }}
            </app-button>
        </div>
    </div>
</div>
</template>

<script>

import common from "../../../../utils/common";
import $common from "../../../../utils/common";

export default {
    computed: {
        common() {
            return common
        }
    },
    data() {
        return {
            orderId: '',
            order: {},
            orderAddress: {},
            servantList: [],
            countdown: 0, // Countdown in seconds
            countdownInterval: null,
            freeOrderPostingQuota: 0,
            balanceText: this.$t('profile>order>orderDetail.orderInfoDetail.balance'),
            weChatText: this.$t('profile>order>orderDetail.orderInfoDetail.weChat'),
        };
    },
    onLoad(params) {
        this.orderId = params.orderId;
        this.getOrder();
    },
    onUnLoad() {
        if (this.countdownInterval) {
            clearInterval(this.countdownInterval);
        }
    },
    methods: {
        getOrder() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.order.search,
                method: 'POST',
                data: {
                    id: this.orderId
                },
                success: (res) => {
                    this.order = res.data.list[0];
                    if (this.order.countdownStartAt) {
                        this.startCountdown();
                    }
                    this.getRemainingFreeOrderPostingQuota();
                    this.getOrderAddress();
                    if (this.order.status == 0) {
                        this.getServantList();
                    }
                },
            });
        },


        getRemainingFreeOrderPostingQuota() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.order.remainingFreePostingQuota,
                method: 'POST',
                data: {
                    userId: this.order.clientId
                },
                success: (res) => {
                    this.freeOrderPostingQuota = res.data.freeOrderPostingQuota;
                },
            });
        },
        getOrderAddress() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.address.search,
                method: 'POST',
                data: {
                    id: this.order.addressId,
                },
                success: (res) => {
                    this.orderAddress = res.data.list[0];
                },
            });
        },

        startCountdown() {
            const countdownDurationInMinutes = 10; // Define your countdown duration in minutes
            const countdownDurationInMilliseconds = countdownDurationInMinutes * 60 * 1000; // Convert to milliseconds

            // Parse the countdownStartAt time to get the start time in milliseconds
            const startTime = new Date(this.order.countdownStartAt).getTime();
            const countdownEndTime = startTime + countdownDurationInMilliseconds; // Calculate the end time

            // Get the current time in milliseconds
            const currentTime = new Date().getTime();

            // Calculate the remaining time until countdown ends
            const remainingTime = countdownEndTime - currentTime;

            console.log("Remaining Time (milliseconds):", remainingTime);

            // Check if the countdown is still active
            if (remainingTime > 0) {
                this.countdown = Math.floor(remainingTime / 1000); // Convert to seconds
                this.countdownInterval = setInterval(() => {
                    if (this.countdown > 0) {
                        this.countdown--;
                    } else {
                        clearInterval(this.countdownInterval);
                        console.log("Countdown has ended.");
                    }
                }, 1000);
            } else {
                this.countdown = 0;
                console.log("Countdown has already ended.");
            }
        },

        formatTime(seconds) {
            const hours = Math.floor(seconds / 3600);
            const minutes = Math.floor((seconds % 3600) / 60);
            const secs = seconds % 60;
            return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
        },

        getServantList() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.servants,
                method: 'POST',
                data: {
                    orderId: this.orderId
                },
                success: (res) => {
                    this.servantList = res.data.list;
                    if (!this.$common.isEmpty(this.servantList)) {
                        // Fetch servantData for all users in parallel
                        const promises = this.servantList.map((user) => {
                            return new Promise((resolve) => {
                                uni.request({
                                    url: getApp().globalData.data.requestUrl + this.$API.userServant.search,
                                    method: 'POST',
                                    data: {
                                        userId: user.id
                                    },
                                    success: (res) => {
                                        user.servantData = res.data.list[0];
                                        resolve();
                                    }
                                });
                            });
                        });
                        // Wait for all servantData to be fetched
                        Promise.all(promises).then(() => {
                            this.$forceUpdate(); // Trigger Vue to re-render with updated servantData
                        });
                    }

                },
            });
        },
        selectServant(servantName) {
            uni.showModal({
                title: '选择达人',
                content: `确定选择${servantName}?`,
                showCancel: true,
                confirmText: '确定',
                success: (res) => {
                    // Handle confirmation
                },
            });
        },

        cancelOrder() {
            if (this.$common.isEmpty(this.servantList)) {
                uni.showModal({
                    title: this.$t('profile>order>orderDetail.noCandidateCancelModal.title'),
                    content: this.$t('profile>order>orderDetail.noCandidateCancelModal.content'),
                    showCancel: true,
                    confirmText: this.$t('pub.button.confirm'),
                    cancelText: this.$t('pub.button.cancel'),
                    success: (res) => {
                        if (res.confirm) {
                            uni.request({
                                url: getApp().globalData.data.requestUrl + this.$API.order.updateStatus,
                                method: 'POST',
                                data: {
                                    orderId: this.order.id,
                                    status: 3
                                },
                                success: (res) => {
                                    uni.redirectTo({
                                        url: '/pages/profile/order/order',
                                    })
                                },
                            });
                        }

                    },
                });
            } else {
                uni.showModal({
                    title: this.$t('profile>order>orderDetail.hasCandidateCancelModal.title'),
                    content: this.$t('profile>order>orderDetail.hasCandidateCancelModal.content') + `${this.freeOrderPostingQuota}`,
                    showCancel: true,
                    confirmText: this.$t('pub.button.confirm'),
                    cancelText: this.$t('pub.button.cancel'),
                    success: (res) => {
                        if (res.confirm) {
                            uni.request({
                                url: getApp().globalData.data.requestUrl + this.$API.order.updateStatus,
                                method: 'POST',
                                data: {
                                    orderId: this.order.id,
                                    status: 3
                                },
                                success: (res) => {
                                    uni.redirectTo({
                                        url: '/pages/profile/order/order',
                                    })
                                },
                            });
                        }

                    },
                });
            }

        },

        repostOrder() {
            uni.showModal({
                title: this.$t('profile>order>orderDetail.repostModal.title'),
                content: this.$t('profile>order>orderDetail.repostModal.content'),
                showCancel: true,
                confirmText: this.$t('pub.button.confirm'),
                cancelText: this.$t('pub.button.cancel'),
                success: async (res) => {
                    if (res.confirm) {
                        const serviceType = () => {
                            return new Promise(
                                (resolve, reject) => {
                                    uni.request({
                                        url: getApp().globalData.data.requestUrl + this.$API.serviceType.search,
                                        method: 'POST',
                                        data: {
                                            id: this.order.requiredServantType
                                        },
                                        success: (res) => {
                                            resolve(res.data.list[0])
                                        },
                                    });
                                }
                            )
                        }
                        const serviceTypeData = await serviceType()
                        uni.redirectTo({
                            url: '/pages/profile/order/order-initiate/order-initiate?serviceType=' + serviceTypeData.id + '&serviceName=' + serviceTypeData.name + '&orderId=' + this.orderId,
                        });
                    }
                },
            });
        },

        async userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId + '?serviceType=' + serviceType + '&serviceName=' + serviceName,
            });
        }
    }
};
</script>

<style scoped>
.price-respondent-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: center;
}

.price-section, .respondent-section {
    text-align: left;
    width: 45%;
}

.divider {
    height: 40px;
    width: 1px;
    background-color: #ddd;
    margin: 0 10px;
}

.user-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #ddd;
}

.user-item.no-border {
    border-bottom: none;
}

.countdown-container {
    margin: 20px 0;
    text-align: center;
    background-color: #fffae5;
    padding: 10px;
    border-radius: 10px;
}

.order-detail {
    display: flex;
    justify-content: space-between;
}

.order-detail span {
    font-weight: bold
}
</style>
