<template>
<div class="page-mono center" style="background-color: #f5f7fb">
    <div class="page-mono-header">
        <div v-if="paymentMethodType == 0" style="font-size: 2em">
            <span>{{ $t('profile>balance>withdraw>addPaymentAccount.addAilpay1') }}</span>
            <span style="font-weight: bold">{{ '"' + idCardName + '"' }}</span>
            <span>{{ $t('profile>balance>withdraw>addPaymentAccount.addAilpay2') }}</span>
        </div>
        <div v-else style="font-size: 2em">
            <span>{{ $t('profile>balance>withdraw>addPaymentAccount.addBankCard1') }}</span>
            <span style="font-weight: bold">{{ '"' + idCardName + '"' }}</span>
            <span>{{ $t('profile>balance>withdraw>addPaymentAccount.addBankCard2') }}</span>
        </div>
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
            v-model="issuanceLocationPlaceholder"
            @click="issuanceLocationToggle"
            disabled
        />
        <input
            type="text"
            v-model="bankcardData.issuer"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.issuerPlaceholder')"
        />
        <input
            type="text"
            v-model="bankcardData.identifier"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.bankCardPlaceholder')"
        />
        <picker
            mode="selector"
            :range="bankNameRanges"
            :value="bankName"
            @change="bindBankNameChange"
        >
            <span>{{ bankName === '' ? $t('profile>balance>withdraw>addPaymentAccount.bankNamePlaceholder') : bankName }}</span>
        </picker>
        <input
            type="text"
            v-model="mobileValidationCodeMatcher"
            :placeholder="$t('profile>balance>withdraw>addPaymentAccount.bankCardPlaceholder')"
        />
        <div class="input-with-button">
            <input
                type="text"
                :placeholder="$t('profile>balance>withdraw>addPaymentAccount.smsVerificationCodePlaceholder')"
            />
            <div class="button">{{ $t('pub.button.send') }}</div>
        </div>
        <app-title bold>{{ $t('profile>balance>withdraw>addPaymentAccount.verificationCodeTo') + $common.maskedMobile(mobile) }}</app-title>
    </div>

    <div class="fix-bottom">
        <app-button shaped size="very-large" @click="savePaymentMethod" width="85vw">
            {{ $t('pub.button.confirm') }}
        </app-button>
    </div>

    <cc-selectDity
        :province="province"
        :city="city"
        :area="area"
        :show="locationSelectorVisible"
        @sureSelectArea="bindIssuanceLocationChange"
    >
    </cc-selectDity>

</div>
</template>

<script>
import $common from "../../../../../utils/common";

export default {
    name: "add-payment-account",
    data() {
        return {
            paymentMethodType: 0,
            mobile: '',

            // ailpayAccount
            ailpayAccountData: {
                userId: '',
                name: ''
            },

            // bankcard
            idCardName: '',
            bankcardData: {
                userId: '',
                accountType: 0,
                issuanceLocation: '',
                issuer: '',
                identifier: '',
            },
            bankName: '',
            bankNameRanges: [],
            province: "广东省",
            city: "广州市",
            area: "天河区",
            mobileValidationCode: '',
            mobileValidationCodeMatcher: '',

            locationSelectorVisible: false,
        }
    },
    onLoad(params) {
        this.getBankName()
        this.paymentMethodType = params.paymentMethodType;
        this.bankcardData.userId = params.userId
        this.ailpayAccountData.userId = params.userId;
        this.idCardName = params.idCardName;
        this.mobile = params.mobile;
    },
    computed: {
        $common() {
            return $common
        },
        issuanceLocationPlaceholder() {
            return this.bankcardData.issuanceLocation
                ? this.bankcardData.issuanceLocation
                : this.$t('profile>balance>withdraw>addPaymentAccount.issuanceLocationPlaceholder');
        }
    },
    methods: {
        getBankName() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.bankCard.searchBank,
                method: 'POST',
                data: {
                    page: 1,
                    pageSize: 20,
                },
                success: (res) => {
                    if(res.data.status === 200) {
                        this.bankNameRanges = res.data.list.map(item => item.name);
                    }
                },
            });
        },

        bindIssuanceLocationChange(e) {
            let data = e;
            let address = data.province + data.city + data.area;
            this.locationSelectorVisible = false;
            this.bankcardData.issuanceLocation = address;
            this.province = data.province;
            this.city = data.city;
            this.area = data.area;
        },
        bindBankNameChange(e) {
            const bankNameIdx = parseInt(e.detail.value);
            this.bankName = this.bankNameRanges[bankNameIdx];
        },

        savePaymentMethod() {
            // Validate inputs
            if(this.paymentMethodType == 0 && !this.ailpayAccountData.name) {
                uni.showToast({title: this.$t('pub.showToast.finishForm'), icon: 'none'});
                return;
            }
            if(this.paymentMethodType == 1 && (
                !this.bankcardData.issuanceLocation ||
                !this.bankcardData.issuer ||
                !this.bankcardData.identifier ||
                !this.bankName
            )) {
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
                            uni.navigateBack()
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
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

        // Toggle
        issuanceLocationToggle() {
            this.locationSelectorVisible = true
        }
    },
};
</script>

<style scoped>

</style>