<template>
<div class="message-input-container">
    <img src="/static/page/chat/upload.svg" alt="Upload" class="upload-button" @click="selectFileType"/>
    <textarea
        v-model="message"
        :style="{height: `${currentHeight}px`}"
        class="message-input"
        placeholder="Enter your message..."
        @input="autoResize"
        @keyup.enter="sendMessage"
        rows="1"
        maxlength="2000"
    ></textarea>
    <img src="/static/page/chat/send.svg" alt="Send" class="send-button" @click="sendMessage"/>
</div>
</template>

<script>
export default {
    data() {
        return {
            message: '',
            minHeight: 20,
            currentHeight: 20,  // Use this reactive property to control the height
        };
    },
    methods: {
        sendMessage() {
            if (this.message.trim()) {
                this.$emit('send', this.message.trim());
                this.message = '';
                this.currentHeight = this.minHeight;
            }
        },
        autoResize() {
            // As an alternative to manipulating the DOM, use Vue to reactively control the size
            this.currentHeight = this.minHeight; // Reset to min height to calculate properly
            this.$nextTick(() => {
                const estimatedSize = this.estimateHeight(this.message);
                this.currentHeight = Math.max(this.minHeight, estimatedSize);
            });
        },
        estimateHeight(text) {
            // Simple estimation based on character count, you can adjust this method as needed
            const lines = text.split('\n').length + (text.length / 50); // Approx chars per line
            return lines * 20;  // Approx line height in pixels
        }
    }
};
</script>

<style scoped>
.message-input-container {
    display: flex;
    padding: 10px;
    background-color: #f3f2f6;
    border-top: 1px solid #ddd;
    align-items: center;
}

.message-input {
    flex-grow: 1;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 10px;
    margin-right: 10px;
    background-color: white;
    resize: none; // Prevent user resizing
    overflow-y: hidden; // Hide scrollbar to maintain design
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
    padding: 5px 10px 5px 0px;
}
</style>
