<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('profile>balance>transactionHistory.transactionHistory') }}</app-title>
    </div>

    <!-- Search input -->
    <app-input mode="text" :placeholder="$t('pub.page.search')" col="12" class="mb-2" v-model="searchKeyword" @input="onSearchInput"/>

    <div class="currency-type-selection">
        <button :class="{ active: currencyType === 0 }" @click="setCurrencyType(0)">
            {{ $t('profile>balance>transactionHistory.balance') }}
        </button>
        <button :class="{ active: currencyType === 1 }" @click="setCurrencyType(1)">
            {{ $t('profile>balance>transactionHistory.lookingCoin') }}
        </button>
    </div>

    <!-- Transactions Container using app-container -->
    <scroll-view
        :scroll-top="0"
        scroll-y="true"
        style="height: 80vh"
        @scrolltoupper="reload"
        @scrolltolower="onReachBottom"
    >
        <div
            class="app-container transaction-item"
            v-for="transaction in transactionList"
            :key="transaction.id"
            @click="transactionDetailRedirect(transaction.id)"
        >
            <div class="transaction-icon">
                <img v-if="transaction.transactionType === 1" :src="app.globalData.data.ossIconRequestUrl+'/page/profile/balance/hand-holding-circle-dollar.svg'" alt="Received"/>
                <img v-else :src="app.globalData.data.ossIconRequestUrl+'/page/profile/balance/circle-dollar-to-slot.svg'" alt="Deducted"/>
            </div>
            <div class="transaction-details">
                <div class="transaction-title">
                    <span>{{ transaction.transactionType === 1 ? income : expanse }}</span>
                </div>
                <div class="transaction-detail">
                    <span>
                        {{ language != "zh-Hans" ? transaction.description : transaction.descriptionCn }}
                    </span>
                </div>
            </div>
            <div style="text-align: right">
                <div class="transaction-amount" :class="{ 'positive': transaction.transactionType === 1, 'negative': transaction.transactionType === 0 }">
                    {{ transaction.amount.toFixed(2) }}
                </div>
                <div class="transaction-datetime">
                    <span>{{ $common.timeToStampRecord(transaction.createdAt) }}</span>
                </div>
            </div>
        </div>
        <div v-if="loading" class="loading-text">{{ $t('pub.page.loading') }}</div>
        <div v-else-if="!hasMore" class="no-more-data-container-list">{{ $t('pub.page.noMoreData') }}</div>
    </scroll-view>
</div>
</template>

<script>
import common from "../../../../utils/common";
import app from "../../../../App.vue";
import $common from "../../../../utils/common";

export default {
    computed: {
        $common() {
            return $common
        },
        app() {
            return app
        }
    },
    data() {
        return {
            currencyType: 0,
            userProfileAvailable: false,
            transactionList: [],
            searchKeyword: '',
            income: this.$t('profile>balance>transactionHistory.income'),
            expanse: this.$t('profile>balance>transactionHistory.expanse'),
        };
    },
    onLoad() {
        this.reload();
    },
    methods: {
        reload(){
            this.resetPagination();
            this.getDataList();
        },
        setCurrencyType(type) {
            this.currencyType = type;
            this.resetPagination();
            this.getDataList();
        },
        buildApiParams() {
            let url = getApp().globalData.data.requestUrl + this.$API.transaction.search;
            let method = 'POST';
            let baseData = {
                userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                currencyType: this.currencyType,
                page: this.page,
                size: this.pageSize,
            };
            let data = {};

            if (this.searchKeyword && this.searchKeyword.trim() !== '') {
                data = {
                    ...baseData,
                    keyword: this.searchKeyword,
                };
            } else {
                data = {
                    ...baseData,
                };
            }

            return { url, method, data };
        },
        onSearchInput() {
            this.resetPagination();
            this.getDataList();
        },
        getDataList() {
            if (this.loading || !this.hasMore || this.$common.isEmpty(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)) return;
            this.loading = true;
            const { url, method, data } = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: async (res) => {
                    const transactions = res.data.list;
                    if (this.page === 1) {
                        this.transactionList = [];
                    }
                    if (transactions.length < this.pageSize) {
                        this.hasMore = false;
                    }
                    this.transactionList = this.transactionList.concat(transactions);

                    this.transactionList.forEach((transaction) => {
                        transaction.createdAt = transaction.createdAt ? this.$common.stampToTime(transaction.createdAt) : '';
                    });

                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },
    }
};
</script>

<style scoped>
.currency-type-selection {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    background-color: #0A2342;
    border-radius: 50px;
    padding: 4px;
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
    background-color: #2196f338;
    width: 40px;
    height: 40px;
    border-radius: 20px;
    justify-content: center;
    display: flex;
    align-items: center;
}

.transaction-icon img {
    width: 40px;
    height: 25px;
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

.transaction-datetime {
    font-size: 14px;
    color: #afb3cc;
}

.transaction-amount {
    font-size: 16px;
    font-weight: bold;
}

.transaction-amount.positive {
    color: #34bc68;
}

.transaction-amount.negative {
    color: #1f2758;
}
</style>
