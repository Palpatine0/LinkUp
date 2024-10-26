<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('profile>address.addressManage') }}</app-title>
        <img src="/static/common/create.svg" style="width: 28px; height: 28px;" @click="addressCreateRedirect"/>
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

    <!-- Addresses Container using app-container -->
    <scroll-view
        :scroll-top="0"
        scroll-y="true"
        style="height: 80vh"
        @scrolltolower="onReachBottom"
    >
        <div
            class="app-container"
            v-for="address in addressList"
            :key="address.id"
            @click="addressDetailRedirect(address.id)"
        >
            <div class="address-content">
                <div style="width: 100%;">
                    <div style="display: flex; align-items: center; justify-content: space-between;">
                        <app-title bold="true" type="h3" style="width: 330px;">{{ address.consignee }}</app-title>
                        <span class="phone-number">{{ address.phoneNumber }}</span>
                    </div>
                    <div class="address-info">
                        <div class="address-details">
                            {{ address.addressName }}
                        </div>
                        <div class="justify-SB">
                            <span style="font-size: 14px; color: gray;">{{ address.detail }}</span>
                            <img class="icon" src="/static/common/trash-can-solid.svg" @click.stop="deleteAddress(address.id)">
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="loading" class="loading-text">{{ $t('pub.page.loading') }}</div>
        <!-- No More Data Message -->
        <div v-else-if="!hasMore" class="no-more-data-container-list">{{ $t('pub.page.noMoreData') }}</div>
    </scroll-view>
</div>
</template>

<script>

export default {
    data() {
        return {
            addressList: [],
            searchKeyword: '',
        };
    },
    onShow() {
        this.reload()
    },
    methods: {
        reload(){
            this.resetPagination();
            this.getDataList();
        },
        buildApiParams() {
            let url = getApp().globalData.data.requestUrl + this.$API.address.search;
            let method = 'POST';
            let baseData = {
                userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                page: this.page,
                size: this.pageSize,
            };
            let data = {};

            if (this.searchKeyword && this.searchKeyword.trim() !== '') {
                data = {
                    ...baseData,
                    keyword: this.searchKeyword
                }
            } else {
                data = {
                    ...baseData,
                };
            }

            return {url, method, data};
        },
        onSearchInput() {
            this.resetPagination();
            this.getDataList();
        },
        getDataList() {
            if (this.loading || !this.hasMore || this.$common.isEmpty(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)) return;
            this.loading = true;
            const {url, method, data} = this.buildApiParams();
            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    const addresses = res.data.list;
                    if (this.page === 1) {
                        this.addressList = [];
                    }
                    if (addresses.length < this.pageSize) {
                        this.hasMore = false;
                    }
                    // Append new addresses to the list
                    this.addressList = this.addressList.concat(addresses);
                    // Process 'createdAt' fields
                    this.addressList.forEach((address) => {
                        address.createdAt = address.createdAt ? this.$common.stampToTime(address.createdAt) : '';
                    });
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },

        deleteAddress(aid,){
            uni.showModal({
                title: this.$t('pub.modal.title.confirm'),
                content: this.$t('pub.modal.content.confirm'),
                showCancel: true,
                confirmText: this.$t('pub.modal.button.confirm'),
                cancelText: this.$t('pub.modal.button.cancel'),
                success: (res) => {
                    if(res.confirm) {
                        uni.request({
                            url: getApp().globalData.data.requestUrl + this.$API.address.delete,
                            method: 'POST',
                            data: {
                                id: aid
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

        // Redirect to create address page
        addressCreateRedirect() {
            uni.navigateTo({
                url: '/pages/profile/address/address-manage/address-manage',
            });
        },

        // Redirect to address details (can be used for editing)
        addressDetailRedirect(addressId) {
            console.log("addressDetailRedirect(addressId) {addressDetailRedirect(addressId) {")
            uni.navigateTo({
                url: '/pages/profile/address/address-manage/address-manage?addressId=' + addressId,
            });
        },
    },
};
</script>

<style scoped>
.address-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.address-info {
    margin-top: 5px;
}

.address-details {
    color: #555;
}

.phone-number {
    font-size: 14px;
    color: gray;
    margin-left: 10px;
}

.no-more-data-container-list {
    text-align: center;
    color: gray;
    margin: 10px 0;
}
</style>
