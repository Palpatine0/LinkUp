<template>
<div class="withdraw-container">
    <div class="mask" @click="close()"></div>
    <div class="widget-center withdraw-anim">
        <div class="withdraw-header">{{ $t('component>balance>withdraw.withdrawTitle') }}</div>

        <!-- Input for Custom Amount -->
        <div class="custom-withdraw">
            <app-input
                mode="text"
                col="12"
                class="mb-2"
                :placeholder="$t('component>balance>withdraw.placeholder')"
                v-model="withdrawAmount"
            />
        </div>
        <!-- Confirm Button -->
        <div class="center-h" style="width: 60%;">
            <app-button @click="confirmWithdraw" shaped>{{ $t('component>balance>withdraw.withdrawBtn') }}</app-button>
        </div>
    </div>
</div>
</template>

<script>
export default {
    data() {
        return {
            withdrawAmount: this.userInfo.lookingCoins || '',
            lookingCoinsBalance: this.userInfo.lookingCoins || 0,
        };
    },
    props: {userInfo: Object},
    methods: {
        close() {
            this.$parent.coinWithdrawToggle(false);
        },

        confirmWithdraw() {
            if (this.withdrawAmount <= 0 || this.withdrawAmount > this.lookingCoinsBalance) {
                uni.showToast({title: this.$t('component>balance>withdraw.showToast.invalidAmount'), icon: 'none'});
            } else {
                const updatedBalance = parseFloat((this.lookingCoinsBalance - this.withdrawAmount).toFixed(2));
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.transaction.updateLookingCoin,
                    method: 'POST',
                    data: {
                        userId: this.userInfo.id,
                        amount: this.withdrawAmount,
                        balanceAfter: updatedBalance,
                        currencyType: 1,
                        transactionType: 0,
                        description: "Withdraw Looking Coins",
                        descriptionCn: "提现领克币",
                    },
                    success: (res) => {
                        if(res.data.code==0){
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        }else {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                        }
                        this.$parent.reload();
                        this.close();
                    },
                });
            }
        },
    },
};
</script>


<style scoped>
.withdraw-container {
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

.withdraw-header {
    font-size: 34rpx;
    font-weight: bold;
    margin-bottom: 20rpx;
}

.custom-withdraw input {
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
</style>

