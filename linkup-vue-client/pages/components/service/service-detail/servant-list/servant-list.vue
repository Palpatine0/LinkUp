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
            serviceTypeId: ""
        }
    },
    onLoad(params) {
        this.serviceTypeId = params.serviceTypeId;
        console.log("serviceTypeId")
        console.log(this.serviceTypeId)
    },
    methods: {
        getServantList() {
            if (!this.hasMore || this.loading) return;

            this.loading = true;
            uni.request({
                url: getApp().globalData.requestUrl + '/user/search',
                method: "POST",
                data: {
                    page: this.page,
                    size: this.size,
                },
                success: (res) => {
                    const users = res.data.userList;

                    if (this.page === 1) {
                        this.userList = [];
                    }

                    if (users.length < this.size) {
                        this.hasMore = false;
                    }

                    this.userList = this.userList.concat(users);
                    this.page += 1;
                },
                complete: () => {
                    this.loading = false; // Reset the loading flag
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