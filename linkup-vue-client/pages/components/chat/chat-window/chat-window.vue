<template>
<div class="chat-page">
    <ChatHeader :username="contact.nickname" :avatar="contact.avatar"/>

    <scroll-view
        v-show="isUserInfoLoaded"
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
    name: "chat-window",
    components: {
        ChatHeader,
        MessageBubble,
        MessageInput,
    },
    data() {
        return {
            userId: uni.getStorageSync(app.globalData.data.userInfoKey).id,
            user: {},
            isUserInfoLoaded: false,
            contactId: '',
            contact: {},
            messages: [],

            scrollTop: 0,

            socketOpen: false,
            socketTask: null,
        };
    },
    async onLoad(params) {
        this.contactId = params.contactId;
        await this.getUser();
        await this.getContact();
        await this.getMessages();
        this.connectWebSocket();
    },
    onUnload() {
        if (this.socketTask) {
            this.socketTask.close();
        }
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
        getMessages() {
            return new Promise(async (resolve, reject) => {
                if(this.loading || !this.hasMoreMessages) return;
                this.loading = true;

                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.message.search,
                    method: 'POST',
                    data: {
                        senderId: this.userId,    // Current user's ID
                        recipientId: this.contactId,  // Contact's ID
                        page: this.page,
                        size: this.pageSize
                    },
                    success: (res) => {
                        const fetchedMessages = res.data.list;

                        // If fewer messages are returned than requested, assume no more to load
                        if(fetchedMessages.length < this.pageSize) {
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
        loadMoreMessages() {
            this.getMessages().then(() => {
                this.scrollTop += 100; // Adjust this value based on how many messages are loaded and the estimated height
            });
        },
        // Handle sending a new message
        handleSend(messageContent) {
            if (this.socketOpen) {
                const messageData = {
                    senderId: this.userId,
                    recipientId: this.contactId,
                    content: messageContent,
                    mediaType: 0,
                };
                const messageStr = JSON.stringify(messageData);
                this.socketTask.send({
                    data: messageStr,
                    success: () => {
                        console.log('Message sent via WebSocket.');
                    },
                    fail: () => {
                        console.error('Failed to send message via WebSocket.');
                    },
                });
                // Add the message to the local messages array
                this.messages.push({
                    id: Date.now(),
                    content: messageContent,
                    senderId: this.userId,
                    createdAt: new Date().toISOString(),
                });
                this.scrollTop = 0;
            } else {
                console.error('WebSocket is not connected.');
            }
        },
        connectWebSocket() {
            if (this.socketTask) {
                console.log('WebSocket task already exists.');
                return;
            }
            const socketUrl = getApp().globalData.data.socketUrl + '/chat?userId=' + this.userId;
            this.socketTask = uni.connectSocket({
                url: socketUrl,
                success: () => {
                    console.log('WebSocket connection created.');
                },
                fail: (err) => {
                    console.error('WebSocket connection failed:', err);
                    this.socketTask = null;
                    setTimeout(() => {
                        this.connectWebSocket();
                    }, 5000);
                },
            });

            this.socketTask.onOpen(() => {
                console.log('WebSocket connection opened.');
                this.socketOpen = true;
            });
            this.socketTask.onMessage((res) => {
                console.log('Received message:', res.data);
                const messageData = JSON.parse(res.data);
                if (messageData.senderId == this.contactId || messageData.recipientId == this.contactId) {
                    this.messages.push({
                        id: messageData.id,
                        content: messageData.content,
                        senderId: messageData.senderId,
                        createdAt: messageData.createdAt,
                    });
                    this.scrollTop = 0;
                }
            });
            this.socketTask.onClose(() => {
                console.log('WebSocket connection closed.');
                this.socketOpen = false;
                setTimeout(() => {
                    this.connectWebSocket();
                }, 5000);
            });
            this.socketTask.onError((err) => {
                console.error('WebSocket error:', err);
                this.socketOpen = false;
            });
        }
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
</style>
