<template>
<div :class="['message-bubble', isSender ? 'sender' : 'receiver']">
    <p>{{ text }}</p>
</div>
</template>

<script>
export default {
    props: {
        text: { type: String, required: true },
        isSender: { type: Boolean, default: false }
    }
}
</script>

<style scoped>
.message-bubble {
    max-width: 60%;
    margin: 10px;
    padding: 10px;
    border-radius: 10px;
    color: white;
}

.sender {
    background-color: #007bff;
    margin-left: auto;
}

.receiver {
    background-color: #ccc;
}
</style>
