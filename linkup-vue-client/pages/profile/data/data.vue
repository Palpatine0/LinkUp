<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('profile>data.ds') }}</app-title>
    </div>
</div>
</template>


<script>
import paginationMixin from '../../../utils/paginationMixin';
import app from "../../../App.vue";

export default {
    mixins: [paginationMixin],
    data() {
        return {
            userProfileAvailable: false,
            orderList: [],
            searchKeyword: '',
        };
    },
    onLoad() {
        const userInfo = uni.getStorageSync(app.globalData.data.userInfoKey)
        console.log("userInfo", userInfo)
        const userVerification = uni.getStorageSync(app.globalData.data.userVerificationKey)
        console.log("userVerification", userVerification)
        const sysInfo = uni.getStorageSync(app.globalData.data.systemInfoKey)
        console.log("sysInfo", sysInfo)
        const language = uni.getStorageSync("language")
        console.log("language", language)
    },
};
</script>

