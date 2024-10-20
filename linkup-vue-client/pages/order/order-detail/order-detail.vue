<template>
<div class="page" style="background-color: #f3f2f6;padding-top: 0 !important;">

    <div class="detail-toggle">
        <div @click="detailTypeToggle(0)" style="position: relative">
            <div style="z-index: 10">{{ $t('order>orderDetail.candidateSelection') }}</div>
            <div v-show="detailType === 0" class="active"></div>
        </div>
        <div @click="detailTypeToggle(1)" style="margin-left: 10px; position: relative">
            <div style="z-index: 10">{{ $t('order>orderDetail.orderDetail') }}</div>
            <div v-show="detailType === 1" class="active"></div>
        </div>
    </div>

    <app-title v-if="order.status!=orderConstant.COMPLETED" type="h2" bold="true">
        {{ language != "zh-Hans" ? order.serviceType.name + " Service" : order.serviceType.nameCn + "服务" }}
    </app-title>

    <div v-if="candidateSelectionCountdown != 0 && order.status == orderConstant.PENDING">
        <div class="center-h">
            <img style="width: 140px" src="/static/page/order/order-detail/hourglass-half.svg">
        </div>
        <div class="countdown-container mb-2">
            <p style="font-weight: bold">{{ $t('order>orderDetail.selectionCountdown') }}</p>
            <p>{{ candidateSelectionCountdown }}</p>
        </div>
        <div class="tips-text">
            <div>{{ $t('order>orderDetail.selectedBeforeCountdown') }}</div>
        </div>

        <div class="center-h" style="width: 40vw" >
            <app-button type="small" color="red" shaped size="small"  @click="cancelOrder">{{ $t('order>orderDetail.cancelOrder') }}</app-button>
        </div>
    </div>

    <div v-show="detailType === 0">
        <!-- Cancel before Waiting Respond OT -->
        <div v-if="cancelStatus.isCancelManually" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('order>orderDetail.orderCanceledManuallyExplanation') }}</p>
            <p>{{ $t('order>orderDetail.refunded') }}</p>
        </div>
        <!-- Cancel cuz Waiting Respond OT -->
        <div v-if="cancelStatus.isCancelByTimeout" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('order>orderDetail.orderCanceledByTimeoutExplanation') }}</p>
            <p>{{ $t('order>orderDetail.refunded') }}</p>
        </div>
        <!-- Cancel before Selection OT -->
        <div v-if="cancelStatus.isCancelManuallyWithinSelection" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('order>orderDetail.orderCanceledManuallyWithinSelection') }}</p>
            <p>{{ $t('order>orderDetail.refunded') }}</p>
        </div>
        <!-- Cancel cuz Selection OT -->
        <div v-if="cancelStatus.isCancelByTimeoutWithinSelection" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">{{ $t('order>orderDetail.orderClosed') }}</app-title>
            <p>{{ $t('order>orderDetail.orderCanceledByTimeoutWithinSelection') }}</p>
            <p>{{ $t('order>orderDetail.refunded') }}</p>
        </div>

        <!-- SERVANT CONTAINER  -->
        <div v-if="candidateSelectionCountdown != 0 && order.status == orderConstant.PENDING" class="mt-4">
            <app-title type="h3" bold="true">{{ $t('order>orderDetail.candidates') }}</app-title>
            <div v-if="candidateList.length > 0">
                <z-swiper v-model="candidateList" :options="{slidesPerView: 'auto', centeredSlides: true, spaceBetween: 14}" style="width: 100%">
                    <z-swiper-item v-for="(user, index) in candidateList" :key="index" :custom-style="{width: '500rpx'}">
                        <demo-item :item="user">
                            <app-container color="#fff" col="12"  @click="userDetailRedirect(user.id)">
                                <div v-if="index === 0" class="badge">
                                    <img style="width: 80px;height: 68px;" src="/static/page/order/order-detail/badge.png">
                                    <p class="badge-text">{{$t('order>orderDetail.servantDetail.bestValueOffer')}}</p>
                                </div>
                                <div v-else-if="index === 1" class="badge">
                                    <img style="width: 80px;height: 68px;" src="/static/page/order/order-detail/badge.png">
                                    <p class="badge-text">{{$t('order>orderDetail.servantDetail.secondBestDeal')}}</p>
                                </div>
                                <div v-else-if="index === 2" class="badge">
                                    <img style="width: 80px;height: 68px;" src="/static/page/order/order-detail/badge.png">
                                    <p class="badge-text">{{$t('order>orderDetail.servantDetail.topBudgetChoice')}}</p>
                                </div>
                                <div class="center-h">
                                    <image style="width: 160px; height: 160px; border-radius: 50%; margin: 46px 0 12px 0" :src="user.avatar" mode="aspectFill"></image>
                                </div>
                                <app-title style="text-align: center" type="h3" bold="true">{{ user.nickname }}</app-title>
                                <div class="flex justify-SB mt-2 mb-1">
                                    <div class="center-h" style="width: 50%;display: flex;flex-direction: row;align-items: center;">
                                        <div class="flex" style="margin: 3px 0px 0px -6px">
                                            <div style="text-align: center">
                                                <span style="font-size: 42px; margin: 0 10px;">{{ user.gender === 0 ? '👨‍💻' : '👩‍💻' }}</span>
                                                <app-title type="h3" bold="true">{{ user.age }}</app-title>
                                            </div>
                                        </div>
                                    </div>
                                    <div style="width: 50%">
                                        <div class="servant-detail-item">
                                            <p class="value">¥{{ user.quotedPrice }}</p>
                                            <p class="label">{{ $t('order>orderDetail.servantDetail.quotedPrice') }}</p>
                                            <div class="divider-servant"></div>
                                        </div>
                                        <div v-if="!$common.isEmpty(user.servantData.goodPerformanceRate)&&user.servantData.goodPerformanceRate!=0" class="servant-detail-item">
                                            <p class="value">{{ user.servantData.goodPerformanceRate }}%</p>
                                            <p class="label">{{ $t('order>orderDetail.servantDetail.positiveFeedback') }}</p>
                                            <div class="divider-servant"></div>
                                        </div>
                                        <div class="servant-detail-item">
                                            <p class="value">{{ user.completedOrderCount }}</p>
                                            <p class="label">{{ $t('order>orderDetail.servantDetail.assistanceProvided') }}</p>
                                        </div>
                                    </div>
                                </div>
                                <!--<p style="margin-bottom: 10px">{{ user.servantData.bio }}</p>-->
                            </app-container>
                            <div style="width: 70%;" class="center-h">
                                <app-button type="small" @click="selectServant(user.nickname,user.id,user.quotedPrice)" shaped>
                                    {{ $t('order>orderDetail.selectCandidate') }}
                                </app-button>
                            </div>
                        </demo-item>
                    </z-swiper-item>
                </z-swiper>
            </div>
            <div v-else>
                <div class="no-more-data-text" style="margin-bottom: 60vh;">
                    {{ $t('order>orderDetail.noCandidate') }}
                </div>
            </div>
        </div>

        <!-- SERVICE IN PROGRESS -->
        <div v-if="order.status == orderConstant.PROCESSING&&isServiceInProgress" class="app-container" style="background-color: #44e1a6">
            <app-title type="h3" bold="true" style="color: white">{{ $t('order>orderDetail.serviceInProgress') }}</app-title>
            <app-button type="small" color="black" shaped size="small" @click="completeOrder">
                {{ $t('order>orderDetail.completeOrder') }}
            </app-button>
        </div>

        <div v-if="order.status == orderConstant.PROCESSING" style="width: 80vw;margin: 0 auto">
            <app-container color="#fff" col="12" @click="userDetailRedirect(orderServant.id)">
                <div class="center-h">
                    <image style="width: 160px; height: 160px; border-radius: 50%; margin: 25px 0 12px 0" :src="orderServant.avatar" mode="aspectFill"></image>
                </div>
                <app-title style="text-align: center" type="h3" bold="true">{{ orderServant.nickname }}</app-title>
                <div class="flex justify-SB mt-2 mb-1">
                    <div class="center-h" style="width: 50%;display: flex;flex-direction: row;align-items: center;">
                        <div class="flex" style="margin: 3px 0px 0px -6px">
                            <div style="text-align: center">
                                <span style="font-size: 42px; margin: 0 10px;">{{ orderServant.gender === 0 ? '👨‍💻' : '👩‍💻' }}</span>
                                <app-title type="h3" bold="true">{{ orderServant.age }}</app-title>
                            </div>
                        </div>
                    </div>
                    <div style="width: 50%">
                        <div class="servant-detail-item">
                            <p class="value">¥{{ order.price }}</p>
                            <p class="label">{{ $t('order>orderDetail.servantDetail.quotedPrice') }}</p>
                            <div class="divider-servant"></div>
                        </div>
                        <div v-if="!$common.isEmpty(orderServant.servantData.goodPerformanceRate)&&orderServant.servantData.goodPerformanceRate!=0" class="servant-detail-item">
                            <p class="value">{{ orderServant.servantData.goodPerformanceRate }}%</p>
                            <p class="label">{{ $t('order>orderDetail.servantDetail.positiveFeedback') }}</p>
                            <div class="divider-servant"></div>
                        </div>
                        <div class="servant-detail-item">
                            <p class="value">{{ orderServant.completedOrderCount }}</p>
                            <p class="label">{{ $t('order>orderDetail.servantDetail.assistanceProvided') }}</p>
                        </div>
                    </div>
                </div>
            </app-container>
            <div style="width: 70%;" class="center-h">
                <app-button type="small" @click="chatWindowRedirect(orderServant.id)" shaped>
                    {{ $t('order>orderDetail.startChatting') }}
                </app-button>
            </div>
        </div>
        <!-- /SERVANT CONTAINER  -->


        <!-- SERVICE COMPLETE -->
        <div v-if="order.status==orderConstant.COMPLETED" class="center-h">
            <div class="center-h">
                <img style="width: 140px" src="/static/page/order/check-double.svg">
            </div>
            <div class="tips-text">
                <div>{{ $t('order>orderDetail.serviceComplete') }}</div>
            </div>

            <!-- Rating Section -->
            <div class="rating-section">
                <app-title type="h2" bold="true">
                    <div>{{ $t('order>orderDetail.rateRequest') }}</div>
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

        <!-- Cancel order opt (no candidates) -->
        <div v-if="order.status == orderConstant.PENDING && $common.isEmpty(this.candidateList)" class="fix-bottom flex">
            <div style="width: 100%">
                <app-button color="red" shaped @click="cancelOrder">
                    {{ $t('order>orderDetail.cancelOrder') }}
                </app-button>
            </div>
            <img class="reload-btn center-v" @click="reload()" src="/static/page/order/order-detail/rotate-right-solid.svg">
        </div>
        <!-- Repost order opt -->
        <div v-if="order.status == orderConstant.CANCELED" class="fix-bottom">
            <app-button shaped @click="repostOrder">
                {{ $t('order>orderDetail.repostOrder') }}
            </app-button>
        </div>
    </div>

    <div v-show="detailType === 1">
        <!-- ORDER BASIC INFO CONTAINER-->
        <div class="app-container" style="background-color: white !important;">
            <div class="price-respondent-container">
                <!-- Price Section -->
                <div class="price-section">
                    <app-title bold="true">{{ $t('order>orderDetail.orderInfoBasic.price') }}</app-title>
                    <p>¥{{ order.price }}</p>
                </div>

                <!-- Divider -->
                <div class="divider"></div>

                <!-- Respondent Section -->
                <div class="respondent-section">
                    <app-title bold="true">{{ $t('order>orderDetail.orderInfoBasic.totalCandidates') }}</app-title>
                    <p>{{ order.candidateCount }}</p>
                </div>
            </div>
        </div>
        <!-- /ORDER BASIC INFO CONTAINER-->

        <!-- ORDER DETAIL -->
        <div class="order-detail-section">
            <div class="order-detail-item">
                <div class="icon"><img src="/static/page/order/order-detail/timer.svg" alt="Payment Icon"/></div>
                <div class="details">
                    <span class="label">{{ $t('order>orderDetail.orderInfoDetail.serviceTime') }}</span>
                    <p class="value">
                        {{ $common.stampToTime(order.serviceScheduleStart, {yyyy: false, ss: false}) }}
                        -
                        {{ $common.stampToTime(order.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false}) }}
                    </p>
                </div>
            </div>
            <div class="order-detail-item">
                <div class="icon"><img src="/static/page/order/order-detail/location-dot.svg" alt="Payment Icon"/></div>
                <div class="details">
                    <span class="label">{{ $t('order>orderDetail.orderInfoDetail.address') }}</span>
                    <p class="value">{{ order.address.address }} </p>
                    <p class="value">{{ order.address.addressName }} </p>
                    <p class="value">{{ order.address.detail }}</p>
                </div>
            </div>
            <div class="order-detail-item">
                <div class="icon" style="width: 20px;margin-left: 22px;"><img src="/static/page/order/order-detail/memo.svg" alt="Payment Icon"/></div>
                <div class="details">
                    <span class="label">{{ $t('order>orderDetail.orderInfoDetail.orderId') }}</span>
                    <div @click="$common.addToClipboard(order.identifier)" class="value">{{ order.identifier }}</div>
                </div>
            </div>
            <div class="order-detail-item">
                <div class="icon"><img src="/static/page/order/order-detail/credit-card-front.svg" alt="Payment Icon"/></div>
                <div class="details">
                    <span class="label">{{ $t('order>orderDetail.orderInfoDetail.paymentMethod') }}</span>
                    <div v-if="order.paymentMethod == 0" class="value">{{ balanceText }}</div>
                    <div v-else-if="order.paymentMethod == 1" class="value">{{ weChatText }}</div>
                </div>
            </div>
        </div>
        <!-- /ORDER DETAIL -->

    </div>
</div>
</template>

<script>

import $common from "../../../utils/common";

export default {
    name: "order-detail",
    computed: {
        $common() {
            return $common
        }
    },
    data() {
        return {
            detailType: 0,
            orderId: '',
            order: {},
            orderAddress: {},

            candidateList: [],
            orderServant: {},

            freeOrderPostingQuota: 0,

            balanceText: this.$t('order>orderDetail.orderInfoDetail.balance'),
            weChatText: this.$t('order>orderDetail.orderInfoDetail.weChat'),

            candidateSelectionCountdown: 0,

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
        this.reload();
    },
    methods: {
        reload() {
            this.getOrder()
        },
        getOrder() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.order.search,
                method: 'POST',
                data: {
                    id: this.orderId
                },
                success: async (res) => {
                    this.order = res.data.list[0];
                    if(this.order.status == this.orderConstant.PENDING) {
                        if(this.order.countdownStartAt) {
                            const startTime = this.order.countdownStartAt;
                            const durationInMinutes = 10;
                            const endTime = new Date(startTime).getTime() + durationInMinutes * 60 * 1000;
                            this.$common.calculateCountdown(startTime, endTime, (remainingTime) => {
                                this.candidateSelectionCountdown = remainingTime;
                            });
                        }
                        this.getRemainingFreeOrderPostingQuota();
                        this.getcandidateList();
                    }
                    if(this.order.status == this.orderConstant.PROCESSING) {
                        await this.getOrderServant();
                        await this.getOrderServantInfo();
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

        getcandidateList() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.servants,
                method: 'POST',
                data: {
                    orderId: this.orderId
                },
                success: (res) => {
                    this.candidateList = res.data.list.sort((a, b) => a.quotedPrice - b.quotedPrice);

                    if(!this.$common.isEmpty(this.candidateList)) {
                        // Use Promise.all for concurrent requests
                        const promises = this.candidateList.map((user) => {
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

                        // Wait for all servantData to be fetched
                        Promise.all(promises).then(() => {
                            this.$forceUpdate(); // Trigger Vue to re-render with updated servantData
                        });
                    }
                },
                fail: (error) => {
                }
            });
        },
        selectServant(servantName, servantId, quotedPrice) {
            uni.showModal({
                title: this.$t('order>orderDetail.selectServantModal.title'),
                content: this.$t('order>orderDetail.selectServantModal.content1') + servantName + this.$t('order>orderDetail.selectServantModal.content2'),
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
            if(this.$common.isEmpty(this.candidateList)) {
                uni.showModal({
                    title: this.$t('order>orderDetail.noCandidateCancelModal.title'),
                    content: this.$t('order>orderDetail.noCandidateCancelModal.content'),
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
                                    uni.switchTab({
                                        url: '/pages/order/order',
                                    })
                                },
                            });
                        }
                    },
                });
            } else {
                uni.showModal({
                    title: this.$t('order>orderDetail.hasCandidateCancelModal.title'),
                    content: this.$t('order>orderDetail.hasCandidateCancelModal.content') + `${this.freeOrderPostingQuota}`,
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
                                    uni.switchTab({
                                        url: '/pages/order/order',
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
                title: this.$t('order>orderDetail.completeOrderModal.title'),
                content: this.$t('order>orderDetail.completeOrderModal.content'),
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
                title: this.$t('order>orderDetail.repostModal.title'),
                content: this.$t('order>orderDetail.repostModal.content'),
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
                            url: '../order-initiate/order-initiate?serviceType=' + serviceTypeData.id + '&serviceName=' + serviceTypeData.name + '&serviceNameCn=' + serviceTypeData.nameCn + '&orderId=' + this.orderId,
                        });
                    }
                },
            });
        },

        async getOrderServant() {
            const getServant = () => {
                return new Promise((resolve, reject) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.user.search,
                        method: 'POST',
                        data: {
                            id: this.order.servantId
                        },
                        success: (res) => {
                            this.orderServant = res.data.list[0]
                            resolve()
                        },
                    });
                })
            }
            return await getServant();
        },

        async getOrderServantInfo() {
            const getServantInfo = () => {
                return new Promise((resolve, reject) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.userServant.search,
                        method: 'POST',
                        data: {
                            userId: this.order.servantId
                        },
                        success: (res) => {
                            this.$set(this.orderServant, 'servantData', res.data.list[0] || {});
                            resolve()
                        },
                    });
                })
            }
            return await getServantInfo();
        },

        selectEmoji(rating) {
            this.selectedEmoji = rating;
        },
        submitFeedback() {
            if(this.$common.isEmpty(this.selectedEmoji)) {
                uni.showToast({title: this.$t('order>orderDetail.showToast.selectRate'), icon: 'none'});
                return;
            }
            if(!this.$common.isEmpty(this.order.performanceRating)) {
                uni.showToast({title: this.$t('order>orderDetail.showToast.rated'), icon: 'none'});
                return;
            }
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.order.rateServant,
                method: 'POST',
                data: {
                    orderId: this.orderId,
                    rating: this.selectedEmoji
                },
                success: (res) => {
                    console.log(res)
                    if(res.data.status == 200) {
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                    } else {
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    }
                    this.reload()
                },
                fail: (error) => {
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
            this.reload();
        },

        detailTypeToggle(type) {
            this.detailType = type;
        },


        // Redirect
        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId,
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
.detail-toggle {
    padding: 16px 0;
    display: flex;
    font-weight: bold;
    font-size: 20px;
}

.active {
    background-color: rgb(38 118 247 / 0.4);
    height: 6px;
    border-radius: 5px;
    width: 80%;
    margin-top: -10px;
}

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
    background-color: #f0f0f0;
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

.tips-text {
    width: 100%;
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

.order-detail-section {
    padding: 15px;
    border-radius: 15px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    color: #333;
    background-color: white;
    margin-bottom: 10vh;
}

.order-detail-item {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 16px;
    color: #4a4a4a;
}

.order-detail-item .icon {
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 18px;
}

.order-detail-item .details {
    display: flex;
    flex-direction: column;
}

.order-detail-item .label {
    font-weight: bold;
    color: #000;
    font-size: 16px;
    margin-bottom: 4px;
}

.order-detail-item .value {
    color: #4a4a4a;
    font-size: 14px;
}

.divider-servant {
    width: 90%;
    height: 2px;
    background-color: #f0f0f0;
    margin: 6px 0;;
}

.servant-detail-item {
    line-height: 24px
}

.servant-detail-item .value {
    font-size: 20px;
    font-weight: bold;
}

.servant-detail-item .label {
    font-size: 12px;
}

.countdown-container {
    text-align: center;
    background-color: #2676f7;
    padding: 2px 56px;
    border-radius: 50px;
    color: white;
    font-size: 16px;
    width: 164px;
    margin: 0 auto;
}

.badge {
    position: absolute;
    display: inline-block;
    font-size: 14px;
    font-weight: bold;
    padding: 7px 12px;
    color: white;
    border-radius: 4px 4px 0 0;
    text-align: center;
    top: 0;
    right: 0px;
}

.badge-text{
    position: absolute;
    top: 18px;
    width: 80px;
}

</style>