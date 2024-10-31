<template>
<div class="page page-mono center" >
    <div class="page-mono-header">
        <app-title type="h1" bold>{{ $t('register.step4Title') }}</app-title>
        <p class="center-h">{{ $t('register.step4Desc') }}</p>
    </div>

    <div class="page-mono-input-wrapper">
        <input
            class="page-mono-input center-h"
            type="text"
            v-model="realName"
            :placeholder="$t('profile>realNameAuthentication.realName')"
        />
        <input
            class="page-mono-input center-h"
            type="text"
            v-model="idCardNumber"
            :placeholder="$t('profile>realNameAuthentication.idCard')"
        />
    </div>
    <app-button shaped size="very-large"  @click="authUserIdentity"  width="85vw">
        {{ $t('pub.button.confirm')  }}
    </app-button>
</div>
</template>

<script>
import $API from "../../../api/api";
import app from "../../../App.vue";

export default {
    name: "real-name-authentication",
    props: {
        user: Object,
        realName: '',
        idCardNumber: '',
    },
    methods: {
        authUserIdentity() {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            if(this.$common.isEmpty(this.realName)||this.$common.isEmpty(this.idCardNumber)) {
                uni.hideLoading();
                uni.showToast({
                    title: this.$t('pub.showToast.finishForm'),
                    icon: 'none'
                });
                return;
            }
            /*uni.request({
                url: getApp().globalData.data.requestUrl + $API.user.update,
                method: 'POST',
                data: {
                    id: this.user.id,
                    nickname: this.user.nickname,
                    gender: this.user.gender,
                    age: this.user.age,
                    avatar: this.user.avatar,
                },
                success: (res) => {
                    uni.hideLoading();
                    if(res.data.status === 200) {
                        uni.setStorageSync(app.globalData.data.userInfoKey, this.user);
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        this.showSubmitButton = false;
                        this.originalUser = {...this.user};
                    } else {
                        uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                    }
                },
            });*/
        },
    },
};
</script>

<style scoped>
.page-mono {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
}

.page-mono-header {
    width: 90vw;
    position: absolute;
    top: 16vh;
    text-align: center;
}

.page-mono-header app-title {
    margin-bottom: 10px;
}

.page-mono-header p {
    font-size: 18px;
    color: #666;
    margin-top: 68px;
    width: 70vw;
}

.page-mono-input {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 15px 90px;
    font-size: 16px;
    color: #333;
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 30px;
    width: 103%;
    text-align: center;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12);
    transition: box-shadow 0.3s ease, border-color 0.3s ease;
}

.page-mono-input:focus {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-color: #007bff;
    outline: none;
}

.page-mono-input-wrapper {
    display: flex;
    flex-direction: column;
    gap: 15px;
    width: 100%;
}
</style>