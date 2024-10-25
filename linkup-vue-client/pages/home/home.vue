<template>
<div class="page">
    <app-title type="h1" bold="true">{{ $t('home.nearby') }}</app-title>

    <!-- Category Filters (Optional, can be expanded) -->
    <div class="app-container service-info" :style="{backgroundImage: `url(${serviceTypeList[0].coverImg})`, backgroundRepeat: 'no-repeat',backgroundPosition: 'center center',backgroundSize: '135%'}" @click="serviceDetailRedirect(serviceTypeList[0].id)">
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
export default {
    name: "home",
    data() {
        return {
            user: {},
            userList: [],
            serviceTypeList: []
        };
    },
    onLoad(param) {
        console.log("HOME PARAM")
        console.log(param)
        if(!this.$common.isEmpty(param.referrerId)) {
            uni.setStorageSync('referrerId', param.referrerId);
            console.log("uni.getStorageSync('referrerId')")
            console.log(uni.getStorageSync('referrerId'))
        }
        this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey)
        this.getDataList();
    },
    methods: {
        getDataList() {
            if(!this.hasMore || this.loading) return;
            this.loading = true;
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.search,
                method: "POST",
                data: {
                    page: this.page,
                    size: this.pageSize,
                    role: 2
                },
                success: (res) => {
                    let users = res.data.list;
                    users = users.filter(user => user.id !== this.user.id);

                    if(this.page === 1) {
                        this.userList = [];
                    }

                    if(users.length < this.pageSize) {
                        this.hasMore = false;
                    }

                    this.userList = this.userList.concat(users);
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.serviceType.search,
                method: "POST",
                data: {},
                success: (res) => {
                    this.serviceTypeList = [];
                    this.serviceTypeList = res.data.list;
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
