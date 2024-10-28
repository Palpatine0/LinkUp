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
        :placeholder="conversationExpirationTime"
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
        ChatItemSelector,
    },
    props: {
        conversationId: {type: [String, Number], default: null},
        conversation: {type: Object, default: () => ({})},
    },
    data() {
        return {
            message: '',

            canSendMessage: false,
            conversationExpirationTime: '',

            timeouts: [],

            isChatItemSelectorToggleVisible: false,
        };
    },
    mounted() {
        this.isEligibleSendMsg();
    },
    beforeDestroy() {
        this.timeouts.forEach((id) => clearTimeout(id));
    },

    methods: {
        isEligibleSendMsg() {
            if(this.conversation && Object.keys(this.conversation).length > 0) {
                const currentTime = new Date().getTime();
                const expirationTime = new Date(this.conversation.expirationTime).getTime();
                const remainingTime = Math.floor((expirationTime - currentTime) / 1000);
                if(remainingTime > 0) {
                    this.canSendMessage = true;
                    this.conversationExpirationTime = this.$t('componentPage>chat>chatWindow.conversationExpireAt') + this.$common.stampToTime(this.conversation.expirationTime, {yyyy: false, ss: false, MM: false, dd: false,});
                    this.enableChatForDuration(remainingTime);
                } else {
                    this.canSendMessage = false;
                    this.conversationExpirationTime = '';
                    const timeout = setTimeout(() => {
                        this.checkForConversationUpdate();
                    }, 1000);
                    this.timeouts.push(timeout);
                }
            } else {
                this.canSendMessage = false;
                this.conversationExpirationTime = '';
            }
        },
        enableChatForDuration(duration) {
            const timeout = setTimeout(() => {
                this.canSendMessage = false;
                this.checkForConversationUpdate();
            }, duration * 1000);
            this.timeouts.push(timeout);
        },
        checkForConversationUpdate() {
            if(this.$parent && typeof this.$parent.getConversation === 'function') {
                this.$parent.getConversation().then(() => {
                    this.isEligibleSendMsg();
                });
            }
        },
        sendMessage() {
            if(!this.canSendMessage) {
                uni.showModal({
                    title: this.$t('componentPage>chat>chatWindow.insufficientTimeModal.title'),
                    content: this.$t('componentPage>chat>chatWindow.insufficientTimeModal.content'),
                    showCancel: false,
                    confirmText: this.$t('pub.modal.button.confirm'),
                    cancelText: this.$t('pub.modal.button.cancel'),
                });
                return;
            } else {
                if(this.message.trim()) {
                    this.$emit('handleSend', this.message.trim());
                    this.message = '';
                }
            }
        },
        chatItemSelectorToggle() {
            this.isChatItemSelectorToggleVisible = !this.isChatItemSelectorToggleVisible;
        },
    },
    watch: {
        conversation: {
            immediate: true,
            handler() {
                this.isEligibleSendMsg();
            },
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
    padding-bottom: 20px;
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
