<template>
<div style="height: 100vh;">
    <UserDetail
        :user="user"
        :userServant="userServant"
        @contactRedirect="contactRedirect"
    />
</div>
</template>

<script>
import UserDetail from 'components/page/user/user-detail.vue';

export default {
    name: "HomePage",
    components: {
        UserDetail
    },
    data() {
        return {
            userId: '',
            user: {},
            userServant: {}
        }
    },
    onLoad(params) {
        this.userId = params.userId
        this.getUser()
        this.getUserServant()
    },
    methods: {
        getUser() {
            uni.request({
                url: getApp().globalData.requestUrl + '/user/search',
                method: 'POST',
                data: {
                    id: this.userId
                },
                success: (res) => {
                    this.user = res.data.userList[0]
                },
            });
        },
        getUserServant() {
            uni.request({
                url: getApp().globalData.requestUrl + '/user-servant/search',
                method: 'POST',
                data: {
                    userId: this.userId
                },
                success: (res) => {
                    this.userServant = res.data.userServantList[0]
                },
            });
        },
        contactRedirect() {
            console.log("contactRedirect() {contactRedirect() {contactRedirect() {contactRedirect() {")
            uni.navigateTo({
                url: '/pages/chat/chat-window/chat-window?contactId=' + this.userId
            });
        }
    }
}
</script>

<style scoped>
</style>
