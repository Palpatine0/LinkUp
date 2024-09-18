<template>
<div class="message-input-container">
    <img src="/static/page/chat/upload.svg" alt="Upload" class="upload-button" @click="selectFileType"/>
    <textarea
        v-model="message"
        class="message-input"
        placeholder="Enter your message..."
        @keyup.enter="sendMessage"
        rows="1"
        maxlength="2000"
        auto-height
    ></textarea>
    <img src="/static/page/chat/send.svg" alt="Send" class="send-button" @click="sendMessage"/>
</div>
</template>

<script>
export default {
    data() {
        return {
            message: '',
        };
    },
    methods: {
        sendMessage() {
            if (this.message.trim()) {
                this.$emit('handleSend', this.message.trim());
                this.message = '';
            }
        },
        selectFileType() {
            // Your existing code for file selection
        },
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
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
}

.upload-button {
    background-color: transparent;
    border: none;
    border-radius: 50%;
    width: 30px;
    height: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    padding: 5px;
}
</style>
