<template>
<div class="page" style="background-color: #f3f2f6">


    <!--HEADER-->
    <div class="center-h">
        <div class="back" @click="back()">
            <img style="width: 22px;height: 22px" src="/static/page/register/back.svg">
        </div>
        <div class="nav-bar">
            {{ language != "zh-Hans" ? requiredServantType + " Service" : requiredServantTypeCn + "服务" }}
        </div>
    </div>
    <!--/HEADER-->

    <!--PROCESSING CONTAINER-->
    <div v-if="order.status == orderConstant.PROCESSING" class="center-h">
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
        <div class="client-addr-detail" >
            <div style="display: flex;justify-content: flex-start;margin: 18px 0 2px 0;width: 90%;">
                <div style="width: 80%; text-align: left;">
                    <p style="font-weight: bold;font-size:18px">{{ orderAddress.addressName }}</p>
                    <p style="font-weight: bold;font-size:18px">{{ orderAddress.detail }}</p>
                    <p >
                        {{ $common.stampToTime(this.order.serviceScheduleStart, {yyyy: false, ss: false}) }}
                        -
                        {{ $common.stampToTime(this.order.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false}) }}
                    </p>
                </div>
                <div style="margin-top: 14px;margin-left: 18px;">
                    <img style="width: 36px;height: 36px;" src="/static/page/order/siren-on.svg">
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
    <div v-if="order.status==orderConstant.COMPLETED" class="center-h">
        <div class="center-h">
            <img style="width: 140px" src="/static/page/order/check-double.svg">
        </div>
        <div class="completed-text">
            <div>{{ $t('profile>order>orderDetail.serviceComplete') }}</div>
        </div>

        <!-- Rating Section -->
        <div class="rating-section" >
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
            orderAddress: {},

            orderClient: {},

            requiredServantType: '',
            requiredServantTypeCn: '',

            serviceCountdown: 0,
            serviceCompleteCountdown: 0,

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
                    this.getOrderAddress();
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
                            console.log("if(this.isServiceInProgress) {")
                            const endTime = new Date(this.order.serviceScheduleEnd);
                            this.$common.calculateCountdown(null, endTime, (remainingTime) => {
                                this.serviceCompleteCountdown = remainingTime;
                            });
                        }

                        this.getServantType();
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
        getServantType(id) {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.serviceType.search,
                method: 'POST',
                data: {
                    id: id
                },
                success: (res) => {
                    var servantType = res.data.list[0]
                    this.requiredServantType = servantType.name
                    this.requiredServantTypeCn = servantType.nameCn
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
            if(!this.$common.isEmpty(this.order.performanceRating)) {
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