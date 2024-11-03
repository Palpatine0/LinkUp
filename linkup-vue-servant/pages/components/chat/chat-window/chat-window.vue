<template>
<div class="chat-page">
    <ChatHeader
        :username="contact.nickname"
        :avatar="contact.avatar"
    />

    <scroll-view
        v-show="isUserInfoLoaded"
        class="message-list"
        :scroll-into-view="lastMessageId"
        scroll-y="true"
        @scrolltoupper="loadMoreMessages"
        style="height: 80vh"
    >
        <div
            v-for="(item, index) in processedMessages"
            :key="item.type === 'message' ? (item.data.id || item.data.tempId) : 'time-' + index"
            :id="item.type === 'message' ? 'message-' + (item.data.id || item.data.tempId) : ''"
        >
            <div v-if="item.type === 'timeLabel'" class="time-label">
                {{ item.time }}
            </div>
            <div v-else>
                <MessageBubble
                    :senderAvatar="user.avatar"
                    :receiverAvatar="contact.avatar"
                    :receiverId="contact.id"
                    :content="item.data.content"
                    :msgBelongs="item.data.senderId === userId"
                    :isRead="item.data.isRead"
                />
            </div>
        </div>
    </scroll-view>

    <MessageInput
        :conversationId="conversationId"
        :conversation="conversation"
        @handleSend="handleSend"
    />
</div>
</template>

<script>
import ChatHeader from '../../../../components/page/chat/chat-header.vue';
import MessageBubble from '../../../../components/page/chat/message-bubble.vue';
import MessageInput from '../../../../components/page/chat/message-input.vue';

export default {
    name: "chat-window",
    components: {
        ChatHeader,
        MessageBubble,
        MessageInput,
    },
    computed: {
        processedMessages() {
            const result = [];
            let lastTimestamp = null;

            this.messages.forEach((message) => {
                const messageTime = new Date(message.createdAt);
                let needTimeLabel = false;

                if(!lastTimestamp) {
                    needTimeLabel = true;
                } else {
                    const timeDiff = messageTime - lastTimestamp;

                    if(messageTime.toDateString() !== lastTimestamp.toDateString()) {
                        // Different day
                        needTimeLabel = true;
                    } else if((messageTime - lastTimestamp) >= 5 * 60 * 1000) {
                        // Same day, more than 5 minutes apart
                        needTimeLabel = true;
                    }
                }

                if(needTimeLabel) {
                    // Insert time label before this message
                    const timeLabel = this.$common.timeToStampRecord(messageTime);
                    result.push({
                        type: 'timeLabel',
                        time: timeLabel,
                        timestamp: message.createdAt,
                    });
                }

                // Add the message
                result.push({
                    type: 'message',
                    data: message,
                });

                lastTimestamp = messageTime;
            });

            return result;
        },
    },
    data() {
        return {
            isUserInfoLoaded: false,

            userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
            user: {},

            contactId: '',
            contact: {},

            conversationId: null,
            conversation: {},

            scrollTop: 0,
            messages: [],
            lastMessageId: '',
            socketOpen: false,
            socketTask: null,
        };
    },
    async onLoad(params) {
        this.contactId = parseInt(params.contactId);
        await this.getUser();
        await this.getContact();
        await this.getConversation();
        this.connectWebSocket();
    },
    onUnload() {
        if(this.socketTask) {
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
                        id: this.userId,
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
        getConversation() {
            return new Promise((resolve, reject) => {
                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.conversation.search,
                    method: 'POST',
                    data: {
                        clientId: this.contactId,
                        servantId: this.userId,
                    },
                    success: (res) => {
                        if(res.data.status === 200 && res.data.list && res.data.list.length > 0) {
                            this.conversation = res.data.list[0];
                            this.conversationId = this.conversation.id;
                        } else {
                            this.conversation = {};
                            this.conversationId = null;
                        }
                        resolve();
                    },
                    fail: (err) => {
                        reject(err);
                    },
                });
            });
        },
        getMessages() {
            return new Promise(async(resolve, reject) => {
                if(this.loading || !this.hasMore) return;
                this.loading = true;

                uni.request({
                    url: getApp().globalData.data.requestUrl + this.$API.message.search,
                    method: 'POST',
                    data: {
                        senderId: this.userId,
                        recipientId: this.contactId,
                        page: this.page,
                        size: 14
                    },
                    success: async(res) => {
                        const fetchedMessages = res.data.list;

                        // If fewer messages are returned than requested, assume no more to load
                        if(fetchedMessages.length < 14) {
                            this.hasMore = false;
                        }

                        // Prepend the newly fetched messages and sort by createdAt
                        this.messages = fetchedMessages.concat(this.messages).sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
                        this.$nextTick(() => {
                            if(this.messages.length > 0) {
                                this.lastMessageId =
                                    'message-' + this.messages[this.messages.length - 1].id;
                            }
                        });

                        this.page += 1;
                        this.isUserInfoLoaded = true;
                        this.loading = false;

                        // Mark messages as read after fetching
                        await this.markMessagesAsRead();
                        this.sendReadReceipt();
                        resolve();
                    },
                    fail: (err) => {
                        this.loading = false;
                        reject(err);
                    }
                });
            });
        },
        async markMessagesAsRead() {
            const unreadMessageIds = this.messages
            .filter(msg => msg.senderId == this.contactId && msg.isRead == 0)
            .map(msg => msg.id);

            if(unreadMessageIds.length > 0) {
                if(this.socketOpen) {
                    const readReceiptData = {
                        type: 'readReceipt',
                        data: {
                            messageIds: unreadMessageIds,
                            senderId: this.userId,
                            recipientId: this.contactId
                        }
                    };
                    const messageStr = JSON.stringify(readReceiptData);
                    this.socketTask.send({
                        data: messageStr,
                        success: () => {
                            // Update local messages
                            this.messages.forEach(msg => {
                                if(unreadMessageIds.includes(msg.id)) {
                                    msg.isRead = 1; // Update the read status locally
                                }
                            });
                        },
                        fail: () => {
                        },
                    });
                } else {
                }
            }
        },
        loadMoreMessages() {
            this.getMessages().then(() => {
                this.scrollTop += 100;
            });
        },

        handleSend(messageContent) {
            if(this.socketOpen) {
                const tempId = Date.now();
                const messageData = {
                    type: 'message',
                    data: {
                        tempId: tempId,
                        senderId: this.userId,
                        recipientId: this.contactId,
                        conversationId: this.conversationId,
                        content: messageContent,
                        mediaType: 0,
                    },
                };
                const messageStr = JSON.stringify(messageData);
                this.socketTask.send({
                    data: messageStr,
                    success: () => {
                    },
                    fail: () => {
                    },
                });
                // Add the message to the local messages array
                this.messages.push({
                    id: tempId,
                    tempId: tempId,
                    content: messageContent,
                    senderId: this.userId,
                    createdAt: new Date().toISOString(),
                    isRead: 0,
                });
                this.$nextTick(() => {
                    this.lastMessageId = 'message-' + tempId;
                });
            } else {
            }
        },
        sendReadReceipt() {
            // Collect unread message IDs from messages sent by the contact
            const unreadMessageIds = this.messages
            .filter(msg => msg.senderId === this.contactId && msg.isRead === 0)
            .map(msg => msg.id);

            if(unreadMessageIds.length > 0 && this.socketOpen) {
                const readReceiptData = {
                    type: 'readReceipt',
                    data: {
                        senderId: this.userId,
                        recipientId: this.contactId,
                        messageIds: unreadMessageIds,
                    },
                };
                this.socketTask.send({
                    data: JSON.stringify(readReceiptData),
                    success: () => {
                        // Update local messages to mark as read
                        this.messages.forEach(msg => {
                            if(unreadMessageIds.includes(msg.id)) {
                                msg.isRead = 1;
                            }
                        });
                    },
                    fail: () => {
                    },
                });
            }
        },
        sendImmediateReadReceipt(messageIds) {
            if(this.socketOpen) {
                const readReceiptData = {
                    type: 'readReceipt',
                    data: {
                        senderId: this.userId,
                        recipientId: this.contactId,
                        messageIds: messageIds,
                    },
                };
                this.socketTask.send({
                    data: JSON.stringify(readReceiptData),
                    success: () => {
                        // Update local messages to mark as read
                        this.messages.forEach((msg, index) => {
                            if(messageIds.map(id => String(id)).includes(String(msg.id))) {
                                this.$set(this.messages[index], 'isRead', true);
                            }
                        });
                    },
                    fail: () => {
                    },
                });
            } else {
            }
        },

        connectWebSocket() {
            if(this.socketTask) {
                return;
            }

            this.socketTask = uni.connectSocket({
                url: getApp().globalData.data.socketUrl + '/chat?userId=' + this.userId,
                success: () => {
                },
                fail: (err) => {
                    this.socketTask = null;
                    setTimeout(() => {
                        this.connectWebSocket();
                    }, 5000);
                },
            });

            this.socketTask.onOpen(() => {
                this.socketOpen = true;
                this.getMessages();
            });
            this.socketTask.onMessage((res) => {
                const messageObject = JSON.parse(res.data);
                const messageData = messageObject.data;
                const messageType = messageObject.type;

                if(messageType === 'message') {
                    const msgSenderId = messageData.senderId;
                    const msgRecipientId = messageData.recipientId;

                    // Handle incoming chat message
                    if(msgSenderId == this.contactId || msgRecipientId == this.contactId) {
                        if(msgSenderId == this.userId) {
                            // Update local message with database ID
                            const index = this.messages.findIndex(msg => msg.tempId == messageData.tempId);
                            if(index !== -1) {
                                this.$set(this.messages, index, {
                                    ...this.messages[index],
                                    id: messageData.id,
                                    createdAt: messageData.createdAt,
                                    isRead: messageData.isRead,
                                });
                            } else {
                            }
                        } else {
                            // Handle incoming message from the contact
                            this.messages.push({
                                id: messageData.id,
                                content: messageData.content,
                                senderId: messageData.senderId,
                                createdAt: messageData.createdAt,
                                isRead: messageData.isRead,
                            });
                            this.$nextTick(() => {
                                this.lastMessageId = 'message-' + messageData.id;
                            });

                            // Send read receipt immediately
                            if(msgSenderId == this.contactId) {
                                this.sendImmediateReadReceipt([messageData.id]);
                            }
                        }
                    }
                } else if(messageType === 'readReceipt') {
                    const messageIds = (messageData.messageIds || []).map(id => String(id));
                    const tempMessageIds = (messageData.tempMessageIds || []).map(id => String(id));
                    this.messages.forEach((msg, index) => {
                        const msgIdStr = String(msg.id);
                        const msgTempIdStr = String(msg.tempId);
                        if(messageIds.includes(msgIdStr) || tempMessageIds.includes(msgTempIdStr)) {
                            this.$set(this.messages[index], 'isRead', 1);
                        }
                    });
                } else if(messageType === 'error') {
                    if(messageData == "No permission to initiate conversation") {
                        for(let i = this.messages.length - 1; i >= 0; i--) {
                            if(this.messages[i].senderId === this.userId) {
                                this.$set(this.messages[i], 'erroring', 1);
                                break;
                            }
                        }
                        uni.showToast({
                            title: this.$t('componentPage>chat>chatWindow.showToast.cannotSendMsg'),
                            icon: 'none',
                            duration: 6000
                        });
                    }
                }
            });

            this.socketTask.onError((err) => {
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
    flex-direction: column;
}

.time-label {
    text-align: center;
    margin: 10px 0;
    color: #888;
    font-size: 12px;
}

</style>
