<template>
<div class="page" style="background-color: #f3f2f6">
    <div class="mb-2">
        <!-- Alipay -->
        <div v-if="$common.unDefined(ailPayAccount)" class="center mt-1 mb-2" style="flex-direction: column" @click="addPaymentAccountRedirect(0)">
            <img style="width: 74%;height: 164px" :src="app.globalData.data.ossImageRequestUrl+'/miscellaneous/card-slot.jpg'">
            <app-title bold style="color: #7f7f7f;position: relative;top: -60px">{{ $t('profile>balance>withdraw.addAilpay') }}</app-title>
        </div>
        <div v-else class="mb-1">
            <div class="center ">
                <app-container color="#3474ff" style="color: #FFF" col="12" @click="withdrawToggle(0, ailPayAccount.id)">
                    <div class="justify-SB" style="width: 60vw">
                        <img style="width: 50px; height: 50px;" :src="app.globalData.data.ossIconRequestUrl+'/page/profile/balance/withdraw/ailpay.jpg'" mode="aspectFill"/>
                        <div style="text-align: end">
                            <app-title bold type="h3">{{ ailPayAccount.name }}</app-title>
                        </div>
                    </div>
                    <app-title class="mt-4" style="text-align: end;display: block;">
                        <img style="width: 120px; height: 40px;" :src="app.globalData.data.ossIconRequestUrl+'/page/profile/balance/withdraw/ailpay-text.jpg'" mode="aspectFill"/>
                    </app-title>
                </app-container>
            </div>
            <div class="center">
                <div style="width: 60vw;text-align: end;">
                    <div class="flex" style="justify-content: flex-end" @click="unlinkAilpay">
                        <img class="icon" style="margin-right: 4px;" :src="app.globalData.data.ossIconRequestUrl+'/common/link-slash.svg'">
                        <div class="tips" style="color: #808080">{{ $t('profile>balance>withdraw.unlink') }}</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bank Card -->
        <div v-if="!bankcardList.length>0" class="center mb-2" style="flex-direction: column" @click="addPaymentAccountRedirect(1)">
            <img style="width: 74%;height: 164px;" :src="app.globalData.data.ossImageRequestUrl+'/miscellaneous/card-slot.jpg'">
            <app-title bold style="color: #7f7f7f;position: relative;top: -60px">{{ $t('profile>balance>withdraw.addBankCard') }}</app-title>
        </div>
        <z-swiper v-else v-model="bankcardList" :options="{slidesPerView: 'auto', centeredSlides: true, spaceBetween: 14}" style="width: 100%">
            <z-swiper-item v-for="(bankcard, index) in bankcardList" :key="index" :custom-style="{width: '500rpx'}">
                <demo-item :item="bankcard">
                    <app-container color="#FFF" style="color: #505050" col="12" @click="withdrawToggle(1, bankcard.id)">
                        <div class="justify-SB">
                            <img style="width: 50px; height: 50px;" :src="bankcard.bank.logo" mode="aspectFill"/>
                            <div style="text-align: end">
                                <app-title bold type="h3">{{ bankcard.type == 0 ? $t('profile>balance>withdraw.debitCard') : $t('profile>balance>withdraw.creditCard') }}</app-title>
                                <app-title @click.stop="cardVisibilityToggle(index)">{{ getDisplayIdentifier(bankcard, index) }}</app-title>
                            </div>
                        </div>
                        <app-title v-if="language != 'zh-Hans'" class="mt-4" style="text-align: end;display: block;" type="h1" bold="true">
                            {{ bankcard.bank.abbr }}
                        </app-title>
                        <app-title v-else class="mt-4" style="text-align: end;display: block;" type="h2" bold="true">
                            {{ bankcard.bank.name }}
                        </app-title>
                    </app-container>
                    <div class="center">
                        <div style="width: 60vw;text-align: end;">
                            <div class="flex" style="justify-content: flex-end" @click="deleteBankCard(bankcard.id)">
                                <img class="icon" style="margin-right: 4px;" :src="app.globalData.data.ossIconRequestUrl+'/common/trash-can.svg'">
                                <div class="tips" style="color: #808080">{{ $t('pub.button.delete') }}</div>
                            </div>
                        </div>
                    </div>
                    <img v-if="index === bankcardList.length - 1" src="/static/common/create-gray.svg" class="right-icon" @click="addPaymentAccountRedirect(1)"/>
                </demo-item>
            </z-swiper-item>
        </z-swiper>
    </div>

    <app-container color="#f3f2f6">
        <div class="tips" style="text-align: start">{{ $t('profile>balance>withdraw.tips') }}</div>
    </app-container>

    <app-title v-if="withdrawalRequestList.length>0" type="h2" bold>{{ $t('profile>balance>withdraw.withdrawHistory') }}</app-title>
    <scroll-view
        :scroll-top="0"
        scroll-y="true"
        style="height: 20vh"
        @scrolltolower="onReachBottom"
    >
        <div
            class="app-container transaction-item"
            v-for="withdrawalRequest in withdrawalRequestList"
            :key="withdrawalRequest.id"
        >
            <div class="transaction-icon">
                <img :src="withdrawalRequest.icon" alt="Received"/>
            </div>
            <div class="transaction-details">
                <div class="transaction-title">
                    <span>¥{{ withdrawalRequest.amount }}</span>
                </div>
                <div class="transaction-detail">
                    <span>
                        {{ $common.timeToStampRecord(withdrawalRequest.createdAt) }}
                    </span>
                </div>
            </div>
        </div>
        <div v-if="loading" class="loading-text">{{ $t('pub.page.loading') }}</div>
        <div v-else-if="!hasMore" class="no-more-data-container-list">{{ $t('pub.page.noMoreData') }}</div>
    </scroll-view>

    <Withdraw v-if="withdrawVisible" :user="user" :method="method" :methodId="methodId"></Withdraw>
</div>
</template>

<script>
import app from "../../../../App.vue";
import $common from "../../../../utils/common";
import Withdraw from "../../../../components/page/balance/withdraw.vue";

export default {
    name: "withdraw",
    computed: {
        $common() {
            return $common;
        },
        app() {
            return app;
        },
    },
    components: {
        Withdraw
    },
    data() {
        return {
            user: '',

            ailPayAccount: [],
            bankcardList: [],
            showFullIdentifier: {},

            withdrawalRequestList: [],

            method: null,
            methodId: null,

            withdrawVisible: false,
        };
    },
    async onShow() {
        this.user = await this.$common.getUser(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)
        this.reload();
    },
    methods: {
        reload() {
            this.getBankCards();
            this.getAilpayAccount();
            this.getWithdrawalHistory()
        },
        getBankCards() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.bankCard.search,
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
        getAilpayAccount() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.ailpayAccount.search,
                method: 'POST',
                data: {
                    userId: this.user.id,
                },
                success: (res) => {
                    this.ailPayAccount = res.data.list[0];
                },
                fail: () => {
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
        },
        getWithdrawalHistory() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.withdrawalRequest.search,
                method: 'POST',
                data: {
                    userId: this.user.id,
                },
                success: (res) => {
                    this.withdrawalRequestList = res.data.list;
                },
                fail: () => {
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
        },

        getDisplayIdentifier(bankcard, index) {
            if(this.showFullIdentifier[index]) {
                return bankcard.identifier;
            } else {
                return "···· ···· ···· " + bankcard.identifier.slice(-4);
            }
        },

        unlinkAilpay() {
            uni.showModal({
                title: this.$t('profile>balance>withdraw.unlinkAilpayModal.title'),
                content: this.$t('profile>balance>withdraw.unlinkAilpayModal.content'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: (res) => {
                    if(res.confirm) {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + this.$API.ailpayAccount.delete,
                            method: 'POST',
                            data: {
                                id: this.ailPayAccount.id
                            },
                            success: (res) => {
                                uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                                this.reload()
                            },
                            fail: (err) => {
                                uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                            }
                        });
                    }
                },
            });
        },
        deleteBankCard(id) {
            uni.showModal({
                title: this.$t('profile>balance>withdraw.deleteBankCardModal.title'),
                content: this.$t('profile>balance>withdraw.deleteBankCardModal.content'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: (res) => {
                    if(res.confirm) {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + this.$API.bankCard.delete,
                            method: 'POST',
                            data: {
                                id: id
                            },
                            success: (res) => {
                                uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                                this.reload()
                            },
                            fail: (err) => {
                                uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                            }
                        });
                    }
                },
            });
        },


        // Toggle
        cardVisibilityToggle(index) {
            this.showFullIdentifier[index] = !this.showFullIdentifier[index];
        },
        withdrawToggle(method, methodId) {
            this.method = method;
            this.methodId = methodId;
            this.withdrawVisible = !this.withdrawVisible
        },

        // Redirect
        addPaymentAccountRedirect(type) {
            uni.navigateTo({
                url: `/pages/profile/balance/withdraw/add-payment-account/add-payment-account?paymentMethodType=${type}&userId=${this.user.id}&idCardName=${this.user.idCardName}`
            });
        },
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

.currency-type-selection button {
    flex: 1;
    background-color: transparent;
    color: white;
    border: none;
    border-radius: 50px;
    margin: 0 5px;
    cursor: pointer;
    font-weight: bold;
    font-size: 16px;
    transition: background-color 0.3s;
}

.currency-type-selection button.active {
    background-color: white;
    color: #0f172a;
}

.currency-type-selection button:not(.active) {
    color: white;
}

.currency-type-selection button:not(.active):hover {
    background-color: rgba(255, 255, 255, 0.2);
}

/* Existing styles */
.transaction-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 10px 0;
    padding: 10px;
    background-color: white;
    border-radius: 10px;
}

.transaction-icon {
    padding: 6px;
    width: 40px;
    height: 40px;
    justify-content: center;
    display: flex;
    align-items: center;
}

.transaction-icon img {
    border-radius: 5px;
    width: 40px;
    height: 40px;
}

.transaction-details {
    flex-grow: 1;
    padding-left: 20px;
}

.transaction-title {
    font-weight: bold;
    font-size: 16px;
    color: #282e5c;
}

.transaction-detail {
    font-size: 14px;
    color: #787ea1;
}
</style>
