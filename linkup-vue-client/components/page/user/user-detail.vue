<template>
<div class="user-card">
    <!-- Top Section with Image/Background -->
    <div class="top-section">
        <div class="avatar-placeholder"></div>
        <div class="user-info">
            <p class="username">{{ username }}</p>
            <p class="user-details">{{ details }}</p>
        </div>
        <button class="select-button" @click="selectUser">选择达人</button>
    </div>

    <!-- Middle Section (Details/Description) -->
    <div class="middle-section">
        <div class="content-placeholder"></div>
        <div class="detail-placeholder"></div>
        <div class="detail-placeholder"></div>
        <div class="detail-placeholder"></div>
    </div>

    <!-- Bottom Section (Call to Action Button) -->
    <div class="bottom-section">
        <button class="action-button" @click="startChat">发起聊天</button>
    </div>
</div>
</template>

<script>
export default {
    props: {
        username: {
            type: String,
            required: true
        },
        details: {
            type: String,
            required: true
        }
    },
    methods: {
        selectUser() {
            this.$emit("select-user");
        },
        startChat() {
            this.$emit("start-chat");
        }
    }
}
</script>

<style scoped>
.user-card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 100%;
    background-color: #f5f5f5;
}

.top-section {
    background-color: #f0eff5;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    position: relative;
}

.avatar-placeholder {
    width: 100px;
    height: 100px;
    background-color: #e0e0e0;
    border-radius: 50%;
    margin-bottom: 10px;
}

.user-info {
    text-align: center;
    margin-bottom: 20px;
}

.username {
    font-size: 20px;
    font-weight: bold;
    color: white;
}

.user-details {
    font-size: 16px;
    color: white;
}

.select-button {
    position: absolute;
    right: 20px;
    top: 20px;
    padding: 10px 20px;
    background-color: #4186ff;
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 14px;
}

.middle-section {
    flex-grow: 1;
    background-color: white;
    padding: 20px;
}

.content-placeholder,
.detail-placeholder {
    background-color: #ccc;
    height: 20px;
    margin-bottom: 15px;
    border-radius: 4px;
}

.detail-placeholder {
    height: 16px;
}

.bottom-section {
    padding: 20px;
    background-color: white;
}

.action-button {
    width: 100%;
    padding: 15px 0;
    background-color: #4186ff;
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 16px;
}
</style>
