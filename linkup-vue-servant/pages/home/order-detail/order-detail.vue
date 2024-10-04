<template>
<div class="page" style="background-color: #f3f2f6">
    <!-- Title -->
    <app-title type="h2" bold="true">
        {{ language != "zh-Hans" ? order.title : order.titleCn }}
    </app-title>

    <!-- Price and Respondent Container -->
    <div class="app-container" style="background-color: white !important;">
        <div class="price-respondent-container">
            <!-- Price Section -->
            <div class="price-section">
                <app-title bold="true">{{ $t('home>orderDetail.orderInfoBasic.price') }}</app-title>
                <p>¥{{ order.price }}</p>
            </div>

            <!-- Divider -->
            <div class="divider"></div>

            <!-- Respondent Section -->
            <div class="respondent-section">
                <app-title bold="true">{{ $t('home>orderDetail.orderInfoBasic.totalCandidates') }}</app-title>
                <p>{{ order.candidateCount }}</p>
            </div>
        </div>
    </div>


    <!-- Order Detail -->
    <div class="mt-4" style="color: grey">
        <div class="order-detail">
            <span>{{ $t('home>orderDetail.orderInfoDetail.orderTime') }}:</span> {{ $common.stampToTime(order.createdAt) }}
        </div>
        <div class="order-detail">
            <span>{{ $t('home>orderDetail.orderInfoDetail.address') }}:</span>
            <div style="flex-direction: column;text-align: end;width: 70vw;">
                <div>{{ orderAddress.address }}</div>
                <div>{{ orderAddress.addressName }}</div>
                <div>{{ orderAddress.detail }}</div>
            </div>
        </div>
    </div>

    <div v-if="order.status==0" class="fix-bottom">
        <app-button v-if="isQuotedPriceRequestSent" shaped >
            {{ $t('home>orderDetail.acceptOrderSent') }}
        </app-button>
        <app-button v-else shaped @click="priceRequestToggle">
            {{ $t('home>orderDetail.acceptOrder') }}
        </app-button>
    </div>

    <IncreasedQuotedPriceRequest v-if="increasedQuotedPriceRequestVisible" :order="order"></IncreasedQuotedPriceRequest>
</div>
</template>

<script>

import $common from "../../../utils/common";
import IncreasedQuotedPriceRequest from "../../../components/page/order/increased-quoted-price-request.vue";

export default {
    components: {
        IncreasedQuotedPriceRequest
    },
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

            isQuotedPriceRequestSent: false,
            increasedQuotedPriceRequestVisible: false,
        };
    },
    onLoad(params) {
        this.orderId = params.orderId;
        this.getOrder();
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
                    this.getOrderCandidate()
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
        getOrderCandidate() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.search,
                method: 'POST',
                data: {
                    orderId: this.orderId
                },
                success: (res) => {
                    const userId = uni.getStorageSync(getApp().globalData.data.userInfoKey).id;
                    this.isQuotedPriceRequestSent = res.data.list.some(item => item.servantId === userId);
                },
            });
        },

        // Toggle
        priceRequestToggle() {
            this.increasedQuotedPriceRequestVisible = !this.increasedQuotedPriceRequestVisible
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
</style>
