<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('profile>balance>transactionHistory.transactionHistory') }}</app-title>
    </div>

    <!-- Search input -->
    <app-input
        mode="text"
        :placeholder="$t('pub.page.search')"
        col="12"
        class="mb-2"
        v-model="searchKeyword"
        @input="onSearchInput"
    />

    <!-- Transactions Container using app-container -->
    <scroll-view
        :scroll-top="0"
        scroll-y="true"
        style="height: 80vh"
        @scrolltolower="onReachBottom"
    >
        <div
            class="app-container transaction-item"
            v-for="transaction in transactionList"
            :key="transaction.id"
            @click="transactionDetailRedirect(transaction.id)"
        >
            <div class="transaction-icon">
                <!-- Display different icons based on the transaction type -->
                <img v-if="transaction.transactionType === 1" src="/static/page/balance/hand-holding-circle-dollar.svg" alt="Received"/>
                <img v-else src="/static/page/balance/circle-dollar-to-slot.svg" alt="Deducted"/>
            </div>
            <div class="transaction-details">
                <div class="transaction-title">
                    <span>{{ transaction.transactionType === 1 ? income : expanse }}</span>
                </div>
                <div class="transaction-detail">
                    <span>{{ language != "en" ? transaction.descriptionCn : transaction.description}}</span>
                </div>
            </div>
            <div style="text-align: right">
                <div class="transaction-amount" :class="{'positive': transaction.transactionType === 1, 'negative': transaction.transactionType === 0}">
                    {{ transaction.transactionType === 1 ? '' : '' }}{{ transaction.amount.toFixed(2) }}
                </div>
                <div class="transaction-datetime">
                    <span>{{ common.timeToStampRecord(transaction.createdAt) }}</span>
                </div>
            </div>
        </div>
        <div v-if="loading" style="color: gainsboro; margin-left: 10px;">{{ $t('pub.page.loading') }}</div>
        <div v-else-if="!hasMore" class="no-more-data-container-list">{{ $t('pub.page.noMoreData') }}</div>
    </scroll-view>
</div>
</template>

<script>
import common from "../../../../utils/common";

export default {
    computed: {
        common() {
            return common
        }
    },
    data() {
        return {
            language: uni.getStorageSync("language"),
            userProfileAvailable: false,
            transactionList: [],
            searchKeyword: '',
            income: this.$t('profile>balance>transactionHistory.income'),
            expanse: this.$t('profile>balance>transactionHistory.expanse')
        };
    },
    onLoad() {
        this.resetPagination();
        this.getTransactionList();
    },
    methods: {
        // Fetch transaction list
        getTransactionList() {
            if (this.loading || !this.hasMore||this.$common.isEmpty(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)) return;

            this.loading = true;

            const {url, method, data} = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    const transactions = res.data.list;
                    if (this.page === 1) {
                        this.transactionList = [];
                    }
                    if (transactions.length < this.size) {
                        this.hasMore = false;
                    }
                    // Append new transactions to the list
                    this.transactionList = this.transactionList.concat(transactions);
                    // Process 'createdAt' fields
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

        // Build API parameters based on search keyword
        buildApiParams() {
            let url = '';
            let method = 'POST';
            let data = {};

            if (this.searchKeyword && this.searchKeyword.trim() !== '') {
                // Use the search endpoint
                url = getApp().globalData.data.requestUrl + this.$API.transaction.search;
                data = {
                    userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                    keyword: this.searchKeyword,
                    page: this.page,
                    size: this.size,
                };
            } else {
                // Use the get-all-by-user-id endpoint
                url = getApp().globalData.data.requestUrl + this.$API.transaction.search;
                method = 'POST';
                data = {
                    userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                    page: this.page,
                    size: this.size,
                };
            }

            return {url, method, data};
        },

        // Handle search input changes
        onSearchInput() {
            this.resetPagination();
            this.getTransactionList();
        },
    },
};
</script>

<style scoped>
.transaction-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 10px 0;
    padding: 10px;
    background-color: white;
    border-radius: 10px;
}

.transaction-icon{
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
    width: 32px;
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
