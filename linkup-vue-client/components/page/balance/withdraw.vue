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
                @input="validateAmount"
            />
        </div>

        <div class="tips">
            {{ $t('component>balance>withdraw.minWithdrawAmount') }}
        </div>
        <div class="tips">
            {{ $t('component>balance>withdraw.hint') }}
        </div>

        <!-- Display Selected Amount -->
        <div class="no-more-data">
            <div class="credit">¥{{ withdrawAmount == null ? 0 : withdrawAmount.toFixed(2) }}</div>
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
            withdrawAmount: null,
            balance: this.user.balance || 0, // user's current balance
        };
    },
    props: {
        user: Object,
        method: Number,
        methodId: Number
    },
    methods: {
        close() {
            this.$parent.withdrawToggle(false);
        },
        validateAmount() {
            let amount = this.withdrawAmount.toString();
            amount = amount.replace(/[^0-9.]/g, '');

            if(amount.includes('.')) {
                const parts = amount.split('.');
                if(parts[1].length > 2) {
                    amount = parts[0] + '.' + parts[1].slice(0, 2);
                }
            }

            this.withdrawAmount = parseFloat(amount) || 0;
            if(this.withdrawAmount > this.balance) {
                this.withdrawAmount = this.balance;
            }
        },

        confirmWithdraw() {
            const today = new Date();
            const userlastWithdrawDate = new Date(this.user.lastWithdrawalDate);
            const sameDayWithdraw = userlastWithdrawDate.toDateString() === today.toDateString();

            if(sameDayWithdraw) {
                uni.showToast({title: this.$t('component>balance>withdraw.showToast.oneWithdrawPerDay'), icon: 'none'});
                return;
            }

            if(this.withdrawAmount <= 0) {
                uni.showToast({title: this.$t('component>balance>withdraw.showToast.invalidAmount'), icon: 'none'});
            } else if(this.withdrawAmount > this.user.balance) {
                uni.showToast({title: this.$t('component>balance>withdraw.showToast.insufficientBalance') + this.user.balance, icon: 'none'});
            } else if(this.withdrawAmount < 100) {
                uni.showToast({title: this.$t('component>balance>withdraw.showToast.minWithdrawAmount'), icon: 'none'});
            } else {
                const updatedBalance = parseFloat((this.balance - this.withdrawAmount).toFixed(2));
                // Update the user's  withdrawal date
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.withdrawalRequest.save,
                    method: 'POST',
                    data: {
                        userId: this.user.id,
                        amount: this.withdrawAmount,
                        method: this.method,
                        methodId: this.methodId,
                    },
                    success: (res) => {
                        if(res.data.code == 0) {
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                            this.$parent.getUser();
                            this.close();
                        } else {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                        }
                    },
                    fail: (err) => {
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    },
                });
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.user.update,
                    method: 'POST',
                    data: {
                        id: this.user.id,
                        balance: updatedBalance,
                        lastWithdrawalDate: today,
                    },
                    success: (res) => {
                        if(res.data.code == 0) {
                            uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                            this.$parent.getUser();
                            this.close();
                        } else {
                            uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                            console.log(res.data.message)
                        }
                    },
                    fail: (err) => {
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    },
                });
                // Log the transaction
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.transaction.save,
                    method: 'POST',
                    data: {
                        userId: this.user.id,
                        amount: -this.withdrawAmount,
                        balanceAfter: updatedBalance,
                        currencyType: 0,
                        transactionType: 0,
                        description: "Withdraw",
                        descriptionCn: "提现",
                    },
                    success: (res) => {
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

