<template>
<div class="page">
    <app-title type="h1" bold="true">{{ $t('home.nearby') }}</app-title>

    <!-- Category Filters (Optional, can be expanded) -->
    <div class="app-container service-info" style="background: url('https://i.imghippo.com/files/gHQ3o1727185530.jpg') no-repeat center center;background-size: 135%;" @click="serviceDetailRedirect(1)">
        <div class="gradient-overlay" style="border-bottom-right-radius: 15px;border-bottom-left-radius: 15px"></div>
        <div class="service-info-text">
            {{ $t('home.tourGuideServices') }}
        </div>
    </div>
    <!-- Scroll View for User List -->
    <user-list
        :userList="userList"
        height="60vh"
        @user-click="userDetailRedirect"
    />
</div>
</template>

<script>

import app from "../../App.vue";
import common from "../../utils/common";

export default {
    name: "home",
    data() {
        return {
            // ** page vars ** //
            page: 1,
            size: 20,
            hasMore: true,
            loading: false,
            // ** /page vars ** //

            user: {},
            userList: [],
        };
    },
    onLoad(e) {
        if (!common.isEmpty(e.referrerId)) {
            uni.setStorageSync('referrerId', e.referrerId);
            console.log("uni.getStorageSync('referrerId')")
            console.log(uni.getStorageSync('referrerId'))
        }
        this.user = uni.getStorageSync(app.globalData.data.userInfoKey)
        this.getUserList();
    },
    methods: {
        getUserList() {
            if (!this.hasMore || this.loading) return;
            this.loading = true;
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.search,
                method: "POST",
                data: {
                    page: this.page,
                    size: this.size,
                },
                success: (res) => {
                    let users = res.data.list;
                    users = users.filter(user => user.id !== this.user.id);

                    if (this.page === 1) {
                        this.userList = [];
                    }

                    if (users.length < this.size) {
                        this.hasMore = false;
                    }

                    this.userList = this.userList.concat(users); // Append new users to the list
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },

        // redirects
        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId,
            });
        },
        serviceDetailRedirect(serviceTypeId) {
            uni.navigateTo({
                url: '/pages/components/service/service-detail/service-detail?serviceTypeId=' + serviceTypeId,
            });
        }
    }
};
</script>

<style scoped>
.contact-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* Two items per row */
    gap: 16px; /* Adjust spacing between the grid items */
}

.contact-item {
    background-color: white;
    padding: 10px;
    border-radius: 8px;
    text-align: center;
}

.contact-avatar {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 50%;
    margin-bottom: 10px;
}

.contact-info {
    text-align: center;
}

.contact-name {
    font-size: 18px;
    font-weight: bold;
}

.contact-number {
    font-size: 14px;
    color: #666;
}

.service-info {
    height: 160px;
    position: relative;
}

.service-info-text {
    position: absolute;
    bottom: 40px;
    width: 100%;
    z-index: 100;
    height: 20px;
    color: white;
    font-size: 35px;
    font-weight: bold;
}

</style>
