<template>
<div :class="['message-bubble', msgBelongs ? 'sender' : 'receiver']">
    <p>{{ content }}</p>
</div>
</template>

<script>
export default {
    props: {
        content: {type: String, required: true},
        msgBelongs: {type: Boolean, default: false}
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
