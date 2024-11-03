<template>
<div v-if="user.isIdentityVerified==0" class="page-mono center" style="background-color: #f5f7fb">
    <div class="page-mono-header">
        <app-title type="h1" bold>{{ $t('profile>realNameVerification.verifyIdentity') }}</app-title>
        <p class="center-h">{{ $t('profile>realNameVerification.tips') }}</p>
    </div>

    <div class="page-mono-form">
        <input
            type="text"
            v-model="name"
            :placeholder="$t('profile>realNameVerification.realName')"
        />
        <input
            type="text"
            v-model="idCardNumber"
            :placeholder="$t('profile>realNameVerification.idCard')"
        />
    </div>
    <div class="fix-bottom">
        <app-button shaped size="very-large" @click="validateUserIdentity" width="85vw">
            {{ $t('pub.button.confirm') }}
        </app-button>
    </div>
</div>
<div v-else class="page" style="background-color: #f5f7fb">
    <div class="background-icon">
        <img style="margin-top: 20px" src="/static/page/order/order-detail/check-double.svg">
    </div>
    <div class="tips-text mb-4" style="text-align: center">
        <div>{{ $t('profile>realNameVerification.verified') }}</div>
    </div>
    <app-container color="#FFF">
        <div class="link-item justify-SB">
            <span class="link-text bold">{{ $t('profile>realNameVerification.idCardName') }}</span>
            <span class="link-text ">{{ user.idCardName }}</span>
        </div>
        <div class="separator"></div>
        <div class="link-item justify-SB">
            <span class="link-text bold">{{ $t('profile>realNameVerification.idCardNumber') }}</span>
            <span class="link-text">{{ user.idCardNumber }}</span>
        </div>
    </app-container>
</div>
</template>

<script>
import $API from "../../../api/api";
import app from "../../../App.vue";

export default {
    name: "real-name-verification",
    props: {
        name: '',
        idCardNumber: '',
    },
    data() {
        return {
            user: ''
        }
    },
    async onLoad() {
        this.user = await this.$common.getUser(uni.getStorageSync(getApp().globalData.data.userInfoKey).id)
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
                        uni.showToast({title: this.$t('profile>realNameVerification.showToast.authSuccess'), icon: 'none'});
                        const newUserData = await this.$common.getUser(uni.getStorageSync(getApp().globalData.data.userInfoKey).id);
                        uni.removeStorageSync(app.globalData.data.userInfoKey);
                        uni.setStorageSync(getApp().globalData.data.userInfoKey, newUserData);
                        uni.setStorageSync(app.globalData.data.userVerificationKey, true);
                        uni.switchTab({
                            url: '/pages/profile/profile',
                        });
                    } else {
                        if(res.data.message == "Match failed") {
                            uni.showToast({title: this.$t('profile>realNameVerification.showToast.matchFailed'), icon: 'none'});
                        } else if(res.data.message == "No record") {
                            uni.showToast({title: this.$t('profile>realNameVerification.showToast.noRecord'), icon: 'none'});
                        } else {
                            uni.showToast({title: this.$t('profile>realNameVerification.showToast.inputError'), icon: 'none'});
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
.tips-text {
    width: 100%;
    font-size: 36px;
    font-weight: bold;
    line-height: 40px;
    color: #192C77;
}

.link-item {
    padding: 10px 0;
}

.link-text {
    position: relative;
}

.separator {
    height: 1px;
    background-color: #ccc;
    margin-left: 10px;
    width: 80vw;
}
</style>