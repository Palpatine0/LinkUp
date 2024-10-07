<template>
<div class="charge-container">
    <div class="mask" @click="close()"></div>
    <div class="widget-center charge-anim">
        <div class="charge-header">{{ $t('component>balance>coinDeposit.depositTitle') }}</div>

        <!-- Predefined Amount Buttons -->
        <div class="charge-options">
            <div
                v-for="amount in predefinedAmounts"
                :key="amount"
                class="app-button-outlined"
                @click="setAmount(amount)"
            >
                ¥{{ amount }}
            </div>
        </div>

        <!-- Custom Amount Input -->
        <div class="center-h">
            <app-input mode="number" :placeholder="$t('component>balance>coinDeposit.inputCustomAmount')" v-model="selectedAmount" class="mb-2"/>
        </div>
        <div class="tips">
            {{ $t('component>balance>coinDeposit.customAmountRange') }}
        </div>
        <!-- Confirm Button -->
        <div class="center-h" style="width: 60%;">
            <app-button @click="paymentMethodSelectionToggle" shaped>{{ $t('component>balance>coinDeposit.depositBtn') }}</app-button>
        </div>
    </div>

    <PaymentMethodSelection v-if="paymentMethodSelectionVisible" :user="userInfo" :balanceAdequate="balanceAdequate"></PaymentMethodSelection>
</div>
</template>


<script>
import PaymentMethodSelection from "../payment/payment-method-selection.vue";

export default {
    components: {PaymentMethodSelection},
    data() {
        return {
            selectedAmount: '',
            predefinedAmounts: [30, 50, 100, 200, 500],
            balance: '',
            paymentMethodSelectionVisible: false,
            balanceAdequate: false
        };
    },
    props: {userInfo: Object},
    methods: {
        close() {
            this.$parent.coinDepositToggle(false);
        },
        setAmount(amount) {
            this.selectedAmount = amount;
            this.balance = amount.toFixed(2); // Update balance to match selected amount
        },

        formSubmit(paymentMethod) {
            if(this.selectedAmount > 0) {
                const updatedBalance = parseFloat((this.selectedAmount + this.userInfo.balance).toFixed(2));
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.transaction.updateLookingCoin,
                    method: 'POST',
                    data: {
                        userId: this.userInfo.id,
                        amount: this.selectedAmount,
                        balanceAfter: updatedBalance,
                        currencyType: 1,
                        transactionType: 1,
                        description: "Purchase Looking Coins",
                        descriptionCn: "购买领客币",
                        paymentMethod: paymentMethod
                    },
                    success: (res) => {
                        if(res.data.code == 0) {
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        } else {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                        }
                        this.$parent.reload();
                        this.close();
                    },
                });
            } else {
                uni.showToast({title: this.$t('component>balance>coinDeposit.showToast.selectAmount'), icon: 'none'});
            }
        },

        paymentMethodSelectionToggle() {
            if(this.selectedAmount <= 0) {
                uni.showToast({title: this.$t('component>balance>coinDeposit.showToast.selectAmount'), icon: 'none'});
            }else{
                this.balanceAdequate = this.$common.balanceAdequateValidation(this.selectedAmount, this.userInfo.balance)
                this.paymentMethodSelectionVisible = !this.paymentMethodSelectionVisible
            }

        }
    },
};
</script>

<style scoped>
.charge-container {
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
    background-color: rgba(0, 0, 0, 0.5); /* Dimmed background for focus */
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

.charge-header {
    font-size: 34rpx;
    font-weight: bold;
    margin-bottom: 20rpx;
}

.charge-options {
    justify-content: space-between;
    margin-bottom: 30rpx;
}

.custom-charge {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30rpx;
}

.custom-charge input {
    flex: 1;
    padding: 20rpx;
    border-radius: 30rpx;
    border: 1px solid #ccc;
    margin-right: 10rpx;
    font-size: 30rpx;
}

.confirm-button {
    background-color: #f8be23;
    color: white;
    padding: 20rpx 0;
    border-radius: 30rpx;
    width: 100%;
    font-size: 32rpx;
}

.confirm-button:hover {
    background-color: #f8be23;
}


.custom-charge {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30rpx;
}

.custom-amount-input {
    flex: 1;
    padding: 20rpx;
    border-radius: 30rpx;
    border: 1px solid #ccc;
    margin-right: 10rpx;
    font-size: 30rpx;
}

.custom-charge-button {
    background-color: #f8be23;
    color: white;
    padding: 20rpx 0;
    border-radius: 30rpx;
    width: 100px;
    font-size: 16rpx;
}


.credit {
    font-size: 28rpx;
    color: #666;
}

.custom-charge {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30rpx;
}

.custom-amount-input {
    flex: 1;
    padding: 20rpx;
    border-radius: 30rpx;
    border: 1px solid #ccc;
    margin-right: 10rpx;
    font-size: 30rpx;
}

</style>
