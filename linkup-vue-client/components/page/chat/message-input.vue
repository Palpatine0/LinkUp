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
    <ChatItemSelector
        v-show="isChatItemSelectorToggleVisible"
        @giftSelected="handleGiftSelection"
    />
    <Gift v-if="isGiftVisible"/>
</div>
</template>

<script>
import ChatItemSelector from "./chat-item-selector.vue";
import Gift from "./gift.vue";
import app from "../../../App.vue";
import $common from "../../../utils/common";

export default {
    name: "message-input",
    computed: {
        $common() {
            return $common
        }
    },
    props: {
        randomNum: {type: String},
        senderId: {type: String},
        contactId: {type: String},
        conversationId: {type: String},
        conversation: {type: Object},
    },
    components: {
        ChatItemSelector,
        Gift
    },
    data() {
        return {
            conversationId: '',
            conversation: {},

            message: '',

            canSendMessage: false,
            conversationExpirationTime: '',

            timeouts: [],

            isChatItemSelectorToggleVisible: false,
            isGiftVisible: false,
        };
    },
    mounted() {
        this.isEligibleSendMsg()
        this.$bus.$on(
            'giftToggle' + this.randomNum,
            (visibility) => {
                this.isGiftVisible = visibility;
            }
        );
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
                    this.$bus.$emit(
                        'updateCanSendMessage' + this.randomNum,
                        this.canSendMessage
                    );
                    this.conversationExpirationTime = this.$t('componentPage>chat>chatWindow.conversationExpireAt') + this.$common.stampToTime(this.conversation.expirationTime, {yyyy: false, ss: false, MM: false, dd: false,});
                    this.enableChatForDuration(remainingTime);
                    this.isGiftVisible = false
                } else {
                    this.canSendMessage = false;
                    this.conversationExpirationTime = '';
                }
            } else {
                this.canSendMessage = false;
                this.isGiftVisible = true;
                this.conversationExpirationTime = '';
            }
        },
        enableChatForDuration(duration) {
            this.canSendMessage = true;
            const timeout = setTimeout(() => {
                this.canSendMessage = false;
            }, duration * 1000);
            this.timeouts.push(timeout);
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
                    this.$emit('handleSend', this.message.trim(), this.conversationId);
                    this.message = '';
                }
            }
        },

        handleGiftSelection(selectedGift) {
            this.processGiftPurchase(selectedGift);
        },
        processGiftPurchase(gift) {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.gift.purchase,
                method: 'POST',
                data: {
                    senderId: this.senderId,
                    recipientId: this.contactId,
                    giftId: gift.id,
                },
                success: (res) => {
                    if(res.data.status == 200) {
                        uni.showToast({title: this.$t('pub.showToast.success'), icon: 'none'});
                        this.$parent.getConversation();
                    } else if(res.data.status == 500) {
                        if(res.data.message == 'Insufficient looking coins to purchase the gift') {
                            if(this.language != 'zh-Hans') {
                                uni.showToast({title: res.data.message, icon: 'none'});
                            } else {
                                uni.showToast({title: '你的领客币余额不足', icon: 'none'});
                            }
                        }
                    }
                },
                fail: (error) => {
                    uni.showToast({title: this.$t('pub.showToast.fail'), icon: 'none'});
                },
            });
        },

        // Toggles
        chatItemSelectorToggle() {
            this.isChatItemSelectorToggleVisible = !this.isChatItemSelectorToggleVisible
        },
        giftToggle() {
            this.isGiftVisible = !this.isGiftVisible
        }
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