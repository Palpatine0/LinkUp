<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('chat.chats') }}</app-title>
    </div>

    <div v-if="!isUserLogin" class="center-h">
        <div class="background-icon">
            <img src="/static/page/order/messages.svg">
        </div>
        <div style="margin-top: -40px;">
            <app-button shaped @click="signIn">{{ $t('chat.signIn') }}</app-button>
        </div>
    </div>
    <div v-if="isUserLogin">
        <!-- Search input -->
        <app-input mode="text" :placeholder="$t('pub.page.search')" col="12" class="mb-2"/>
        <!-- Contact List -->
        <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh">
            <div v-for="(contact, index) in contactList" :key="contact.id">
                <div
                    @click="contactRedirect(contact.id)"
                    class="contact-item"
                >
                    <img :src="contact.avatar" alt="contact.name" class="avatar">
                    <div class="info">
                        <h2 class="name">{{ contact.nickname }}</h2>
                        <p class="contact-number">{{ contact.lastMessage }}</p>
                    </div>
                </div>
                <!-- Separator div instead of border-bottom -->
                <div v-if="index !== contactList.length - 1" class="separator"></div>
            </div>
        </scroll-view>
    </div>

</div>
</template>

<script>
import app from "../../App.vue";

export default {
    data() {
        return {
            isUserLogin: false,
            userId: uni.getStorageSync(app.globalData.data.userInfoKey).id,
            contactList: [],
        };
    },
    onShow() {
        this.isUserLogin = uni.getStorageSync(app.globalData.data.userLoginKey) == true ? true : false;
        if(this.isUserLogin) {
            this.getDataList();
        }
    },
    methods: {
        getDataList() {
            this.contactList = [];
            if(this.$common.isEmpty(this.userId)) return;
            if(this.loading) return; // Prevent multiple requests
            this.loading = true;
            const uniqueUserIds = new Set(); // To store all unique contact IDs

            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.conversation.search,
                method: 'POST',
                data: {
                    clientId: this.userId,
                    page: this.page,
                    size: this.pageSize,
                },
                success: (res) => {
                    const contactData = res.data.list;
                    contactData.forEach(contact => {
                        uniqueUserIds.add(contact.servantId);
                    });
                    console.log("uniqueUserIds")
                    console.log(uniqueUserIds)
                    uniqueUserIds.forEach(id => {
                        this.getUserDetails(id);
                    });
                },
                fail: (err) => {
                    this.loading = false;
                }
            });
        },
        getUserDetails(id) {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.search,
                method: "POST",
                data: {
                    id: id,
                },
                success: (res) => {
                    this.contactList.push(res.data.list[0])
                },
                complete: () => {
                    this.loading = false;
                },
                fail: (err) => {
                    this.loading = false;
                }
            });
        },

        async signIn() {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            await getApp().globalData.signIn()
            this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey)
            this.isUserLogin = uni.getStorageSync(getApp().globalData.data.userLoginKey)
            uni.hideLoading();
        },

        // Redirects
        contactRedirect(contactId) {
            uni.navigateTo({
                url: '/pages/components/chat/chat-window/chat-window?contactId=' + contactId
            });
        }
    }
}

</script>

<style scoped>
.contact-item {
    display: flex;
    align-items: center;
    padding: 10px;
}

.separator {
    height: 1px;
    background-color: #ccc;
    margin-left: 68px;
    width: 70vw;
}

.avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.info .name {
    font-size: 16px;
    font-weight: bold;
    margin: 0;
}

.info .contact-number {
    font-size: 14px;
    color: #666;
}
</style>