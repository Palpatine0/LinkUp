<template>
<div class="price-request-container">
    <div class="mask" @click="close()"></div>
    <div class="widget-center price-request-anim">
        <div v-if="optType==0" class="price-request-header">{{ $t('component>order>increasedQuotedPriceRequest.requestHigherPay') }}</div>
        <div v-else class="price-request-header">{{ $t('component>order>increasedQuotedPriceRequest.updateQuotedPrice') }}</div>

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
            <div class="credit">{{ $t('component>order>increasedQuotedPriceRequest.tips') }}</div>
            <div v-if="optType==1" class="credit bold">{{ $t('component>order>increasedQuotedPriceRequest.updateQuotedPriceTips') }}</div>
        </div>

        <!-- Confirm Button -->
        <div class="center-h" style="width: 60%;">
            <app-button @click="confirmPriceRequest" shaped>{{ $t('component>order>increasedQuotedPriceRequest.sendRequest') }}</app-button>
        </div>
    </div>
</div>
</template>

<script>
export default {
    data() {
        return {
            orderCandidateId: '',
            initialPrice: this.order.price || 0,
            quotedPrice: this.order.price || 0,
            maxPrice: this.order.price * 2,
        };
    },
    props: {
        order: Object,
        optType: Number
    },
    async mounted() {
        if(this.optType == 1) {
            await this.getOrderCandidate()
        }
    },
    methods: {
        close() {
            this.$parent.priceRequestToggle(false);
        },
        async getOrderCandidate() {
            const getOrderCandidate = () => {
                return new Promise(
                    (resolve, reject) => {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.search,
                            method: 'POST',
                            data: {
                                orderId: this.order.id,
                                servantId: uni.getStorageSync(getApp().globalData.data.userInfoKey)?.id,
                            },
                            success: (res) => {
                                if(res.data.status == 200) {
                                    let orderCandidate = res.data.list[0];
                                    this.initialPrice = orderCandidate.quotedPrice
                                    this.quotedPrice = orderCandidate.quotedPrice
                                    this.orderCandidateId = orderCandidate.id
                                    resolve()
                                }
                            },
                        });
                    }
                )
            }
            await getOrderCandidate()
        },
        confirmPriceRequest() {
            if(this.quotedPrice < this.initialPrice || this.quotedPrice > this.maxPrice) {
                uni.showToast({
                    title: this.$t('component>order>increasedQuotedPriceRequest.showToast.priceRange') + `¥${this.initialPrice.toFixed(2)} - ¥${this.maxPrice.toFixed(2)}`,
                    icon: 'none'
                });
            } else {

                if(this.optType == 0) {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.save,
                        method: 'POST',
                        data: {
                            orderId: this.order.id,
                            servantId: uni.getStorageSync(getApp().globalData.data.userInfoKey)?.id,
                            quotedPrice: this.quotedPrice,
                        },
                        success: (res) => {
                            if(res.data.status == 200) {
                                uni.showToast({title: this.$t('component>order>increasedQuotedPriceRequest.showToast.sent'), icon: 'none'});
                                this.$parent.getOrder();
                                this.close();
                            } else {
                                uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                            }
                        },
                        fail: () => {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                        },
                    });
                } else {
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.orderCandidate.update,
                        method: 'POST',
                        data: {
                            id: this.orderCandidateId,
                            orderId: this.order.id,
                            servantId: uni.getStorageSync(getApp().globalData.data.userInfoKey)?.id,
                            quotedPrice: this.quotedPrice,
                        },
                        success: (res) => {
                            if(res.data.status == 200) {
                                uni.showToast({title: this.$t('component>order>increasedQuotedPriceRequest.showToast.sent'), icon: 'none'});
                                this.$parent.getOrder();
                                this.close();
                            } else {
                                uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                            }
                        },
                        fail: () => {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                        },
                    });
                }
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
