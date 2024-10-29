<template>
<div :class="['message-container', msgBelongs ? 'sender-container' : 'receiver-container']">
    <img :src="msgBelongs ? senderAvatar : receiverAvatar" alt="avatar" class="avatar"/>
    <div :class="['message-bubble', msgBelongs ? 'sender' : 'receiver']">
        <p class="message-content">{{ content }}</p>
        <!-- read indicator -->
        <div v-if="msgBelongs" class="read-indicator">
            <img v-if="isRead" style="width: 12px;height: 12px" src="/static/page/chat/circle-check-solid.svg">
            <img v-else style="width: 12px;height: 12px" src="/static/page/chat/circle-regular.svg">
        </div>
        <div v-if="msgBelongs && erroring" class="erroring-exclamation">
            <img style="width: 18px;height: 18px" src="/static/page/chat/circle-exclamation.svg">
        </div>
    </div>
</div>
</template>

<script>
export default {
    props: {
        senderAvatar: { type: String, required: true },
        receiverAvatar: { type: String, required: true },
        content: {type: String, required: true},
        msgBelongs: {type: Boolean, default: false},
        isRead: {type: Boolean, default: false},
        erroring: {type: Boolean, default: false},
    },
};
</script>

<style scoped>
.message-container {
    display: flex;
    align-items: flex-end; /* Aligns items (avatar and bubble) to the bottom */
    width: 100%;
    margin: 10px 0;
}

.avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    margin: 5px;
}

.sender-container {
    flex-direction: row-reverse; /* Aligns avatar and message to the right for sender */
}

.receiver-container {
    flex-direction: row; /* Aligns avatar and message to the left for receiver */
}

.message-bubble {
    display: inline-block; /* Allows bubble to fit content width */
    width: auto; /* Automatically adjusts to the content length */
    max-width: 60%; /* Maximum width the bubble can grow to */
    margin: 5px 10px;
    padding: 10px;
    border-radius: 16px;
    color: #000;
    overflow-wrap: break-word; /* Allows long words to break and wrap */
    word-wrap: break-word; /* For compatibility with older browsers */
}

.sender {
    background-color: #cfe6fd;
    margin-left: auto; /* Aligns to the right */
}

.receiver {
    background-color: #f1f1f1;
    margin-right: auto; /* Aligns to the left */
}

.message-content {
    white-space: pre-wrap; /* Preserves whitespace and line breaks */
}

.read-indicator {
    margin-top: -8px;
    align-self: flex-end;
    position: absolute;
    margin-left: -25px;
}

.erroring-exclamation {
    background-color: #FFF;
    border-radius: 50%;
    margin-top: -12px;
    align-self: flex-end;
    position: absolute;
    margin-left: -34px;
}
</style>
