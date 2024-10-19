<template>
<div>
    <div :class="['message-bubble', msgBelongs ? 'sender' : 'receiver']">
        <p class="message-content">{{ content }}</p>
        <div v-if="msgBelongs && isRead" class="read-indicator"></div>
        <div v-if="msgBelongs && erroring" class="erroring-exclamation">
            <img style="width: 20px;height: 20px" src="/static/page/chat/circle-exclamation.svg">
        </div>
    </div>
</div>
</template>

<script>
export default {
    props: {
        content: {type: String, required: true},
        msgBelongs: {type: Boolean, default: false},
        isRead: {type: Boolean, default: false},
        erroring: {type: Boolean, default: false},
    },
};
</script>

<style scoped>
.message-bubble {
    max-width: 60%;
    margin: 10px;
    padding: 10px;
    border-radius: 10px;
    color: white;
    overflow-wrap: break-word; /* Allows long words to break and wrap */
    word-wrap: break-word; /* For compatibility with older browsers */
}

.sender {
    background-color: #007bff;
    margin-left: auto;
}

.receiver {
    background-color: #ccc;
}

.message-content {
    white-space: pre-wrap; /* Preserves whitespace and line breaks */
}

.read-indicator {
    width: 8px;
    height: 8px;
    background-color: #007bff;
    border-radius: 50%;
    margin-top: 5px;
    align-self: flex-end;
    position: absolute;
    margin-left: -20px;
}

.erroring-exclamation {
    background-color: #FFF;
    border-radius: 50%;
    margin-top: -20px;
    align-self: flex-end;
    position: absolute;
    margin-left: -33px;
}
</style>
