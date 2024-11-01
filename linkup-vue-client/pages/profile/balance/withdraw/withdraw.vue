<template>
<div class="page" style="background-color: #f3f2f6">
    <div class="mb-8">
        <div v-if="$common.isEmpty(ailPayAccount)" class="center mb-2">
            <img style="width: 74%;height: 160px;" src="/static/temp/img.png">
            <app-title bold style="position: absolute;top: 114px;color: #7f7f7f">{{ $t('profile>balance>withdraw.addAilpay') }}</app-title>
        </div>
        <div v-if="!bankcardList.length>0" class="center mb-2">
            <img style="width: 74%;height: 160px;" src="/static/temp/img.png">
            <app-title bold style="position: absolute;top: 296px;color: #7f7f7f">{{ $t('profile>balance>withdraw.addBankCard') }}</app-title>
        </div>
        <z-swiper v-else v-model="bankcardList" :options="{slidesPerView: 'auto', centeredSlides: true, spaceBetween: 14}" style="width: 100%">
            <z-swiper-item v-for="(bankcard, index) in bankcardList" :key="index" :custom-style="{width: '500rpx'}">
                <demo-item :item="bankcard">
                    <app-container :color="bankcard.backgroundColor" :style="{'color':bankcard.color}" col="12">
                        <div class="justify-SB">
                            <img style="width: 50px; height: 50px;" :src="bankcard.logo" mode="aspectFill"></img>
                            <app-title bold="true">{{ bankcard.cardNumber }}</app-title>
                        </div>
                        <app-title style="text-align: end;display: block;margin-top: 50px" type="h3" bold="true">{{ bankcard.cardType }}</app-title>
                    </app-container>
                </demo-item>
            </z-swiper-item>
        </z-swiper>
    </div>
    <app-title type="h2" bold>{{ $t('profile>balance>withdraw.withdrawHistory') }}</app-title>
</div>
</template>

<script>
import common from "../../../../utils/common";
import app from "../../../../App.vue";
import $common from "../../../../utils/common";

export default {
    name: "withdraw",
    computed: {
        $common() {
            return $common
        },
        common() {
            return common;
        },
        app() {
            return app
        }
    },
    data() {
        return {
            bankcardList: [],
            ailPayAccount: [],
            user: '',
            bankCardNumber: ''
        };
    },
    onLoad() {
        this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey)
        this.reload();
    },
    methods: {
        reload() {
            this.getBankCards()
        },
        getBankCards() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.bankcard.search,
                method: 'POST',
                data: {
                    userId: this.user.id,
                },
                success: async(res) => {
                    this.bankcardList = res.data.data
                },
                fail: () => {
                    uni.hideLoading();
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
        },
        addBankCard() {
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
                        uni.showToast({title: this.$t('profile>balance>withdraw.showToast.authSuccess'), icon: 'none'});
                        this.reload()
                    } else {
                        console.log(res.data)
                        if(res.data.message == "Existed bank card") {
                            uni.showToast({title: this.$t('profile>balance>withdraw.showToast.existedBankCard'), icon: 'none'});
                        } else if(res.data.message == "Match failed") {
                            uni.showToast({title: this.$t('profile>balance>withdraw.showToast.matchFailed'), icon: 'none'});
                        } else if(res.data.message == "Invalid card") {
                            uni.showToast({title: this.$t('profile>balance>withdraw.showToast.invalidCard'), icon: 'none'});
                        } else {
                            uni.showToast({title: this.$t('profile>balance>withdraw.showToast.inputError'), icon: 'none'});
                        }
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