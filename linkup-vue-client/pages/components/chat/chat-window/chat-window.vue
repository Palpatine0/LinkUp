<template>
<div class="chat-page">
    <ChatHeader
        :username="contact.nickname"
        :avatar="contact.avatar"
    />
    <div class="message-list">
        <MessageBubble
            v-for="message in messages"
            :key="message.id"
            :text="message.text"
            :isSender="message.sender === currentUser"
        />
    </div>
    <MessageInput @send="handleSend"/>
</div>
</template>

<script>
import ChatHeader from '../../../../components/page/chat/chat-header.vue';
import MessageBubble from '../../../../components/page/chat/message-bubble.vue';
import MessageInput from '../../../../components/page/chat/message-input.vue';

export default {
    components: {
        ChatHeader,
        MessageBubble,
        MessageInput
    },
    data() {
        return {
            currentUser: 'me',
            contactId: "",
            contact: {},
            messages: [
                {id: 1, text: 'Hello', sender: 'me'},
                {id: 2, text: 'Hi!', sender: 'them'},
                {id: 3, text: 'How are you?', sender: 'me'}
            ]
        }
    },
    onLoad(params) {
        this.contactId = params.contactId;
        this.getUser();
    },
    methods: {
        getUser() {
            uni.request({
                url: getApp().globalData.requestUrl + '/user/search',
                method: 'POST',
                data: {
                    id: this.contactId
                },
                success: (res) => {
                    this.contact = res.data.userList[0];
                }
            });
        },
        handleSend(message) {
            this.messages.push({id: Date.now(), text: message, sender: 'me'});
        }
    }
}
</script>

<style scoped>
.chat-page {
    display: flex;
    flex-direction: column;
    height: 100vh;
}

.message-list {
    flex-grow: 1;
    padding: 10px;
    overflow-y: auto;
}

.message-input-container {
    border-top: 1px solid #ddd;
}
</style>
