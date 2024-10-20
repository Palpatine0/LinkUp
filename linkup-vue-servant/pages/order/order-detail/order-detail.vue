<template>
<div class="page" style="background-color: #f3f2f6">

    <!--HEADER-->
    <div alt="header" class="center-h">
        <div class="back" @click="back()">
            <img style="width: 22px;height: 22px" src="/static/page/register/back.svg">
        </div>
        <div class="nav-bar">
            {{ language != "zh-Hans" ? order.serviceType.name + " Service" : order.serviceType.nameCn + "服务" }}
        </div>
    </div>
    <!--/HEADER-->

    <!--PROCESSING -->
    <div alt="processing" v-if="order.status == orderConstant.PROCESSING" class="center-h">
        <div class="price-service-detail">
            <div class="price-text">{{ $t('profile>order>orderDetail.orderInfoBasic.price') }}</div>
            <div class="price">¥{{ order.price }}</div>

            <div v-if="order.status == 1 && !isServiceInProgress" class="countdown-container">
                <p style="font-weight: bold">{{ $t('profile>order>orderDetail.serviceCountdown') }}</p>
                <p>{{ serviceCountdown }}</p>
            </div>
            <div v-if="order.status == 1 && isServiceInProgress" class="countdown-container">
                <p style="font-weight: bold">{{ $t('profile>order>orderDetail.serviceCompleteCountdown') }}</p>
                <p>{{ serviceCompleteCountdown }}</p>
            </div>
        </div>
        <div class="client-addr-detail">
            <div style="display: flex;justify-content: flex-start;margin: 18px 0 2px 0;width: 90%;">
                <div style="width: 80%; text-align: left;">
                    <p style="font-weight: bold;font-size:18px">{{ order.address.addressName }}</p>
                    <p style="font-weight: bold;font-size:18px">{{ order.address.detail }}</p>
                    <p>
                        {{ $common.stampToTime(this.order.serviceScheduleStart, {yyyy: false, ss: false}) }}
                        -
                        {{ $common.stampToTime(this.order.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false}) }}
                    </p>
                </div>
                <div style="margin-top: 14px;margin-left: 18px;">
                    <img style="width: 36px;height: 36px;" src="/static/page/order/order-detail/siren-on.svg" @click="emergencyCenterRedirect()">
                </div>
            </div>
            <div class="divider"></div>
            <img style="width: 160px; height: 160px; border-radius: 50%; margin: 10px 0" :src="orderClient.avatar" mode="aspectFill" @click="userDetailRedirect(orderClient.id)">
            <app-title type="h2" bold="true">{{ orderClient.nickname }}</app-title>
            <div style="width: 70%;" class="center-h">
                <app-button type="small" @click="chatWindowRedirect(orderClient.id)" shaped>
                    {{ $t('profile>order>orderDetail.contactUser') }}
                </app-button>
            </div>
        </div>
    </div>
    <!--/PROCESSING CONTAINER-->


    <!-- SERVICE IN PROGRESS -->
    <div v-if="order.status == orderConstant.PROCESSING&&isServiceInProgress" class="app-container" style="background-color: #44e1a6">
        <app-title type="h3" bold="true" style="color: white">{{ $t('profile>order>orderDetail.serviceInProgress') }}</app-title>
    </div>
    <!-- /DYNAMIC STATUS CONTAINERS -->

    <!-- SERVICE COMPLETE -->
    <div alt="completed" v-if="order.status==orderConstant.COMPLETED" class="center-h">
        <div class="center-h">
            <img style="width: 140px" src="/static/page/order/order-detail/check-double.svg">
        </div>
        <div class="completed-text">
            <div>{{ $t('profile>order>orderDetail.serviceComplete') }}</div>
        </div>

        <!-- Rating Section -->
        <div  v-if="order.isReviewed==0" class="rating-section">
            <app-title type="h2" bold="true">
                <div>{{ $t('profile>order>orderDetail.reviewRequest') }}</div>
            </app-title>

            <div class="mt-2" style="text-align: left">
                <app-title bold="true">{{ $t('profile>order>orderDetail.behavioralRecord') }}</app-title>
                <app-input mode="textarea" :placeholder="$t('profile>order>orderDetail.behavioralRecordPlaceholder')" color="#FFF" v-model="reviewFormData.comments"/>
            </div>
            <div class="submit-section mt-3">
                <app-button type="submit" shaped @click="submitFeedback">
                    {{ $t('pub.button.submit') }}
                </app-button>
            </div>
        </div>
    </div>
    <!-- /SERVICE COMPLETE -->

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
            orderId: '',
            order: {},

            orderClient: {},

            isServiceInProgress: false,
            serviceCountdown: 0,
            serviceCompleteCountdown: 0,

            cancelStatus: {
                isCancelManually: false,
                isCancelByTimeout: false,
                isCancelManuallyWithinSelection: false,
                isCancelByTimeoutWithinSelection: false,
            },

            selectedEmoji: null,
            orderConstant: {
                PENDING: 0,
                PROCESSING: 1,
                COMPLETED: 2,
                CANCELED: 3,
            },

            reviewFormData: {
                orderId: '',
                reviewerId: '',
                revieweeId: '',
                comments: ''
            }
        };
    },
    onLoad(params) {
        this.orderId = params.orderId;
        this.reload();
    },
    onUnLoad() {
        if(this.countdownInterval) {
            clearInterval(this.countdownInterval);
        }
    },
    methods: {
        back() {
            uni.navigateBack({
                delta: 1
            });
        },
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
                success: (res) => {
                    this.order = res.data.list[0];
                    if(this.order.status == this.orderConstant.PROCESSING) {
                        this.getOrderClient();
                        this.setServiceInProgressState();
                        if(!this.isServiceInProgress) {
                            const endTime = new Date(this.order.serviceScheduleStart);
                            this.$common.calculateCountdown(null, endTime, (remainingTime) => {
                                this.serviceCountdown = remainingTime;
                            });
                        }
                        if(this.isServiceInProgress) {
                            const endTime = new Date(this.order.serviceScheduleEnd);
                            this.$common.calculateCountdown(null, endTime, (remainingTime) => {
                                this.serviceCompleteCountdown = remainingTime;
                            });
                        }
                    }
                    if(this.order.status == this.orderConstant.COMPLETED) {
                        this.reviewFormData.orderId = this.order.id;
                        this.reviewFormData.reviewerId = this.order.servantId;
                        this.reviewFormData.revieweeId = this.order.clientId;
                    }
                }
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
        async submitFeedback() {
            if(this.$common.isEmpty(this.reviewFormData.comments)) {
                uni.showToast({title: this.$t('profile>order>orderDetail.showToast.fillReview'), icon: 'none'});
                return;
            }
            if(this.order.isReviewed == 1) {
                uni.showToast({title: this.$t('profile>order>orderDetail.showToast.reviewed'), icon: 'none'});
                return;
            }
            const reviewClient = new Promise(
                (resolve, reject) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.order.reviewClient,
                        method: 'POST',
                        data: {
                            orderId: this.orderId,
                        },
                        success: (res) => {
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                            resolve()
                        },
                        fail: (error) => {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                            reject()
                        }
                    });
                }
            )
            await reviewClient
            const reviewSave = new Promise(
                (resolve, reject) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.review.save,
                        method: 'POST',
                        data: {
                            ...this.reviewFormData
                        },
                        success: (res) => {
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                            resolve()
                        },
                        fail: (error) => {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                            reject()
                        }
                    });
                }
            )
            await reviewSave
            this.reload();
        },

        // Redirect
        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId
            });
        },
        chatWindowRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/chat/chat-window/chat-window?contactId=' + userId
            });
        },
        emergencyCenterRedirect(userId) {
            uni.navigateTo({
                url: '/pages/order/order-detail/emergency-center/emergency-center'
            });
        },
    }
};
</script>

<style scoped>
.back {
    position: absolute;
    top: 58px;
    left: 10px;
    width: 35px;
    height: 35px;
}

.nav-bar {
    width: 38vw;
    border-radius: 50px;
    text-align: center;
    font-weight: bold;
    background-color: white;
    margin: 4vh;
    padding: 10px;
}

.divider {
    width: 90%;
    height: 2px;
    background-color: #f0f0f0;
    margin: 11px 0;;
}


.countdown-container {
    text-align: center;
    background-color: #44e1a6;
    padding: 2px 56px;
    border-radius: 50px;
    color: white;
    font-size: 16px;
    margin-top: 74px;
}


.price-text {
    font-weight: bold;
    color: #676A72;
}

.price {
    font-size: 56px;
    font-weight: bold;
    margin-top: 12px;
    font-family: system-ui;
}

.price-service-detail {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 10vh;
}

.client-addr-detail {
    background-color: white;
    width: 100vw;
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    border-radius: 30px 30px 0 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    height: 50vh;
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


.submit-section {
    text-align: center;
    margin-top: 20px;
}

</style>