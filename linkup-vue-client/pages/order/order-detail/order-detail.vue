<template>
<div class="page" style="background-color: #f3f2f6">
    <!-- Title -->
    <app-title type="h1" bold="true">{{ order.title }}</app-title>

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

    <!-- Respondent Users Title -->
    <app-title bold="true" style="margin-top: 20px;">已抢单用户</app-title>

    <!-- Respondent User List -->
    <app-container color="#fff" col="12">
        <div v-for="(user, index) in servantList" :key="index" class="user-item" :class="{ 'no-border': index === servantList.length - 1 }">
            <div class="user-avatar" :style="{ backgroundColor: user.color }"></div>
            <p>{{ user.name }}</p>
        </div>
    </app-container>
</div>
</template>

<script>
export default {
    data() {
        return {
            orderId: '',
            order: {},
            servantList: [
                {name: 'xxxx', color: '#c4c4c4'},
                {name: 'xxxx', color: '#6fc2af'},
                {name: 'xxxx', color: '#2c3e50'}
            ]
        };
    },
    onLoad(params) {
        this.orderId = params.orderId;
        this.getOrder();
        this.getServantList();
    },
    methods: {
        getOrder() {
            uni.request({
                url: getApp().globalData.requestUrl + '/order/search',
                method: 'POST',
                data: {
                    id: this.orderId
                },
                success: (res) => {
                    this.order = res.data.orderList[0]
                },
            });
        },
        getServantList(){
            uni.request({
                url: getApp().globalData.requestUrl + '/order-candidate/get',
                method: 'POST',
                data: {
                    id: this.orderId
                },
                success: (res) => {
                    this.order = res.data.order
                },
            });
        }
    }
};
</script>

<style scoped>
/* Container for Price and Respondent Count */
.price-respondent-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: center;
}

/* Sections for Price and Respondent Count */
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

/* User Item */
.user-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #ddd;
}

.user-item.no-border {
    border-bottom: none;
}

.user-avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    margin-right: 10px;
}
</style>