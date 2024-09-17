<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">我的消息</app-title>
    </div>

    <!-- Search input -->
    <app-input mode="text" placeholder="搜索" col="12" class="mb-2"/>

    <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh" class="mt-4">
        <div
            v-for="(contact, index) in contacts"
            :key="contact.id"
            :class="{'contact-item': true, 'last-item': index === contacts.length - 1}"
            @click="contactRedirect(contact.id)"
        >
            <img :src="contact.avatar" alt="contact.name" class="contact-avatar">
            <div class="contact-info">
                <h2 class="contact-name">{{ contact.name }}</h2>
                <p class="contact-number">{{ contact.number }}</p>
            </div>
        </div>
    </scroll-view>
</div>
</template>

<script>
export default {
    data() {
        return {
            contacts: []
        }
    },
    methods:{
        contactRedirect(contactId){
            uni.navigateTo({
                url: '/pages/components/chat/chat-window/chat-window?contactId=' + contactId
            });
        }
    }
}
</script>

<style scoped>
.contact-item {
    display: flex;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #ccc;
}

.last-item {
    border-bottom: none;  // No border for the last item
}

.contact-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.contact-info .contact-name {
    font-size: 16px;
    font-weight: bold;
    margin: 0;
}

.contact-info .contact-number {
    font-size: 14px;
    color: #666;
}
</style>