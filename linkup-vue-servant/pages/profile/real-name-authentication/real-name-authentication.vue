<template>
<div class="page-mono center" style="background-color: #f5f7fb">
    <div class="page-mono-header">
        <app-title type="h1" bold>{{ $t('profile>realNameAuthentication.verifyIdentity') }}</app-title>
        <p class="center-h">{{ $t('profile>realNameAuthentication.tips') }}</p>
    </div>

    <div class="page-mono-form">
        <input
            type="text"
            v-model="name"
            :placeholder="$t('profile>realNameAuthentication.realName')"
        />
        <input
            type="text"
            v-model="idCardNumber"
            :placeholder="$t('profile>realNameAuthentication.idCard')"
        />
    </div>
    <div class="fix-bottom">
        <app-button shaped size="very-large" @click="validateUserIdentity" width="85vw">
            {{ $t('pub.button.confirm') }}
        </app-button>
    </div>

</div>
</template>

<script>
import $API from "../../../api/api";
import app from "../../../App.vue";

export default {
    name: "real-name-authentication",
    props: {
        name: '',
        idCardNumber: '',
    },
    methods: {
        validateUserIdentity() {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            uni.request({
                url: getApp().globalData.data.requestUrl + $API.user.identityValidation,
                method: 'POST',
                data: {
                    id: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                    role: app.globalData.data.roleKey,
                    name: this.name,
                    idCardNumber: this.idCardNumber,
                },
                success: async(res) => {
                    uni.hideLoading();
                    if(res.data.status === 200) {
                        uni.showToast({title: this.$t('profile>realNameAuthentication.showToast.authSuccess'), icon: 'none'});
                        const newUserData = await this.$common.getUser(uni.getStorageSync(getApp().globalData.data.userInfoKey).id);
                        uni.removeStorageSync(app.globalData.data.userInfoKey);
                        uni.setStorageSync(getApp().globalData.data.userInfoKey, newUserData);
                        uni.setStorageSync(app.globalData.data.userVerificationKey, true);
                        uni.switchTab({
                            url: '/pages/profile/profile',
                        });
                    } else {
                        console.log(res.data)
                        if(res.data.message == "Match failed") {
                            uni.showToast({title: this.$t('profile>realNameAuthentication.showToast.matchFailed'), icon: 'none'});
                        } else if(res.data.message == "No record") {
                            uni.showToast({title: this.$t('profile>realNameAuthentication.showToast.noRecord'), icon: 'none'});
                        } else {
                            uni.showToast({title: this.$t('profile>realNameAuthentication.showToast.inputError'), icon: 'none'});
                        }
                    }
                },
                fail: () => {
                    uni.hideLoading();
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                }
            });
        },
    },
};
</script>

<style scoped>

</style>