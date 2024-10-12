<template>
<div class="message-input-container">
    <img src="/static/page/chat/upload.svg" alt="Upload" class="upload-button" @click="chatItemSelectorToggle"/>
    <textarea
        v-model="message"
        class="message-input"
        @keyup.enter="sendMessage"
        rows="1"
        maxlength="2000"
        auto-height
    ></textarea>
    <img src="/static/page/chat/send.svg" alt="Send" class="send-button" @click="sendMessage"/>
    <ChatItemSelector v-if="isChatItemSelectorToggleVisible"></ChatItemSelector>
</div>
</template>

<script>
import ChatItemSelector from "./chat-item-selector.vue";

export default {
    name: "message-input",
    components: {
        ChatItemSelector
    },
    data() {
        return {
            message: '',
            isChatItemSelectorToggleVisible: false
        };
    },
    methods: {
        sendMessage() {
            if(this.message.trim()) {
                this.$emit('handleSend', this.message.trim());
                this.message = '';
            }
        },
        chatItemSelectorToggle() {
            this.isChatItemSelectorToggleVisible = !this.isChatItemSelectorToggleVisible
        }
    },
};
</script>

<style scoped>
.message-input-container {
    display: flex;
    background-color: #f3f2f6;
    border-top: 1px solid #ddd;
    align-items: center;
    padding: 10px;
}

.message-input {
    flex-grow: 1;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 10px;
    background-color: white;
    resize: none; /* Prevent user resizing */
    overflow-y: auto; /* Allow scrolling when max height is reached */
    max-height: 20vh; /* Set the maximum height as needed */
}

.send-button {
    background-color: transparent;
    border: none;
    border-radius: 50%;
    width: 25px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    align-self: flex-end;
    padding: 0 8px 0 10px;
}

.upload-button {
    background-color: transparent;
    border: none;
    border-radius: 50%;
    width: 25px;
    height: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    padding: 0 10px 6px 8px;
    align-self: flex-end;
}
</style>