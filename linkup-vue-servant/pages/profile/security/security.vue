<template>
<div class="page">
    <app-container color="#f3f2f6">
        <div class="profile-header center-h mb-2">
            <div class="center-h">
                <img :src="user.avatar" alt="Profile Photo" class="profile-photo" @click="mediaSelector()"/>
            </div>
        </div>
    </app-container>
</div>
</template>

<script>

export default {
    data() {
        return {
            user: {},
            realNameAuthenticationVisible: false
        };
    },
    onLoad() {
        this.user = uni.getStorageSync(app.globalData.data.userInfoKey);
    },
    methods: {
        realNameAuthenticationToggle() {
            this.realNameAuthenticationVisible=!this.realNameAuthenticationVisible
        }
    }
};
</script>

<style scoped>
.profile-header {
    text-align: center;
    justify-content: space-between;
}

.profile-photo {
    width: 180px;
    height: 180px;
    border-radius: 50%;
}

.button-register-text {
    font-size: 16px;
}

.disabled {
    pointer-events: none;
    opacity: 0.5;
    color: grey;
}
</style>
