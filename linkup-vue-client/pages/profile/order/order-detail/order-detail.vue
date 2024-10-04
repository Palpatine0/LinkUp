<template>
<div class="page" style="background-color: #f3f2f6">
    <!-- Title -->
    <app-title type="h2" bold="true">
        {{ language != "zh-Hans" ? order.title : order.titleCn }}
    </app-title>


    <!-- ORDER BASIC INFO CONTAINER-->
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
    <!-- /ORDER BASIC INFO CONTAINER-->


    <!-- DYNAMIC STATUS CONTAINER -->
    <!-- Has candidates -->
    <div v-if="order.countdownStartAt">
        <!-- Alert: Choose while still can -->
        <div v-if="countdown > 0&&order.status==0" class="app-container" style="background-color: #feb327">
            <div>
                <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.selectedBeforeCountdown') }}</app-title>
                <p>{{ formatTime(countdown) }}</p>
            </div>
            <app-button type="small" color="red" shaped size="small" @click="cancelOrder">
                {{ $t('profile>order>orderDetail.cancelOrder') }}
            </app-button>
        </div>
        <!-- Alert: Overtime -->
        <div v-if="!countdown > 0&&order.status==0" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('profile>order>orderDetail.orderExpiredExplanation') }}</p>
            <p>{{ $t('profile>order>orderDetail.refunded') }}</p>
        </div>
        <!-- Alert: Cancel with countdown -->
        <div v-if="order.status==3" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('profile>order>orderDetail.orderCanceledManuallyExplanation') }}</p>
            <p>{{ $t('profile>order>orderDetail.refunded') }}</p>
        </div>
    </div>
    <!-- No candidates -->
    <div v-else>
        <div v-if="order.status==3" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('profile>order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('profile>order>orderDetail.orderCanceledExplanation') }}</p>
            <p>{{ $t('profile>order>orderDetail.refunded') }}</p>
        </div>
    </div>
    <!-- /DYNAMIC STATUS CONTAINER -->


    <!-- SERVANT CONTAINER  -->
    <div v-if="countdown > 0&&order.status==0" class="mt-4">
        <app-title bold="true">{{ $t('profile>order>orderDetail.candidates') }}</app-title>
        <div v-if="servantList.length > 0">
            <z-swiper v-model="servantList" :options="{slidesPerView: 'auto', centeredSlides: true, spaceBetween: 14}" style="width: 100%">
                <z-swiper-item v-for="(user, index) in servantList" :key="index" :custom-style="{width: '500rpx'}">
                    <demo-item :item="user">
                        <app-container color="#fff" col="12" @click="userDetailRedirect(user.id)">
                            <div class="center-h">
                                <image style="width: 160px; height: 160px; border-radius: 50%; margin: 30px 0" :src="user.avatar" mode="aspectFill"></image>
                            </div>
                            <app-title type="h3" bold="true">{{ user.nickname }}</app-title>
                            <div class="flex" style="margin: 3px 0 30px -6px">
                                <span style="font-size: 27px; margin: 0 10px; position: relative; top: -8px; left: 2px;">
                                    {{ user.gender === 0 ? '👨‍💻' : '👩‍💻' }}
                                </span>
                                <app-title type="h3" bold="true">{{ user.age }}</app-title>
                            </div>
                            <div class="highlight-blue">
                                {{ $t('profile>order>orderDetail.quotedPrice') }}: {{ user.quotedPrice }}
                            </div>
                            <p style="margin-bottom: 10px">{{ user.servantData.bio }}</p>
                        </app-container>
                        <div style="width: 70%;" class="center-h">
                            <app-button type="small" @click="selectServant(user.nickname,user.id)" shaped>
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

    <div v-if="order.status==1" style="width: 65vw;margin: 0 auto">
        <app-container color="#fff" col="12" @click="userDetailRedirect(orderServant.id)">
            <div class="center-h">
                <image style="width: 160px; height: 160px; border-radius: 50%; margin: 30px 0" :src="orderServant.avatar" mode="aspectFill"></image>
            </div>
            <app-title type="h3" bold="true">{{ orderServant.nickname }}</app-title>
            <div class="flex" style="margin: 3px 0 30px -6px">
            <span style="font-size: 27px; margin: 0 10px; position: relative; top: -8px; left: 2px;">
                {{ orderServant.gender === 0 ? '👨‍💻' : '👩‍💻' }}
            </span>
                <app-title type="h3" bold="true">{{ orderServant.age }}</app-title>
            </div>
            <p style="margin-bottom: 10px">{{ orderServant.servantData.bio }}</p>
        </app-container>
        <div style="width: 70%;" class="center-h">
            <app-button type="small" @click="chatWindowRedirect(orderServant.id)" shaped>
                {{ $t('profile>order>orderDetail.startChatting') }}
            </app-button>
        </div>
    </div>
    <!-- /SERVANT CONTAINER  -->


    <!-- ORDER DETAIL -->
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
            <div style="flex-direction: column;text-align: end;width: 70vw;">
                <div>{{ orderAddress.address }}</div>
                <div>{{ orderAddress.addressName }}</div>
                <div>{{ orderAddress.detail }}</div>
            </div>
        </div>
    </div>
    <!-- /ORDER DETAIL -->


    <!-- Cancel order opt (no candidates) -->
    <div v-if="order.status==0&&common.isEmpty(this.servantList)" class="fix-bottom flex">
        <div style="width: 100%">
            <app-button color="red" shaped @click="cancelOrder">
                {{ $t('profile>order>orderDetail.cancelOrder') }}
            </app-button>
        </div>
        <img class="reload-btn center-v" @click="reload()" src="/static/page/profile/order/rotate-right-solid.svg">
    </div>
    <!-- Repost order opt -->
    <div v-if="order.status==3" class="fix-bottom">
        <app-button shaped @click="repostOrder">
            {{ $t('profile>order>orderDetail.repostOrder') }}
        </app-button>
    </div>


</div>
</template>

<script>

import common from "../../../../utils/common";
import $common from "../../../../utils/common";
import order from "../order.vue";

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
            orderServant: {},
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
                    if (this.order.status == 1) {
                        this.getOrderServant();
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
                    console.log("this.servantList");
                    console.log(res.data.list);
                    this.servantList = res.data.list;

                    if (!this.$common.isEmpty(this.servantList)) {
                        // Use Promise.all for concurrent requests
                        const promises = this.servantList.map((user) => {
                            console.log("Fetching data for user:", user);
                            return new Promise((resolve) => {
                                uni.request({
                                    url: getApp().globalData.data.requestUrl + this.$API.userServant.search,
                                    method: 'POST',
                                    data: {
                                        userId: user.id
                                    },
                                    success: (res) => {
                                        user.servantData = res.data.list[0] || {}; // Default to empty object if no data
                                        user.servantData.quotedPrice = user.quotedPrice; // Assign quotedPrice directly
                                        resolve();
                                    },
                                    fail: () => {
                                        user.servantData = {}; // Handle failure case by assigning empty object
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
                fail: (error) => {
                    console.error("Failed to fetch servant list:", error);
                }
            });
        },
        selectServant(servantName, servantId) {
            uni.showModal({
                title: this.$t('profile>order>orderDetail.selectServantModal.title'),
                content: this.$t('profile>order>orderDetail.selectServantModal.content1') + servantName + this.$t('profile>order>orderDetail.selectServantModal.content2'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: (res) => {
                    if (res.confirm) {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + this.$API.order.assignServant,
                            method: 'POST',
                            data: {
                                orderId: this.order.id,
                                servantId: servantId
                            },
                            success: (res) => {
                                this.reload();
                            },
                        });
                    }
                },
            });
        },

        cancelOrder() {
            if (this.$common.isEmpty(this.servantList)) {
                uni.showModal({
                    title: this.$t('profile>order>orderDetail.noCandidateCancelModal.title'),
                    content: this.$t('profile>order>orderDetail.noCandidateCancelModal.content'),
                    showCancel: true,
                    confirmText: this.$t('pub.modal.button.confirm'),
                    cancelText: this.$t('pub.modal.button.cancel'),
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
                    confirmText: this.$t('pub.modal.button.confirm'),
                    cancelText: this.$t('pub.modal.button.cancel'),
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
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
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
                            url: '/pages/profile/order/order-initiate/order-initiate?serviceType=' + serviceTypeData.id + '&serviceName=' + serviceTypeData.name + '&serviceNameCn=' + serviceTypeData.nameCn + '&orderId=' + this.orderId,
                        });
                    }
                },
            });
        },


        getOrderServant() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.search,
                method: 'POST',
                data: {
                    id: this.order.servantId
                },
                success: (res) => {
                    this.orderServant = res.data.list[0]
                },
            });
        },

        reload() {
            this.getOrder()
        },

        async userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId + '?serviceType=' + serviceType + '&serviceName=' + serviceName,
            });
        },
        chatWindowRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/chat/chat-window/chat-window?contactId=' + userId
            });
        },
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

.reload-btn {
    width: 48px;
    height: 48px;
    margin-top: 5px;
    margin-left: 25px
}

.highlight-blue {
    color: white;
    background-color: #007aff;
    border-radius: 5px;
    font-weight: bold;
    padding: 2px;
    font-size: 14px;
    margin-bottom: 4px;
    display: flex;
    max-width: fit-content;
    margin-left: auto;
    margin-right: auto;
}
</style>
