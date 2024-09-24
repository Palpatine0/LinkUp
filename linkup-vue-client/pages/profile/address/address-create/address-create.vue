<template>
<div class="page">
    <app-title type="h1" bold="true">{{ addressId ? '编辑地址' : '新增地址' }}</app-title>

    <!-- Consignee Name -->
    <app-title bold="true">收货人</app-title>
    <app-input
        mode="text"
        placeholder="请输入收货人姓名"
        col="12"
        class="mb-2"
        v-model="address.consignee"
    />

    <!-- Phone Number -->
    <app-title bold="true">联系电话</app-title>
    <app-input
        mode="text"
        placeholder="请输入联系电话"
        col="12"
        class="mb-2"
        v-model="address.phoneNumber"
    />

    <!-- Location -->
    <app-title bold="true">服务地点</app-title>
    <view class="app-input">
        <div v-if="address.address" @tap="authVerification">
            {{ address.addressName }}
        </div>
        <div v-else @tap="authVerification">
            请选择服务地点
        </div>
    </view>

    <!-- Detailed Address -->
    <app-title bold="true">详细地址</app-title>
    <app-input
        mode="text"
        placeholder="请输入详细地址"
        col="12"
        class="mb-2"
        v-model="address.detail"
    />

    <!-- Submit Button -->
    <div class="center_h">
        <div class="app-button" @click="formSubmit">保存地址</div>
    </div>
</div>
</template>

<script>
export default {
    data() {
        return {
            address: {
                id: null,
                userId: uni.getStorageSync('userId'),
                consignee: '',
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
                url: getApp().globalData.requestUrl + '/address/detail',
                method: 'POST',
                data: {id: addressId},
                success: (res) => {
                    if (res.data && res.data.address) {
                        this.address = res.data.address;
                    }
                },
                fail: (err) => {
                    uni.showToast({title: '加载失败', icon: 'none'});
                }
            });
        },

        // Location selection
        authVerification() {
            uni.getSetting({
                success: (res) => {
                    if (res.authSetting['scope.userLocation']) {
                        this.handleChooseLocation()
                    } else if (res.authSetting['scope.userLocation'] === undefined) {
                        this.handleOpenSetting()
                    } else {
                        this.handleOpenSetting()
                    }
                },
            })
        },
        handleChooseLocation() {
            uni.chooseLocation({
                latitude: uni.getStorageSync('latitudeFuzzy') || '',
                longitude: uni.getStorageSync('longitudeFuzzy') || '',
                success: (res) => {
                    uni.setStorageSync('currentLocation', res)
                    this.address.address = res.address
                    this.address.addressName = res.name
                    this.address.latitude = res.latitude
                    this.address.longitude = res.longitude
                },
                fail: function (err) {
                    console.log('取消按钮', err)
                }
            })
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
            const url = this.addressId ? getApp().globalData.requestUrl + '/address/update' : getApp().globalData.requestUrl + '/address/save';
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
                    uni.showToast({title: '地址保存成功', icon: 'none'});
                    uni.navigateTo({
                        url: '/pages/profile/address/address'
                    });
                },
                fail: (err) => {
                    uni.showToast({title: '保存失败', icon: 'none'});
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

.center_h {
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
