<template>
<view class="popup-container">
    <view class="mask" @click="close"></view>
    <view class="popup-content">
        <view class="popup-header">{{ $t('component>address>addressSelector.selectorServiceAddress') }}</view>

        <!-- Address List -->
        <view v-for="address in addressList" :key="address.id" class="address-item" @click="selectAddress(address)">
            <view class="address-content">
                <view class="address-name">{{ address.addressName }}</view>
                <view class="address-detail">{{ address.phoneNumber }}</view>
            </view>
        </view>

        <!-- Create New Address Button -->
        <view class="create-address" @click="addressCreateRedirect">{{ $t('component>address>addressSelector.createAddress') }}</view>
    </view>

</view>
</template>

<script>

import app from "../../../App.vue";

export default {
    data() {
        return {
            addressList: [], // List of available addresses
            createAddressVisible: false, // Second pop-up visibility
        };
    },
    mounted() {
        this.getAddressList(); // Fetch address list on component mount
    },
    methods: {
        close() {
            this.$emit('close'); // Close the pop-up
        },
        selectAddress(address) {
            this.$emit('selectAddress', address);
        },
        getAddressList() {
            // Fetch addresses from the server
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.address.search,
                method: 'POST',
                data: {
                    userId: uni.getStorageSync(app.globalData.data.userInfoKey).id
                },
                success: (res) => {
                    this.addressList = res.data.list; // Populate address list
                },
                fail: (err) => {
                    console.log(err);
                }
            });
        },

        addressCreateRedirect() {
            uni.navigateTo({
                url: '/pages/profile/address/address-manage/address-manage',
            });
        },
    }
};
</script>

<style scoped>
.popup-container {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1000;
    display: flex;
    justify-content: center;
    align-items: center;
}

.popup-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 10px;
    width: 80%;
    max-height: 80%;
    overflow-y: auto;
    z-index: 10;
}

.popup-header {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 20px;
}

.address-item {
    padding: 10px;
    border-bottom: 1px solid #eee;
}

.create-address {
    margin-top: 20px;
    text-align: center;
    color: #007aff;
    cursor: pointer;
}
</style>
