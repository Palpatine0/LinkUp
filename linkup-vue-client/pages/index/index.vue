<template>
<div>
    <div class="welcome">
        <text>欢迎来到</text>
        <text>{{ shopName }}</text>
    </div>

    <div v-if="!userProfileAvailable" class="auth-wrapper">
        <div>
            <button open-type="getUserInfo" @getuserinfo="getUserInfo" style="border-radius: 50px">授权个人信息</button>
        </div>
    </div>

    <div v-else>
        <div v-if="tableId==null" class="headcount-select">
            <div class="tips">请选择桌号</div>
            <div class="scroll-text scroll">
                <scroll-view scroll-x="true" class="scroll-view_H" :enable-flex="true">
                    <div class="table-block">
                        <block v-for="(item,index) in tableIdArr" :key="index">
                            <div @click="tableIdSelect(index,item)" :class="{activetext: index === tableIdTmp}">{{ item }}</div>
                        </block>
                    </div>
                </scroll-view>
            </div>
            <div class="confirm-btn" :class="[tableIdTmp > -1 ? 'confirm-btn-available' : '']" @click="tableIdConfirm()">确认</div>
        </div>
        <div v-else class="headcount-select">
            <div class="tips">请选择就餐人数</div>
            <div class="scroll-text">桌号：{{ tableId }}</div>
            <div class="scroll-text scroll">
                <scroll-view scroll-x="true" class="scroll-view_H" :enable-flex="true">
                    <div class="table-block">
                        <block v-for="(item,index) in headcountArr" :key="index">
                            <div @click="headcountSelect(index,item)" :class="{activetext: index === headcount}">{{ item }}</div>
                        </block>
                    </div>
                </scroll-view>
            </div>
            <div class="confirm-btn" :class="[headcount > -1 ? 'confirm-btn-available' : '']" @click="menuRedirect()">开始点餐</div>
        </div>
    </div>

</div>
</template>

<script>

import common from "../../utils/common";

export default {
    data() {
        return {
            shopName: "Taste Dash",

            headcountArr: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
            headcount: -1,
            tableIdArr: [1, 2, 3, 4],
            tableId: null,
            tableIdTmp: -1,

            userProfileAvailable: false
        }
    },
    methods: {
        tableIdSelect(index, item) {
            this.tableIdTmp = index
        },
        tableIdConfirm(index, item) {
            this.tableId = this.tableIdTmp + 1
            uni.setStorageSync('tableId', this.tableId)
        },

        headcountSelect(index, item) {
            this.headcount = index
            uni.setStorageSync('headcount', item)
        },
        menuRedirect() {
            if (this.headcount <= -1) {
                return false
            }
            uni.navigateTo({
                url: '/pages/menu/menu'
            })
        },

        getUserInfo(e) {
            var openid = "";
            console.log("getUserInfo")
            console.log(e)
            uni.login({
                provider: 'weixin',
                success: (loginRes) => {
                    const {code} = loginRes;
                    uni.request({
                        url: getApp().globalData.requestUrl + '/user/saveUserAuthInfo',
                        method: 'POST',
                        data: {
                            code: code
                        },
                        success: (res) => {
                            const dataSet = res.data.userInfo;
                            if (dataSet.openid == null) {
                                uni.showToast({title: '授权失败', icon: 'none'});
                            } else {
                                uni.setStorageSync('openid', dataSet.openid);
                                uni.setStorageSync('sessionKey', dataSet.openid.session_key);
                                uni.setStorageSync('unionid', dataSet.unionid);
                                uni.setStorageSync('uid', dataSet.id);
                                uni.getUserInfo({
                                    success: function (res) {
                                        var userInfo = res.userInfo
                                        var nickname = userInfo.nickName
                                        var avatar = userInfo.avatarUrl
                                        uni.request({
                                            url: getApp().globalData.requestUrl + '/user/saveUserInfo',
                                            method: 'POST',
                                            data: {
                                                openid: dataSet.openid,
                                                nickname: nickname,
                                                avatar: avatar
                                            },
                                            success: (res) => {
                                                uni.setStorageSync('nickname', nickname);
                                                uni.setStorageSync('avatar', avatar);
                                                const storageInfo = uni.getStorageInfoSync();
                                                console.log("storageInfo")
                                                console.log(storageInfo)
                                                this.hasUserInfo = true
                                            },
                                        });
                                    }
                                })
                                this.userProfileAvailable = true
                                uni.showToast({title: '授权成功', icon: 'none'});
                            }
                        },
                        fail: () => {
                            uni.showToast({title: '请求失败', icon: 'none'});
                        }
                    });
                },
                fail: (err) => {
                    uni.showToast({title: '登录失败', icon: 'none'});
                }
            });

        },
        checkUserInfo() {
            console.log("M checkUserInfo")
            const openid = uni.getStorageSync('openid');
            console.log("openid")
            console.log(openid)
            if (common.isEmpty(openid)) {
                this.userProfileAvailable = false
                uni.showModal({
                    title: '授权',
                    content: '请授权您的个人信息以使用完整服务',
                    showCancel: true,
                    confirmText: '授权',
                    success: (res) => {
                        if (res.confirm) {
                            this.getUserInfo();
                        }
                    }
                });
            } else {
                this.userProfileAvailable = true
            }
        }
    },
    onLoad(e) {
        this.tableId = e.number
        uni.setStorageSync('tableId', e.number)
        const storageInfoSync = uni.getStorageInfoSync();
        console.log("storageInfoSync")
        console.log(storageInfoSync)
        this.checkUserInfo()
    },
}
</script>

<style>
page {
    background-image: url('https://i.imghippo.com/files/0bNg21725139568.jpg');
    background-attachment: fixed;
    background-repeat: no-repeat;
    background-size: cover;
}

.welcome {
    color: #FFFFFF;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 270rpx;
}

.welcome text {
    display: block;
    font-size: 50rpx;
}

.welcome text:nth-child(1) {
    font-family: monospace;
    padding-bottom: 20rpx;
}

.auth-wrapper {
    position: fixed;
    left: 30rpx;
    right: 30rpx;
    bottom: 100rpx;
    height: 650rpx;
    border-radius: 15rpx;
}

.headcount-select {
    background-color: #FFFFFF;
    position: fixed;
    left: 30rpx;
    right: 30rpx;
    bottom: 100rpx;
    height: 550rpx;
    border-radius: 15rpx;
}

.tips {
    height: 80rpx;
    font-size: 35rpx;
    font-weight: bold;
    line-height: 80rpx;
    padding-left: 20rpx;
}

.scroll-text {
    font-size: 30rpx;
    padding-left: 20rpx;
}

.scroll {
    margin-top: 100rpx;
}

.scroll-view_H {
    white-space: nowrap;
    width: 100%;
    height: 75rpx;
}

.table-block {
    display: flex;
    align-items: center;
}

.table-block view {
    height: 75rpx;
    line-height: 75rpx;
    text-align: center;
    background-color: #f7f8f9;
    margin-right: 25rpx;
    padding: 0 70rpx;
    border-radius: 15rpx;
}

.confirm-btn {
    height: 90rpx;
    line-height: 90rpx;
    background-color: #fdf4d7;
    color: #c2c2c2;
    text-align: center;
    margin: 130rpx 20rpx 0 20rpx;
    border-radius: 15rpx;
}

.activetext {
    background-color: #f9dd89 !important;
}

.confirm-btn-available {
    color: #000000 !important;
    background: #f8be23 !important;
}
</style>
