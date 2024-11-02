<template>
<div class="page-mono center" style="background-color: #f5f7fb">
    <div class="page-mono-header">
        <app-title type="h1" bold>{{ paymentMethodType == 0 ? $t('profile>balance>withdraw>addPaymentAccount.addAilpay') : $t('profile>balance>withdraw>addPaymentAccount.addBankCard') }}</app-title>
        <p class="center-h">{{ paymentMethodType == 0 ? $t('profile>balance>withdraw>addPaymentAccount.tipsAilpay') : $t('profile>balance>withdraw>addPaymentAccount.tipsBankCard') }}</p>
    </div>

    <div class="page-mono-form">
        <input
            v-if="paymentMethodType==0"
            type="text"
            v-model="bankCardNumber"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.ailpayPlaceholder')"
        />
        <input
            v-else
            type="text"
            v-model="bankCardNumber"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.bankCardPlaceholder')"
        />
    </div>
    <div class="fix-bottom">
        <app-button shaped size="very-large" @click="validateBankCard" width="85vw">
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
            user: {},
            bankCardNumber: '',
        }
    },
    onLoad(params) {
        this.paymentMethodType = params.paymentMethodType;
        this.user = {
            id: params.userId,
            idCardName: params.userName,
            idCardNumber: params.idCardNumber
        };
    },
    methods: {
        validateBankCard() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.bankcard.validation,
                method: 'POST',
                data: {
                    userId: this.user.id,
                    name: this.user.idCardName,
                    idCardNumber: this.user.idCardNumber,
                    bankCardNumber: this.bankCardNumber,
                },
                success: async(res) => {
                    uni.hideLoading();
                    if(res.data.status === 200) {
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        this.$common.backToLastPage()
                    } else {
                        if(res.data.message == "Existed bank card") {
                            uni.showToast({title: this.$t('profile>balance>withdraw>addPaymentAccount.showToast.existedBankCard'), icon: 'none', duration: 6000});
                        } else if(res.data.message == "Match failed") {
                            uni.showToast({title: this.$t('profile>balance>withdraw>addPaymentAccount.showToast.matchFailed'), icon: 'none', duration: 6000});
                        } else if(res.data.message == "Invalid card") {
                            uni.showToast({title: this.$t('profile>balance>withdraw>addPaymentAccount.showToast.invalidCard'), icon: 'none', duration: 6000});
                        } else {
                            uni.showToast({title: this.$t('profile>balance>withdraw>addPaymentAccount.showToast.inputError'), icon: 'none', duration: 6000});
                        }
                    }
                },
                fail: () => {
                    uni.hideLoading();
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
        },
    },
};
</script>

<style scoped>

</style>