<template>
<div :class="['message-bubble', msgBelongs ? 'sender' : 'receiver']">
    <p class="message-content">{{ content }}</p>
</div>
</template>

<script>
export default {
    props: {
        content: { type: String, required: true },
        msgBelongs: { type: Boolean, default: false },
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
</style>
