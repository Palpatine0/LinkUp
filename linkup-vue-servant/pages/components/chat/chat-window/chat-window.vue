<template>
<div class="chat-page">
    <ChatHeader :username="contact.nickname" :avatar="contact.avatar"/>

    <scroll-view
        v-if="isUserInfoLoaded"
        class="message-list"
        :scroll-top="scrollTop"
        scroll-y="true"
        @scrolltoupper="loadMoreMessages"
        style="height: 80vh"
    >
        <div v-for="message in messages" :key="message.id">
            <MessageBubble
                :content="message.content"
                :msgBelongs="message.senderId === userId"
                :isRead="message.isRead"
            />
        </div>
    </scroll-view>

    <MessageInput @handleSend="handleSend"/>
</div>
</template>


<script>
import ChatHeader from '../../../../components/page/chat/chat-header.vue';
import MessageBubble from '../../../../components/page/chat/message-bubble.vue';
import MessageInput from '../../../../components/page/chat/message-input.vue';
import app from "../../../../App.vue";

export default {
    components: {
        ChatHeader,
        MessageBubble,
        MessageInput,
    },
    data() {
        return {
            userId: uni.getStorageSync(app.globalData.data.userInfoKey).id,
            user: {},
            contactId: '',
            contact: {},
            isUserInfoLoaded: false,
            messages: [],
            scrollTop: 0, // Scroll position, will be updated when messages are loaded
            page: 1, // For pagination
            size: 10, // Number of messages to load per request
            hasMoreMessages: true, // Check if there are more messages to load
            loading: false, // Prevent multiple requests at once
        };
    },
    async onLoad(params) {
        this.contactId = params.contactId;
        await this.getUser();
        await this.getContact();
        await this.getMessages(); // Load the initial set of messages
    },
    methods: {
        getUser() {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.user.search,
                    method: 'POST',
                    data: {
                        id: this.userId
                    },
                    success: (res) => {
                        this.user = res.data.list[0];
                        resolve();
                    },
                    fail: (err) => {
                        reject(err);
                    },
                });
            });
        },
        getContact() {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.user.search,
                    method: 'POST',
                    data: {
                        id: this.contactId,
                    },
                    success: (res) => {
                        this.contact = res.data.list[0];
                        resolve();
                    },
                    fail: (err) => {
                        reject(err);
                    },
                });
            });
        },
        // Fetch initial messages
        getMessages() {
            return new Promise(async (resolve, reject) => {
                if (this.loading || !this.hasMoreMessages) return;
                this.loading = true;

                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.message.search,
                    method: 'POST',
                    data: {
                        senderId: this.userId,    // Current user's ID
                        recipientId: this.contactId,  // Contact's ID
                        page: this.page,
                        size: this.size
                    },
                    success: (res) => {
                        const fetchedMessages = res.data.list;

                        // If fewer messages are returned than requested, assume no more to load
                        if (fetchedMessages.length < this.size) {
                            this.hasMoreMessages = false;
                        }

                        // Prepend the newly fetched messages and sort by createdAt
                        this.messages = fetchedMessages.concat(this.messages).sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));

                        this.page += 1;
                        this.isUserInfoLoaded = true;
                        this.loading = false;
                        resolve();
                    },
                    fail: (err) => {
                        this.loading = false;
                        reject(err);
                    }
                });

                await this.markMessagesAsRead();
            });
        },
        async markMessagesAsRead() {
            const unreadMessageIds = this.messages
            .filter(msg => msg.senderId === this.contactId && !msg.isRead)
            .map(msg => msg.id);

            if (unreadMessageIds.length > 0) {
                await uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.message.markAsRead,
                    method: 'POST',
                    data: {
                        messageIds: unreadMessageIds,
                    },
                    success: (res) => {
                        // Update local messages
                        this.messages.forEach(msg => {
                            if (unreadMessageIds.includes(msg.id)) {
                                msg.isRead = true;
                            }
                        });
                    },
                    fail: (err) => {
                        console.error('Failed to mark messages as read:', err);
                    },
                });
            }
        },

        // Load more messages when scrolling to the top
        loadMoreMessages() {
            this.getMessages().then(() => {
                // Adjust scrollTop to maintain current view after loading older messages
                this.scrollTop += 100; // Adjust this value based on how many messages are loaded and the estimated height
            });
        },
        // Handle sending a new message
        handleSend(messageContent) {
            const messageData = {
                senderId: this.userId,
                recipientId: this.contactId,
                content: messageContent,
                mediaType: 0
            };
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.message.save,
                method: 'POST',
                data: messageData,
                success: (res) => {
                    this.messages.push({
                        id: res.data.id,
                        content: messageContent,
                        senderId: this.userId,
                        createdAt: new Date().toISOString(),
                    });
                    this.scrollTop = 0; // Scroll to the bottom after sending a new message
                },
                fail: () => {
                    uni.showToast({title: 'Network error', icon: 'none'});
                }
            });
        },


    }
};

</script>

<style scoped>
.chat-page {
    display: flex;
    flex-direction: column;
    height: 100vh;
}

.message-list {
    flex-grow: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column-reverse; /* Helps keep new messages at the bottom */
}

.message-input-container {
    border-top: 1px solid #ddd;
}

</style>
