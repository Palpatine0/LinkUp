<template>
<div class="home-page">
    <UserDetail
        :username="'Percival'"
        :details="'Some additional details here'"
        @select-user="handleSelectUser"
        @start-chat="handleStartChat"
    />
</div>
</template>

<script>
import UserDetail from '/components/page/user/user-detail.vue';

export default {
    name: "HomePage",
    components: {
        UserDetail
    },
    methods: {
        handleSelectUser() {
            alert("User selected!");
        },
        handleStartChat() {
            alert("Chat started!");
        }
    }
}
</script>

<style scoped>
.home-page {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f5f5f5;
}
</style>
