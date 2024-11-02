<template>
<div class="page-mono center" style="background-color: #f5f7fb">
    <div class="page-mono-header">
        <app-title type="h1" bold>{{ paymentMethodType == 0 ? $t('profile>balance>withdraw>addPaymentAccount.addAilpay') : $t('profile>balance>withdraw>addPaymentAccount.addBankCard') }}</app-title>
        <p class="center-h">{{ paymentMethodType == 0 ? $t('profile>balance>withdraw>addPaymentAccount.tipsAilpay') : $t('profile>balance>withdraw>addPaymentAccount.tipsBankCard') }}</p>
    </div>

    <div v-if="paymentMethodType==0" class="page-mono-form">
        <input
            type="text"
            v-model="ailpayAccountName"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.ailpayPlaceholder')"
        />
    </div>
    <div v-else class="page-mono-form">
        <picker
            mode="selector"
            :range="['Private', 'Company']"
            :value="bankcardData.accountType"
            @change="onAccountTypeChange"
        >
            <span>{{ bankcardData.accountType === -1 ? $t('profile>balance>withdraw>addPaymentAccount.accountTypePlaceholder') : (bankcardData.accountType === 0 ? $t('profile>balance>withdraw>addPaymentAccount.private') : $t('profile>balance>withdraw>addPaymentAccount.company')) }}</span>
        </picker>
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
        <app-button shaped size="very-large" @click="saveBankCard" width="85vw">
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
                accountType: -1
            },
            ailpayAccountName: ''
        }
    },
    onLoad(params) {
        this.paymentMethodType = params.paymentMethodType;
        this.bankcardData.userId = params.userId;
    },
    methods: {
        onAccountTypeChange(event) {
            this.bankcardData.accountType = parseInt(event.detail.value);
        },
        saveBankCard() {
            // Validate inputs
            if(this.paymentMethodType == 0 && !this.ailpayAccountName) {
                uni.showToast({title: this.$t('profile>balance>withdraw>addPaymentAccount.showToast.inputError'), icon: 'none', duration: 6000});
                return;
            }
            if(this.paymentMethodType == 1 && (!this.bankcardData.identifier || !this.bankcardData.issuer || this.bankcardData.accountType === -1)) {
                uni.showToast({title: this.$t('profile>balance>withdraw>addPaymentAccount.showToast.inputError'), icon: 'none', duration: 6000});
                return;
            }
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            // Proceed with the request if validation passes
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.bankcard.save,
                method: 'POST',
                data: {
                    ...this.bankcardData
                },
                success: async(res) => {
                    uni.hideLoading();
                    if(res.data.status === 200) {
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        this.$common.backToLastPage();
                    } else {
                        let errorMessage = '';
                        switch(res.data.message) {
                            case "Existed bank card":
                                errorMessage = this.$t('profile>balance>withdraw>addPaymentAccount.showToast.existedBankCard');
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
    }
};
</script>

<style scoped>

</style>
