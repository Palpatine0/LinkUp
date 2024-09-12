<template>
<div class="chat-header">
<!--    <img :src="avatar" class="avatar" />-->
    <img :src="avatar" class="avatar" />
    <span class="username">{{ username }}</span>
    <button class="select-button" @click="selectUser">选择</button>
</div>
</template>

<script>
export default {
    props: {
        username: { type: String, default: 'Unknown' },
        avatar: { type: String, default: '/static/chat/profile-circled-test.png' },
    },
    methods: {
        selectUser() {
            console.log('User selected');
        }
    }
}
</script>

<style scoped>
.chat-header {
    display: flex;
    align-items: center;
    padding: 10px 20px 10px 21px;
    background-color: #f3f2f6;
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
}

.username {
    flex-grow: 1;
    font-weight: bold;
}

.select-button {
    background-color: #007bff;
    color: white;
    border: none;
    padding: 0px 14px;
    border-radius: 15px;
    font-size: 12px;
}
</style>
