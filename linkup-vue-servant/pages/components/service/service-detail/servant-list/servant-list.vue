<template>
<user-list
    :userList="servantList"
    height="100vh"
    @user-click="userDetailRedirect"
/>
</template>

<script>
export default {
    name: "servant-list",
    data() {
        return {
            // ** page vars ** //
            page: 1,
            size: 20,
            hasMore: true,
            loading: false,
            // ** /page vars ** //

            serviceTypeId: "",
            servantList: []
        }
    },
    onLoad(params) {
        this.serviceTypeId = params.serviceTypeId;
        this.getServantList()
    },
    methods: {
        getServantList() {
            if (!this.hasMore || this.loading) return;
            this.loading = true;
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.searchServant,
                method: "POST",
                data: {
                    page: this.page,
                    size: this.pageSize,
                    serviceType: this.serviceTypeId
                },
                success: (res) => {
                    const users = res.data.list;

                    if (this.page === 1) {
                        this.servantList = [];
                    }

                    if (users.length < this.pageSize) {
                        this.hasMore = false;
                    }

                    this.servantList = this.servantList.concat(users);
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false;
                },
            });
        },

        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/components/user/user-detail/user-detail?userId=' + userId,
            });
        },
    }
}
</script>

<style scoped>
</style>