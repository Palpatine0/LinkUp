<template>
<scroll-view :scroll-top="0" scroll-y="true" :style="{ height: height }" class="mt-4">
    <div class="contact-grid">
        <div
            v-for="(user, index) in userList"
            :key="user.id"
            :class="{'contact-item': true, 'last-item': index === userList.length - 1}"
            @click="userDetailRedirect(user.id)"
        >
            <img :src="user.avatar" alt="user.name" class="contact-avatar">
            <div class="contact-info">
                <h2 class="contact-name">{{ user.nickname }}</h2>
                <p class="contact-number">{{ user.currentLocation }}</p>
            </div>
        </div>
    </div>
</scroll-view>
</template>

<script>
export default {
    name: 'UserListComponent',
    props: {
        userList: {
            type: Array,
            required: true
        },
        height: {
            type: String,
            default: '72vh'
        }
    },
    methods: {
        userDetailRedirect(userId) {
            this.$emit('user-click', userId);
        }
    }
};
</script>

<style scoped>
.contact-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* Two items per row */
    gap: 16px; /* Adjust spacing between the grid items */
}

.contact-item {
    background-color: white;
    padding: 10px;
    border-radius: 8px;
    text-align: center;
}

.contact-avatar {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 50%;
    margin-bottom: 10px;
}

.contact-info {
    text-align: center;
}

.contact-name {
    font-size: 18px;
    font-weight: bold;
}

.contact-number {
    font-size: 14px;
    color: #666;
}
</style>
