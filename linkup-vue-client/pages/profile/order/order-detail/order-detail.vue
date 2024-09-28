<template>
<div class="page" style="background-color: #f3f2f6">
    <!-- Title -->
    <app-title type="h2" bold="true">{{ order.title }}</app-title>

    <!-- Price and Respondent Container -->
    <div class="app-container" style="background-color: white !important;">
        <div class="price-respondent-container">
            <!-- Price Section -->
            <div class="price-section">
                <app-title bold="true">报价</app-title>
                <p>{{ order.price }} ¥</p>
            </div>

            <!-- Divider -->
            <div class="divider"></div>

            <!-- Respondent Section -->
            <div class="respondent-section">
                <app-title bold="true">已抢单</app-title>
                <p>{{ order.candidateCount }} 人</p>
            </div>
        </div>
    </div>

    <!-- Countdown Timer -->
    <div v-if="order.countdownStartAt">
        <div v-if="countdown > 0" class="app-container" style="background-color: #feb327 !important;display: flex;justify-content: space-between">
            <div>
                <app-title type="h3" bold="true">请在剩余时间内选择一个达人</app-title>
                <p>{{ formatTime(countdown) }}</p>
            </div>
            <app-button type="small" color="red" shaped size="small" @click="cancelOrder">
                取消订单
            </app-button>
        </div>
        <div v-if="!countdown > 0" class="app-container" style="background-color: white !important;">
            <app-title type="h3" bold="true">订单已失效</app-title>
            <p>由于您未在规定时间内选择达人，订单已失效</p>
            <p>您的订单费用已退回至您的余额</p>
        </div>

    </div>

    <!-- Respondent Users Title -->
    <div class="mt-4">
        <app-title bold="true">已抢单用户</app-title>
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
                                选择达人
                            </app-button>
                        </div>
                    </demo-item>
                </z-swiper-item>
            </z-swiper>
        </div>
        <div v-else>
            <div class="no-more-data-text">
                暂时无人抢单
            </div>
        </div>
    </div>
</div>
</template>

<script>

export default {
    data() {
        return {
            orderId: '',
            order: {},
            servantList: [],
            countdown: 0, // Countdown in seconds
            countdownInterval: null,
            freeOrderPostingQuota: 0
        };
    },
    onLoad(params) {
        this.orderId = params.orderId;
        this.getOrder();
        this.getServantList();
    },
    onUnLoad() {
        // Clear the countdown interval when the component is destroyed
        if (this.countdownInterval) {
            clearInterval(this.countdownInterval);
        }
    },
    methods: {
        getOrder() {
            uni.request({
                url: getApp().globalData.data.requestUrl + '/order/search',
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
                },
            });
        },

        getRemainingFreeOrderPostingQuota() {
            uni.request({
                url: getApp().globalData.data.requestUrl + '/order/remaining-free-posting-quota',
                method: 'POST',
                data: {
                    userId: this.order.clientId
                },
                success: (res) => {
                    this.freeOrderPostingQuota = res.data.freeOrderPostingQuota;
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

            console.log("Raw countdownStartAt:", this.order.countdownStartAt);
            console.log("Parsed Start Time (milliseconds):", startTime);
            console.log("Countdown End Time (milliseconds):", countdownEndTime);
            console.log("Current Time (milliseconds):", currentTime);

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
                url: getApp().globalData.data.requestUrl + '/order-candidate/servants',
                method: 'POST',
                data: {
                    orderId: this.orderId
                },
                success: (res) => {
                    this.servantList = res.data.list;

                    // Fetch servantData for all users in parallel
                    const promises = this.servantList.map((user) => {
                        return new Promise((resolve) => {
                            uni.request({
                                url: getApp().globalData.data.requestUrl + '/user-servant/search',
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
            uni.showModal({
                title: '确认取消订单',
                content: `是否确定要取消订单？您今日的免费发单额度还剩${this.freeOrderPostingQuota}次。超出额度本订单只能回退定价的80%`,
                showCancel: true,
                confirmText: '确定',
                success: (res) => {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + '/order/cancel-order',
                        method: 'POST',
                        data: {
                            orderId: this.order.id,
                            status: 2
                        },
                        success: (res) => {

                        },
                    });
                },
            });
        },

        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId,
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
</style>
