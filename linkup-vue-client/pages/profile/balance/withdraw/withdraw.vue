<template>
<div class="page" style="background-color: #f3f2f6">
    <div class="mb-2">
        <div v-if="$common.isEmpty(ailPayAccount)" class="center mb-2" @click="addPaymentAccountRedirect(0)">
            <img style="width: 74%;height: 160px;" :src="app.globalData.data.ossImageRequestUrl+'/miscellaneous/card-slot.jpg'">
            <app-title bold style="position: absolute;top: 114px;color: #7f7f7f">{{ $t('profile>balance>withdraw.addAilpay') }}</app-title>
        </div>
        <div v-if="!bankcardList.length>0" class="center mb-2" @click="addPaymentAccountRedirect(1)">
            <img style="width: 74%;height: 160px;" :src="app.globalData.data.ossImageRequestUrl+'/miscellaneous/card-slot.jpg'">
            <app-title bold style="position: absolute;top: 296px;color: #7f7f7f">{{ $t('profile>balance>withdraw.addBankCard') }}</app-title>
        </div>
        <z-swiper v-else v-model="bankcardList" :options="{slidesPerView: 'auto', centeredSlides: true, spaceBetween: 14}" style="width: 100%">
            <z-swiper-item v-for="(bankcard, index) in bankcardList" :key="index" :custom-style="{width: '500rpx'}">
                <demo-item :item="bankcard">
                    <app-container color="#FFF" style="color: #505050" col="12">
                        <div class="justify-SB">
                            <img style="width: 50px; height: 50px;" :src="bankcard.bank.logo" mode="aspectFill"/>
                            <div style="text-align: end">
                                <app-title bold type="h3">{{ bankcard.type == 0 ? $t('profile>balance>withdraw.debitCard') : $t('profile>balance>withdraw.creditCard') }}</app-title>
                                <app-title @click.stop="toggleCardVisibility(index)">{{ getDisplayIdentifier(bankcard, index) }}</app-title>
                            </div>
                        </div>
                        <app-title v-if="language != 'zh-Hans'" class="mt-4" style="text-align: end;display: block;" type="h1" bold="true">
                            {{ bankcard.bank.abbr }}
                        </app-title>
                        <app-title v-else class="mt-4" style="text-align: end;display: block;" type="h2" bold="true">
                            {{ bankcard.bank.name }}
                        </app-title>
                    </app-container>
                    <img v-if="index === bankcardList.length - 1" src="/static/common/create-gray.svg" class="right-icon" @click="addPaymentAccountRedirect(1)"/>
                </demo-item>
            </z-swiper-item>
        </z-swiper>
    </div>
    <app-container color="#f3f2f6">
        <div class="tips">{{ $t('profile>balance>withdraw.tips') }}</div>
    </app-container>
    <app-title type="h2" bold>{{ $t('profile>balance>withdraw.withdrawHistory') }}</app-title>
</div>
</template>

<script>
import app from "../../../../App.vue";
import $common from "../../../../utils/common";

export default {
    name: "withdraw",
    computed: {
        $common() {
            return $common;
        },
        app() {
            return app;
        }
    },
    data() {
        return {
            bankcardList: [],
            ailPayAccount: [],
            user: '',
            showFullIdentifier: {} // Track visibility of full card numbers by index
        };
    },
    onShow() {
        this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey);
        this.reload();
    },
    methods: {
        reload() {
            this.getBankCards();
        },
        getBankCards() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.bankcard.search,
                method: 'POST',
                data: {
                    userId: this.user.id,
                },
                success: (res) => {
                    this.bankcardList = res.data.list;
                    // Initialize showFullIdentifier for each card
                    this.bankcardList.forEach((_, index) => {
                        this.$set(this.showFullIdentifier, index, false);
                    });
                },
                fail: () => {
                    uni.hideLoading();
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
        },
        addPaymentAccountRedirect(type) {
            const {id, idCardName, idCardNumber} = this.user;
            uni.navigateTo({
                url: `/pages/profile/balance/withdraw/add-payment-account/add-payment-account?paymentMethodType=${type}&userId=${id}&userName=${idCardName}&idCardNumber=${idCardNumber}`
            });
        },
        toggleCardVisibility(index) {
            this.showFullIdentifier[index] = !this.showFullIdentifier[index];
        },
        getDisplayIdentifier(bankcard, index) {
            if(this.showFullIdentifier[index]) {
                return bankcard.identifier;
            } else {
                return "···· ···· ···· " + bankcard.identifier.slice(-4);
            }
        }
    }
};
</script>

<style scoped>
.right-icon {
    width: 40px;
    height: 40px;
    margin-left: 10px;
    vertical-align: middle;
    position: absolute;
    top: 70px;
    left: 98%;
}

</style>
