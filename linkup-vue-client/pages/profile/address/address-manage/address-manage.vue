<template>
<div class="page">
    <app-title v-if="!addressId" type="h1" bold="true">
        {{ $t('profile>address>addressManage.createAddress') }}
    </app-title>
    <app-title v-else type="h1" bold="true">
        {{ $t('profile>address>addressManage.editAddress') }}
    </app-title>

    <!-- Consignee Name -->
    <app-title bold="true">{{ $t('profile>address>addressManage.consignee') }}</app-title>
    <app-input
        mode="text"
        :placeholder="$t('profile>address>addressManage.consigneePlaceholder')"
        col="12"
        class="mb-2"
        v-model="address.consignee"
    />

    <!-- Phone Number -->
    <app-title bold="true">{{ $t('profile>address>addressManage.phoneNumber') }}</app-title>
    <app-input
        mode="text"
        :placeholder="$t('profile>address>addressManage.phonePlaceholder')"
        col="12"
        class="mb-2"
        v-model="address.phoneNumber"
    />

    <!-- Location -->
    <app-title bold="true">{{ $t('profile>address>addressManage.serviceLocation') }}</app-title>
    <div class="app-input" style="margin-bottom: 30px">
        <div v-if="address.address" @tap="authVerification">
            {{ address.addressName }}
        </div>
        <div v-else @tap="authVerification">
            {{$t('profile>address>addressManage.selectLocation')}}
        </div>
    </div>

    <!-- Detailed Address -->
    <app-title bold="true">{{ $t('profile>address>addressManage.detailedAddress') }}</app-title>
    <app-input
        mode="text"
        :placeholder="$t('profile>address>addressManage.detailPlaceholder')"
        col="12"
        class="mb-2"
        v-model="address.detail"
    />

    <!-- Submit Button -->
    <div class="center-h">
        <span v-if="!addressId">
            <div class="app-button" @click="formSubmit">{{ $t('profile>address>addressManage.saveBtn') }}</div>
        </span>
        <span v-else>
            <div class="app-button" @click="formSubmit">{{ $t('profile>address>addressManage.updateBtn') }}</div>
        </span>
    </div>
</div>
</template>

<script>
import app from "../../../../App.vue";

export default {
    data() {
        return {
            address: {
                id: null,
                userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                consignee: uni.getStorageSync(getApp().globalData.data.userInfoKey).nickname,
                phoneNumber: '',
                address: '',
                addressName: '',
                detail: '',
                latitude: null,
                longitude: null,
            },
            addressId: null
        };
    },
    onLoad(options) {
        if (options.addressId) {
            this.addressId = options.addressId;
            this.getAddressDetail(this.addressId);
        }
    },
    methods: {
        // Load address details for editing
        getAddressDetail(addressId) {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.address.search,
                method: 'POST',
                data: {id: addressId},
                success: (res) => {
                    this.address = res.data.list[0];
                },
                fail: (err) => {
                    uni.showToast({title: this.$t('pub.showToast.loadingFail'), icon: 'none'});
                }
            });
        },

        // Location selection
        authVerification() {
            uni.getSetting({
                success: (res) => {
                    if (res.authSetting['scope.userFuzzyLocation']) {
                        this.handleChooseLocation()
                    } else if (res.authSetting['scope.userFuzzyLocation'] === undefined) {
                        this.handleOpenSetting()
                    } else {
                        this.handleOpenSetting()
                    }
                },
            })
        },
        handleChooseLocation() {
            uni.authorize({
                scope: 'scope.userLocation',
                success: () => { // Change to arrow function
                    uni.chooseLocation({
                        latitude: uni.getStorageSync(app.globalData.data.userInfoKey).latitudeFuzzy || '',
                        longitude: uni.getStorageSync(app.globalData.data.userInfoKey).longitudeFuzzy || '',
                        success: (res) => { // Change to arrow function
                            console.log("res");
                            console.log(res);
                            uni.setStorageSync('currentLocation', res);
                            console.log("this.address");
                            console.log(this.address); // Now `this` should refer to the Vue component
                            this.address.address = res.address;
                            this.address.addressName = res.name;
                            this.address.latitude = res.latitude;
                            this.address.longitude = res.longitude;
                        },
                        fail: (err) => {
                            console.log("uni.chooseLocation fail");
                            console.log(err);
                        }
                    });
                },
                fail: (err) => {
                    console.log("uni.authorize err");
                    console.log(err);
                }
            });
        },

        handleOpenSetting() {
            wx.openSetting({
                success: (res) => {
                    if (res.authSetting["scope.userLocation"]) {
                        this.handleChooseLocation()
                    }
                }
            })
        },

        // Submit address form
        formSubmit() {
            const url = this.addressId ? getApp().globalData.data.requestUrl + this.$API.address.update : getApp().globalData.data.requestUrl + this.$API.address.save;
            const method = this.addressId ? 'POST' : 'POST';

            const addressData = {
                ...this.address,
                id: this.addressId ? this.addressId : undefined
            };

            uni.request({
                url: url,
                method: method,
                data: addressData,
                success: (res) => {
                    uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                    uni.navigateTo({
                        url: '/pages/profile/address/address'
                    });
                },
                fail: (err) => {
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
        }
    }
};
</script>

<style scoped>
.app-input {
    margin-bottom: 20px;
}

.center-h {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.app-button {
    background-color: #007aff;
    color: white;
    padding: 10px 20px;
    border-radius: 5px;
    font-weight: bold;
    cursor: pointer;
}
</style>
