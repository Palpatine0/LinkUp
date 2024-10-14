<template>
<div class="page" style="background-color: #f3f2f6">
    <!-- Title -->
    <app-title v-if="order.status!=orderConstant.COMPLETED" type="h2" bold="true">
        {{ language != "zh-Hans" ? order.title : order.titleCn }}
    </app-title>

    <!-- ORDER BASIC INFO CONTAINER-->
    <div v-if="order.status!=orderConstant.COMPLETED" class="app-container" style="background-color: white !important;">
        <div class="price-respondent-container">
            <!-- Price Section -->
            <div class="price-section">
                <app-title bold="true">{{ $t('profile>order>orderDetail.orderInfoBasic.price') }}</app-title>
                <p>¥{{ order.price }}</p>
            </div>
        </div>
    </div>
    <!-- /ORDER BASIC INFO CONTAINER-->


    <!-- SERVICE IN PROGRESS -->
    <div v-if="order.status == orderConstant.PROCESSING&&isServiceInProgress" class="app-container" style="background-color: #44e1a6">
        <app-title type="h3" bold="true" style="color: white">{{ $t('profile>order>orderDetail.serviceInProgress') }}</app-title>
    </div>
    <!-- /DYNAMIC STATUS CONTAINERS -->

    <!-- CLIENT CONTAINER  -->
    <div v-if="order.status == orderConstant.PROCESSING" style="width: 65vw;margin: 0 auto">
        <app-container color="#fff" col="12" @click="userDetailRedirect(orderClient.id)">
            <div class="center-h">
                <image style="width: 160px; height: 160px; border-radius: 50%; margin: 30px 0" :src="orderClient.avatar" mode="aspectFill"></image>
            </div>
            <app-title type="h3" bold="true">{{ orderClient.nickname }}</app-title>
            <div class="flex" style="margin: 3px 0 30px -6px">
                <span style="font-size: 27px; margin: 0 10px; position: relative; top: -8px; left: 2px;">
                    {{ orderClient.gender === 0 ? '👨‍💻' : '👩‍💻' }}
                </span>
                <app-title type="h3" bold="true">{{ orderClient.age }}</app-title>
            </div>
            <p style="margin-bottom: 10px">{{ orderClient.servantData.bio }}</p>
            <div style="width: 70%;" class="center-h">
                <app-button type="small" @click="chatWindowRedirect(orderClient.id)" shaped>
                    {{ $t('profile>order>orderDetail.contactUser') }}
                </app-button>
            </div>
        </app-container>
    </div>
    <!-- /SERVANT CONTAINER  -->


    <!-- SERVICE COMPLETE -->
    <div v-if="order.status==orderConstant.COMPLETED" class="center-h">
        <div class="center-h">
            <img style="width: 140px" src="/static/page/profile/order/check-double.svg">
        </div>
        <div class="completed-text">
            <div>{{ $t('profile>order>orderDetail.serviceComplete') }}</div>
        </div>

        <!-- Rating Section -->
        <div class="rating-section">
            <app-title type="h2" bold="true">
                <div>{{ $t('profile>order>orderDetail.rateRequest') }}</div>
            </app-title>
            <div class="emoji-rating mt-2">
                <div class="emoji" role="img" :class="{ selected: selectedEmoji === 0 }" aria-label="Very dissatisfied" @click="selectEmoji(0)">😣</div>
                <div class="emoji" role="img" :class="{ selected: selectedEmoji === 1 }" aria-label="Neutral" @click="selectEmoji(1)">😐</div>
                <div class="emoji" role="img" :class="{ selected: selectedEmoji === 2 }" aria-label="Very satisfied" @click="selectEmoji(2)">😁</div>
            </div>

            <!-- Submit Button -->
            <div class="submit-section mt-3">
                <app-button type="submit" shaped @click="submitFeedback">
                    {{ $t('pub.button.submit') }}
                </app-button>
            </div>
        </div>
    </div>
    <!-- /SERVICE COMPLETE -->

    <!-- ORDER DETAIL -->
    <div class="mt-4" style="color: grey">
        <div class="order-detail">
            <span>{{ $t('profile>order>orderDetail.orderInfoDetail.orderId') }}:</span> <p @click="common.addToClipboard(order.identifier)">{{ order.identifier }}</p>
        </div>
        <div class="order-detail">
            <span>{{ $t('profile>order>orderDetail.orderInfoDetail.orderTime') }}:</span> {{ common.stampToTime(order.createdAt) }}
        </div>
        <div class="order-detail">
            <span>{{ $t('profile>order>orderDetail.orderInfoDetail.paymentMethod') }}:</span>
            <div v-if="order.paymentMethod == 0">
                {{ balanceText }}
            </div>
            <div v-else-if="order.paymentMethod == 1">
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
    <div v-if="order.status == orderConstant.PENDING && common.isEmpty(this.servantList)" class="fix-bottom flex">
        <div style="width: 100%">
            <app-button color="red" shaped @click="cancelOrder">
                {{ $t('profile>order>orderDetail.cancelOrder') }}
            </app-button>
        </div>
        <img class="reload-btn center-v" @click="reload()" src="/static/page/profile/order/rotate-right-solid.svg">
    </div>
    <!-- Repost order opt -->
    <div v-if="order.status == orderConstant.CANCELED" class="fix-bottom">
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
    name: "order-detail",
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
            orderClient: {},

            freeOrderPostingQuota: 0,

            balanceText: this.$t('profile>order>orderDetail.orderInfoDetail.balance'),
            weChatText: this.$t('profile>order>orderDetail.orderInfoDetail.weChat'),

            countdown: 0,
            countdownInterval: null,

            cancelStatus: {
                isCancelManually: false,
                isCancelByTimeout: false,
                isCancelManuallyWithinSelection: false,
                isCancelByTimeoutWithinSelection: false,
            },

            isServiceInProgress: false,
            selectedEmoji: null,
            orderConstant: {
                PENDING: 0,
                PROCESSING: 1,
                COMPLETED: 2,
                CANCELED: 3,
            }
        };
    },
    onLoad(params) {
        this.orderId = params.orderId;
        this.getOrder();
    },
    onUnLoad() {
        if(this.countdownInterval) {
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
                    this.getOrderAddress();
                    if(this.order.status == this.orderConstant.PENDING) {
                        if(this.order.countdownStartAt) {
                            this.startCountdown();
                        }
                        this.getRemainingFreeOrderPostingQuota();
                        this.getServantList();
                    }
                    if(this.order.status == this.orderConstant.PROCESSING) {
                        this.getOrderClient();
                        this.setServiceInProgressState();
                    }
                    this.setCancellationStates();
                },
            });
        },
        setServiceInProgressState() {
            if(this.order.serviceScheduleStart && this.order.serviceScheduleEnd) {
                const currentTime = new Date().getTime();
                const serviceStartTime = new Date(this.order.serviceScheduleStart).getTime();
                const serviceEndTime = new Date(this.order.serviceScheduleEnd).getTime();
                if(currentTime >= serviceStartTime && currentTime <= serviceEndTime) {
                    this.isServiceInProgress = true;
                } else {
                    this.isServiceInProgress = false;
                }
            } else {
                this.isServiceInProgress = false;
            }
        },
        setCancellationStates() {
            const createdAt = new Date(this.order.createdAt).getTime();
            const canceledAt = this.order.canceledAt ? new Date(this.order.canceledAt).getTime() : null;
            const countdownStartAt = this.order.countdownStartAt ? new Date(this.order.countdownStartAt).getTime() : null;
            const tenMinutes = 10 * 60 * 1000;
            const thirtyMinutes = 30 * 60 * 1000;

            if(this.order.status === this.orderConstant.CANCELED && canceledAt) {
                if(!countdownStartAt && (canceledAt - createdAt) < thirtyMinutes) {
                    this.cancelStatus.isCancelManually = true;
                } else if(!countdownStartAt && (canceledAt - createdAt) >= thirtyMinutes) {
                    this.cancelStatus.isCancelByTimeout = true;
                } else if(countdownStartAt && (canceledAt - countdownStartAt) < tenMinutes) {
                    this.cancelStatus.isCancelManuallyWithinSelection = true;
                } else if(countdownStartAt && (canceledAt - countdownStartAt) >= tenMinutes) {
                    this.cancelStatus.isCancelByTimeoutWithinSelection = true;
                }
            }
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
            if(remainingTime > 0) {
                this.countdown = Math.floor(remainingTime / 1000); // Convert to seconds
                this.countdownInterval = setInterval(() => {
                    if(this.countdown > 0) {
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

                    if(!this.$common.isEmpty(this.servantList)) {
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
        selectServant(servantName, servantId, quotedPrice) {
            uni.showModal({
                title: this.$t('profile>order>orderDetail.selectServantModal.title'),
                content: this.$t('profile>order>orderDetail.selectServantModal.content1') + servantName + this.$t('profile>order>orderDetail.selectServantModal.content2'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: (res) => {
                    if(res.confirm) {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + this.$API.order.assignServant,
                            method: 'POST',
                            data: {
                                orderId: this.order.id,
                                servantId: servantId,
                                quotedPrice: quotedPrice
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
            if(this.$common.isEmpty(this.servantList)) {
                uni.showModal({
                    title: this.$t('profile>order>orderDetail.noCandidateCancelModal.title'),
                    content: this.$t('profile>order>orderDetail.noCandidateCancelModal.content'),
                    showCancel: true,
                    confirmText: this.$t('pub.modal.button.confirm'),
                    cancelText: this.$t('pub.modal.button.cancel'),
                    success: (res) => {
                        if(res.confirm) {
                            uni.request({
                                url: getApp().globalData.data.requestUrl + this.$API.order.updateStatus,
                                method: 'POST',
                                data: {
                                    orderId: this.order.id,
                                    status: this.orderConstant.CANCELED
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
                        if(res.confirm) {
                            uni.request({
                                url: getApp().globalData.data.requestUrl + this.$API.order.updateStatus,
                                method: 'POST',
                                data: {
                                    orderId: this.order.id,
                                    status: this.orderConstant.CANCELED
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
        completeOrder() {
            uni.showModal({
                title: this.$t('profile>order>orderDetail.completeOrderModal.title'),
                content: this.$t('profile>order>orderDetail.completeOrderModal.content'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: (res) => {
                    if(res.confirm) {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + this.$API.order.updateStatus,
                            method: 'POST',
                            data: {
                                orderId: this.order.id,
                                status: this.orderConstant.COMPLETED
                            },
                            success: (res) => {
                                this.reload();
                            },
                        });
                    }

                },
            });
        },
        repostOrder() {
            uni.showModal({
                title: this.$t('profile>order>orderDetail.repostModal.title'),
                content: this.$t('profile>order>orderDetail.repostModal.content'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: async (res) => {
                    if(res.confirm) {
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

        getOrderClient() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.search,
                method: 'POST',
                data: {
                    id: this.order.clientId
                },
                success: (res) => {
                    this.orderClient = res.data.list[0]
                },
            });
        },

        selectEmoji(rating) {
            this.selectedEmoji = rating;
        },
        submitFeedback() {
            if(this.$common.isEmpty(this.selectedEmoji)) {
                uni.showToast({title: this.$t('profile>order>orderDetail.showToast.selectRate'), icon: 'none'});
                return;
            }
            if(!this.$common.isEmpty(this.order.performanceRating)){
                uni.showToast({title: this.$t('profile>order>orderDetail.showToast.rated'), icon: 'none'});
                return;
            }
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.order.rate,
                method: 'POST',
                data: {
                    orderId: this.orderId,
                    rating: this.selectedEmoji
                },
                success: (res) => {
                    uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                },
                fail: (error) => {
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
            this.reload();
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

.highlight {
    color: white;
    background-color: #607D8B;
    border-radius: 5px;
    font-weight: bold;
    padding: 4px 12px;
    font-size: 14px;
    margin-bottom: 4px;
    //display: flex;
    //max-width: fit-content;
    //margin-left: auto;
    //margin-right: auto;
}

.completed-text {
    width: 400px;
    font-size: 36px;
    font-weight: bold;
    line-height: 40px;
    color: #192C77;
}

.rating-section {
    text-align: center;
    margin-top: 20px;
}

.emoji-rating {
    display: flex;
    justify-content: center;
    gap: 20px;
    font-size: 2rem;
}

.emoji-rating div {
    background-color: #FFF;
    border-radius: 25px;
    padding: 0px 34px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.emoji-rating div.selected {
    background-color: #fff0cb;
    color: white;
}

.submit-section {
    text-align: center;
    margin-top: 20px;
}

</style>