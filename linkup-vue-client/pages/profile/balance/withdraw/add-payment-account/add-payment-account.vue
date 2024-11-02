<template>
<div class="page-mono center" style="background-color: #f5f7fb">
    <div class="page-mono-header">
        <app-title type="h1" bold>{{ paymentMethodType == 0 ? $t('profile>balance>withdraw>addPaymentAccount.addAilpay') : $t('profile>balance>withdraw>addPaymentAccount.addBankCard') }}</app-title>
        <p class="center-h">{{ paymentMethodType == 0 ? $t('profile>balance>withdraw>addPaymentAccount.tipsAilpay') : $t('profile>balance>withdraw>addPaymentAccount.tipsBankCard') }}</p>
    </div>

    <div v-if="paymentMethodType==0" class="page-mono-form">
        <input
            type="text"
            v-model="ailpayAccountData.name"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.ailpayPlaceholder')"
        />
    </div>
    <div v-else class="page-mono-form">
        <input
            type="text"
            v-model="bankcardData.identifier"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.bankCardPlaceholder')"
        />
        <input
            type="text"
            v-model="bankcardData.issuer"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.issuerPlaceholder')"
        />

    </div>

    <div class="fix-bottom">
        <app-button shaped size="very-large" @click="savePaymentMethod" width="85vw">
            {{ $t('pub.button.confirm') }}
        </app-button>
    </div>

</div>
</template>

<script>
export default {
    name: "add-payment-account",
    data() {
        return {
            paymentMethodType: 0,
            bankcardData: {
                userId: '',
                identifier: '',
                issuer: '',
                accountType: 0
            },
            ailpayAccountData: {
                userId: '',
                name:''
            }
        }
    },
    onLoad(params) {
        this.paymentMethodType = params.paymentMethodType;
        this.bankcardData.userId = params.userId
        this.ailpayAccountData.userId = params.userId;
    },
    methods: {
        savePaymentMethod() {
            // Validate inputs
            if(this.paymentMethodType == 0 && !this.ailpayAccountData.name) {
                uni.showToast({title: this.$t('pub.showToast.finishForm'), icon: 'none'});
                return;
            }
            if(this.paymentMethodType == 1 && (!this.bankcardData.identifier || !this.bankcardData.issuer)) {
                uni.showToast({title: this.$t('pub.showToast.finishForm'), icon: 'none'});
                return;
            }
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            if(this.paymentMethodType == 0) {
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.ailpayAccount.save,
                    method: 'POST',
                    data: {
                        ...this.ailpayAccountData
                    },
                    success: async(res) => {
                        uni.hideLoading();
                        if(res.data.status === 200) {
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                            uni.navigateBack()
                        } else {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                        }
                    },
                    fail: () => {
                        uni.hideLoading();
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    }
                });
            } else if(this.paymentMethodType == 1) {
                this.$common.removeSpace(this.bankcardData.identifier);
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.bankCard.save,
                    method: 'POST',
                    data: {
                        ...this.bankcardData
                    },
                    success: async(res) => {
                        uni.hideLoading();
                        if(res.data.status === 200) {
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                            this.$common.backToLastPage()
                        } else {
                            let errorMessage = '';
                            switch(res.data.message) {
                                case "Data Existed":
                                    errorMessage = this.$t('pub.showToast.dataExisted');
                                    break;
                                case "Match failed":
                                    errorMessage = this.$t('profile>balance>withdraw>addPaymentAccount.showToast.matchFailed');
                                    break;
                                case "Invalid card":
                                    errorMessage = this.$t('profile>balance>withdraw>addPaymentAccount.showToast.invalidCard');
                                    break;
                                default:
                                    errorMessage = this.$t('profile>balance>withdraw>addPaymentAccount.showToast.inputError');
                            }
                            uni.showToast({title: errorMessage, icon: 'none', duration: 6000});
                        }
                    },
                    fail: () => {
                        uni.hideLoading();
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    }
                });
            }
        },
    },
};
</script>

<style scoped>

</style>