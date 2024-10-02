<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('profile>balance>transactionHistory.transactionHistory') }}</app-title>
        <img src="/static/common/create.svg" style="width: 28px; height: 28px;" @click="transactionInitiateRedirect"/>
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
            class="app-container"
            v-for="transaction in transactionList"
            :key="transaction.id"
            @click="transactionDetailRedirect(transaction.id)"
        >
            <div class="transaction-content">
                <div style="width: 100%;">
                    <div style="display: flex; align-items: center;">
                        <app-title bold="true" type="h3" style="width: 330px;">{{ transaction.title }}</app-title>
                    </div>
                    <div class="transaction-detail">
                        <div class="transaction-amount">
                            {{ $t('profile>balance>transactionHistory.amount') }}: {{ transaction.amount }}
                        </div>
                        <span style="font-size: 14px; color: gray;">{{ transaction.createdAt }}</span>
                        <div style="display:flex;justify-content: space-between;">
                            <span style="font-size: 14px; color: gray;">{{ transaction.identifier }}</span>
                            <div v-if="transaction.status==0" class="flex">
                                <span class="status-dot yellow-dot"></span>
                                <div class="status-text">{{ $t('profile>balance>transactionHistory.pending') }}</div>
                            </div>
                            <div v-if="transaction.status==1" class="flex">
                                <span class="status-dot green-dot"></span>
                                <div class="status-text">{{ $t('profile>balance>transactionHistory.completed') }}</div>
                            </div>
                            <div v-if="transaction.status==2" class="flex">
                                <span class="status-dot gray-dot"></span>
                                <div class="status-text">{{ $t('profile>balance>transactionHistory.failed') }}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="loading" style="color: gainsboro; margin-left: 10px;">{{ $t('pub.page.loading') }}</div>
        <div v-else-if="!hasMore" class="no-more-data-container-list">{{ $t('pub.page.noMoreData') }}</div>
    </scroll-view>
</div>
</template>

<script>
// import transactionDetail from './transaction-detail/transaction-detail.vue';

export default {
    data() {
        return {
            userProfileAvailable: false,
            transactionList: [],
            searchKeyword: '',
        };
    },
    onLoad() {
        this.resetPagination();
        this.getTransactionList();
    },
    computed: {
        /*transactionDetail() {
            return transactionDetail;
        },*/
    },
    methods: {
        // Fetch transaction list
        getTransactionList() {
            if (this.loading || !this.hasMore) return;

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

        // Redirects
        transactionInitiateRedirect() {
            uni.navigateTo({
                url: '/pages/profile/balance/deposit',
            });
        },
        /*transactionDetailRedirect(transactionId) {
            uni.navigateTo({
                url: '/pages/profile/transaction/transaction-detail?transactionId=' + transactionId,
            });
        },*/
    },
};
</script>

<style scoped>
.transaction-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.transaction-detail {
    margin-top: 5px;
}

.status-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-left: 10px;
    margin-top: 2px;
}

.status-text {
    position: relative;
    bottom: 4px;
    left: 5px;
    margin-right: 12px;
}

.yellow-dot {
    background-color: #feb327;
}

.green-dot {
    background-color: #27b459;
}

.gray-dot {
    background-color: #8c8c8c;
}

.transaction-amount {
    color: white;
    background-color: #007aff;
    border-radius: 5px;
    font-weight: bold;
    padding: 2px;
    width: 100px;
    font-size: 14px;
    margin-bottom: 4px;
}

</style>
