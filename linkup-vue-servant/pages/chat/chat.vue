<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('chat.chats') }}</app-title>
    </div>

    <!-- Search input -->
    <app-input mode="text" :placeholder="$t('pub.page.search')" col="12" class="mb-2"/>

    <!-- Contact List -->
    <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh">
        <div v-for="(contact, index) in contactList" :key="contact.id">
            <div
                @click="contactRedirect(contact.id)"
                class="contact-item"
            >
                <img :src="contact.avatar" alt="contact.name" class="contact-avatar">
                <div class="contact-info">
                    <h2 class="contact-name">{{ contact.nickname }}</h2>
                    <p class="contact-number">{{ contact.lastMessage }}</p>
                </div>
            </div>
            <!-- Separator div instead of border-bottom -->
            <div v-if="index !== contactList.length - 1" class="separator"></div>
        </div>
    </scroll-view>
</div>
</template>

<script>
import app from "../../App.vue";
import common from "../../utils/common";

export default {
    data() {
        return {
            // ** page vars ** //
            page: 1,
            size: 10,
            hasMore: true,
            loading: false,
            // ** /page vars ** //

            userId: uni.getStorageSync(app.globalData.data.userInfoKey).id,
            contactList: [],
        };
    },
    onShow() {
        this.contactList = []
        if(!this.$common.isEmpty(this.userId)) {
            this.getUserList();
        }
    },
    methods: {
        // Step 1: Fetch messages to find unique user IDs who sent/received messages
        getUserList() {
            if(this.loading) return; // Prevent multiple requests
            this.loading = true;
            const uniqueUserIds = new Set(); // To store all unique contact IDs

            // Step 1: Find messages where current user is the sender
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.message.searchContacts,
                method: 'POST',
                data: {
                    senderId: this.userId,
                    page: this.page,
                    size: this.pageSize,
                },
                success: (resR) => {
                    const sentMessages = resR.data.list;
                    sentMessages.forEach(message => {
                        uniqueUserIds.add(message.recipientId);
                    });


                    // Step 2: Find messages where current user is the recipient
                    uni.request({
                        url: getApp().globalData.data.requestUrl + this.$API.message.searchContacts,
                        method: 'POST',
                        data: {
                            recipientId: this.userId,
                            page: this.page,
                            size: this.pageSize,
                        },
                        success: (resS) => {
                            const receivedMessages = resS.data.list;
                            // Gather all sender IDs
                            receivedMessages.forEach(message => {
                                uniqueUserIds.add(message.senderId);
                            });

                            // Fetch details for all unique contacts
                            uniqueUserIds.forEach(id => {
                                this.getUserDetails(id);
                            });

                            this.loading = false;
                        },
                        fail: (err) => {
                            this.loading = false;
                        }
                    });
                },
                fail: (err) => {
                    this.loading = false;
                }
            });
        },

        // Step 2: Fetch user details for the unique user IDs found in the messages
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

.contact-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.contact-info .contact-name {
    font-size: 16px;
    font-weight: bold;
    margin: 0;
}

.contact-info .contact-number {
    font-size: 14px;
    color: #666;
}
</style>