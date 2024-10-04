<template>
<div class="price-request-container">
    <div class="mask" @click="close()"></div>
    <div class="widget-center price-request-anim">
        <div class="price-request-header">{{$t('component>order>increasedQuotedPriceRequest.requestHigherPay')}}</div>

        <!-- Input for Custom Price Request -->
        <div class="custom-price-request">
            <app-input
                mode="text"
                col="12"
                class="mb-2"
                :placeholder="'Enter your requested price (Min: ' + initialPrice.toFixed(2) + ', Max: ' + maxPrice.toFixed(2) + ')'"
                v-model="quotedPrice"
            />
        </div>

        <!-- Display Requested Price -->
        <div class="no-more-data">
            <div class="credit">{{$t('component>order>increasedQuotedPriceRequest.tips')}}</div>
        </div>

        <!-- Confirm Button -->
        <div class="center-h" style="width: 60%;">
            <app-button @click="confirmPriceRequest" shaped>{{$t('component>order>increasedQuotedPriceRequest.sendRequest')}}</app-button>
        </div>
    </div>
</div>
</template>

<script>
export default {
    data() {
        return {
            initialPrice: this.order.price || 0,
            quotedPrice: this.order.price || 0,
            maxPrice: this.order.price * 2,
        };
    },
    props: {order: Object},
    methods: {
        close() {
            this.$parent.priceRequestToggle(false);
        },

        confirmPriceRequest() {
            if (this.quotedPrice < this.initialPrice || this.quotedPrice > this.maxPrice) {
                uni.showToast({
                    title: this.$t('component>order>increasedQuotedPriceRequest.showToast.priceRange')+`¥${this.initialPrice.toFixed(2)} - ¥${this.maxPrice.toFixed(2)}`,
                    icon: 'none'
                });
            } else {
                // Submit the requested price update
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.save,
                    method: 'POST',
                    data: {
                        orderId: this.order.id,
                        servantId: uni.getStorageSync(getApp().globalData.data.userInfoKey)?.id,
                        quotedPrice: this.quotedPrice,
                    },
                    success: (res) => {
                        if (res.data.code == 0) {
                            uni.showToast({title: this.$t('component>order>increasedQuotedPriceRequest.showToast.sent'), icon: 'none'});
                            this.$parent.getOrder();
                            this.close();
                        } else {
                            uni.showToast({title: 'pub.showToast.fail', icon: 'none'});
                        }
                    },
                });
            }
        },
    },
};
</script>

<style scoped>
.price-request-container {
    z-index: 1000;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5); /* Dimmed background */
}

.widget-center {
    z-index: 1100;
    background-color: #ffffff;
    border-radius: 20rpx;
    padding: 40rpx 30rpx;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    width: 80%;
    text-align: center;
}

.price-request-header {
    font-size: 34rpx;
    font-weight: bold;
    margin-bottom: 20rpx;
}

.custom-price-request input {
    flex: 1;
    padding: 20rpx;
    border-radius: 30rpx;
    border: 1px solid #ccc;
    margin-right: 10rpx;
    font-size: 30rpx;
    width: 100%;
}

.no-more-data {
    margin: 20px 0;
}

.credit {
    font-size: 28rpx;
    color: #666;
}
</style>
