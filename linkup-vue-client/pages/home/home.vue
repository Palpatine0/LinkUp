<template>
<div class="page">
    <app-title type="h1" bold="true">附近</app-title>

    <!-- Category Filters (Optional, can be expanded) -->
    <div class="flex">
        <div class="app-container" style="width: 48%;margin: 2px;">
            导游
        </div>
        <div class="app-container" style="width: 48%;margin: 2px;">
            XX
        </div>
    </div>
    <div class="flex">
        <div class="app-container" style="width: 48%;margin: 2px;">
            XX
        </div>
        <div class="app-container" style="width: 48%;margin: 2px;">
            XX
        </div>
    </div>

    <!-- Scroll View for User List -->
    <scroll-view :scroll-top="0" scroll-y="true" style="height: 72vh" class="mt-4">
        <div class="contact-grid">
            <div
                v-for="(user, index) in userList"
                :key="user.id"
                :class="{'contact-item': true, 'last-item': index === userList.length - 1}"
                @click="userDetailRedirect(user.id)"
            >
                <img :src="user.avatar" alt="user.name" class="contact-avatar">
                <div class="contact-info">
                    <h2 class="contact-name">{{ user.nickname }}</h2>
                    <p class="contact-number">{{ user.currentLocation }}</p>
                </div>
            </div>
        </div>
    </scroll-view>
</div>
</template>

<script>
export default {
    name: "home",
    data() {
        return {
            userList: [],  // This will store the list of users
            page: 1,       // Page number for pagination
            size: 20,      // Number of users to fetch per request
            hasMore: true, // Flag to check if more users are available to load
            loading: false // To handle the loading state
        };
    },
    onLoad() {
        this.checkUserInfo();
        this.getUserList();
    },
    methods: {
        // User handling
        checkUserInfo() {
            const openid = uni.getStorageSync('openid');
            if (this.common.isEmpty(openid)) {
                this.userProfileAvailable = false;
                uni.showModal({
                    title: '授权',
                    content: '请授权您的个人信息以使用完整服务',
                    showCancel: true,
                    confirmText: '授权',
                    success: (res) => {
                        if (res.confirm) {
                            this.getUserInfo();
                        }
                    },
                });
            } else {
                this.userProfileAvailable = true;
            }
        },
        async getUserInfo(e) {
            uni.showLoading({ title: '加载中' });
            const getUserLoginCode = () => {
                return new Promise((resolve) =>
                    uni.login({
                        provider: 'weixin',
                        success: (res) => {
                            resolve(res.code);
                        },
                        fail: () => {
                            uni.showToast({ title: '用户Code获取失败', icon: 'none' });
                        },
                    })
                );
            };
            const userInfoCode = await getUserLoginCode();
            const getUserAccountData = () => {
                return new Promise((resolve) => {
                    uni.request({
                        url: getApp().globalData.requestUrl + '/user/save-auth-info',
                        method: 'POST',
                        data: {
                            code: userInfoCode,
                        },
                        success: (res) => {
                            if (res.data.auth == null) {
                                uni.showToast({ title: '授权失败', icon: 'none' });
                            } else {
                                resolve(res.data.auth);
                            }
                        },
                        fail: () => {
                            uni.showToast({ title: '授权请求失败', icon: 'none' });
                        },
                    });
                });
            };

            const userAccountData = await getUserAccountData();
            const getUserData = () => {
                return new Promise((resolve) => {
                    uni.getUserInfo({
                        success: function (res) {
                            resolve(res.userInfo);
                        },
                    });
                });
            };

            const userData = await getUserData();
            uni.request({
                url: getApp().globalData.requestUrl + '/user/update',
                method: 'POST',
                data: {
                    // userAccountData
                    id: userAccountData.id,
                    openid: userAccountData.openid,
                    sessionKey: userAccountData.openid.sessionKey,
                    unionid: userAccountData.unionid,
                    // userData
                    role: 1,
                    nickname: userData.nickName,
                    gender: userData.gender,
                    avatar: userData.avatarUrl,
                },
                success: () => {
                    // userAccountData
                    uni.setStorageSync('userId', userAccountData.id);
                    uni.setStorageSync('openid', userAccountData.openid);
                    uni.setStorageSync('sessionKey', userAccountData.openid.session_key);
                    uni.setStorageSync('unionid', userAccountData.unionid);
                    // userData
                    uni.setStorageSync('nickname', userData.nickName);
                    uni.setStorageSync('avatar', userData.avatarUrl);
                    uni.setStorageSync('gender', userData.gender);
                    this.userProfileAvailable = true;
                    uni.hideLoading();
                    uni.showToast({ title: '授权成功', icon: 'none' });
                },
                fail: () => {
                    uni.showToast({ title: '授权失败', icon: 'none' });
                },
            });
        },

        getUserList() {
            if (!this.hasMore || this.loading) return; // Prevent multiple requests if no more data or still loading

            this.loading = true;
            uni.request({
                url: getApp().globalData.requestUrl + '/user/search', // The endpoint for fetching users
                method: "POST",
                data: {
                    page: this.page,  // Pass pagination info to the backend
                    size: this.size,
                },
                success: (res) => {
                    const users = res.data.userList;  // Assuming the API returns userList in the response

                    if (this.page === 1) {
                        this.userList = []; // Reset user list on the first page
                    }

                    if (users.length < this.size) {
                        this.hasMore = false; // If fewer users are returned, no more pages are available
                    }

                    this.userList = this.userList.concat(users); // Append new users to the list
                    this.page += 1; // Increment page number for next request
                },
                complete: () => {
                    this.loading = false; // Reset the loading flag
                },
            });
        },

        // redirects
        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId,
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
</style>
