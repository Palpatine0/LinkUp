<template>
<view class="charge-container">
    <view class="mask" @click="close()"></view>
    <view class="widget-center charge-anim">
        <view class="charge-header">{{ $t('component>balance>deposit.depositTitle') }}</view>
        <!-- Predefined Amount Buttons -->
        <view class="charge-options">
            <div class="app-button-outlined" @click="setAmount(100)">
                ¥100
            </div>
            <div class="app-button-outlined" @click="setAmount(500)">
                ¥500
            </div>
            <div class="app-button-outlined" @click="setAmount(1000)">
                ¥1000
            </div>
            <div class="app-button-outlined" @click="setAmount(3000)">
                ¥3000
            </div>
            <div class="app-button-outlined" @click="setAmount(5000)">
                ¥5000
            </div>
            <div class="app-button-outlined" @click="setAmount(10000)">
                ¥10000
            </div>
        </view>

        <!-- Display Selected Amount -->
        <view class="no-more-data">
            <div class="credit">¥{{ selectedAmount.toFixed(2) }}</div>
        </view>

        <!-- Confirm Button -->
        <div class="center-h" style="width: 60%;">
            <app-button @click="confirmCharge" shaped>{{ $t('component>balance>deposit.depositBtn') }}</app-button>
        </div>
    </view>
</view>
</template>

<script>
export default {
    data() {
        return {
            selectedAmount: 0,
            balance: '',
        };
    },
    props: {userInfo: Object},
    methods: {
        close() {
            this.$parent.depositToggle(false);
        },
        setAmount(amount) {
            this.selectedAmount = amount;
            this.balance = amount.toFixed(2); // Update balance to match selected amount
        },
        validateAmount() {
            // Limit input to two decimal places
            const amount = this.balance;
            if (amount.includes('.')) {
                const parts = amount.split('.');
                if (parts[1].length > 2) {
                    this.balance = parts[0] + '.' + parts[1].slice(0, 2);
                }
            }
            // Update selectedAmount immediately based on the balance
            const amountValue = parseFloat(this.balance);
            if (!isNaN(amountValue) && amountValue > 0) {
                this.selectedAmount = amountValue;
            } else {
                this.selectedAmount = 0;
            }
        },
        setCustomAmount() {
            // Ensure balance is correctly converted to a number with two decimals
            const amount = parseFloat(this.balance).toFixed(2);
            if (!isNaN(amount) && amount > 0) {
                this.selectedAmount = parseFloat(amount);
                console.log("Custom Amount Set: ", this.selectedAmount);
            } else {
                this.$message.error("请输入有效的金额");
                console.log("Invalid Amount: ", this.balance);
            }
        },
        confirmCharge() {
            console.log("Selected Amount: ", this.selectedAmount);
            if (this.selectedAmount > 0) {
                const updatedBalance = parseFloat((this.selectedAmount + this.userInfo.balance).toFixed(2));
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.user.update,
                    method: 'POST',
                    data: {
                        id: this.userInfo.id,
                        balance: updatedBalance
                    },
                    success: (res) => {
                        console.log(res);
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        this.$parent.getUser();
                        this.close();
                    },
                    fail: (err) => {
                        console.log(err);
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    }
                });
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.transaction.save,
                    method: 'POST',
                    data: {
                        userId: this.userInfo.id,
                        amount: this.selectedAmount,
                        balanceAfter: updatedBalance,
                        transactionType: 1,
                        description: "Deposit",
                        descriptionCn: "存款",
                    },
                    success: (res) => {
                    },
                });
            } else {
                uni.showToast({title: this.$t('component>balance>deposit.showToast.selectAmount'), icon: 'none'});
            }
        },
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

.charge-button {
    background-color: #812740;
    color: white;
    padding: 20rpx 0;
    border-radius: 30rpx;
    flex: 1;
    margin: 0 10rpx;
    font-size: 32rpx;
}


.charge-button:hover {
    background-color: #812740;
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

.no-more-data {
    margin: 20px 0;
}

.credit {
    font-size: 28rpx;
    color: #666;
}
</style>