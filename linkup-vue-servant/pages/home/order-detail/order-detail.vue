<template>
<div class="page" style="background-color: #f3f2f6">

    <app-title v-if="order.status!=orderConstant.COMPLETED" type="h2" bold="true">
        {{ language != "zh-Hans" ? order.serviceType.name + " Service" : order.serviceType.nameCn + "服务" }}
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
    <div class="order-detail-section">
        <div class="order-detail-item">
            <div class="icon"><img src="/static/page/home/order-detail/timer.svg" alt="Payment Icon"/></div>
            <div class="details">
                <span class="label">{{ $t('home>orderDetail.orderInfoDetail.serviceTime') }}</span>
                <p class="value">
                    {{ $common.stampToTime(order.serviceScheduleStart, {yyyy: false, ss: false}) }}
                    -
                    {{ $common.stampToTime(order.serviceScheduleEnd, {yyyy: false, ss: false, MM: false, dd: false}) }}
                </p>
            </div>
        </div>
        <div class="order-detail-item">
            <div class="icon"><img src="/static/page/home/order-detail/location-dot.svg" alt="Payment Icon"/></div>
            <div class="details">
                <span class="label">{{ $t('home>orderDetail.orderInfoDetail.address') }}</span>
                <p class="value">{{ order.address.address }} </p>
                <p class="value">{{ order.address.addressName }} </p>
                <p class="value">{{ order.address.detail }}</p>
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
                    this.getOrderCandidate()
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

.order-detail span {
    font-weight: bold
}

.order-detail-section {
    padding: 15px;
    border-radius: 15px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    color: #333;
    background-color: white;
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

</style>
