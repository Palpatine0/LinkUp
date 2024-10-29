<template>
<div class="chat-header">
    <img :src="avatar" class="avatar" @click="$parent.userDetailRedirect(uid)"/>
    <span class="username">{{ username }}</span>

    <app-button v-if="!canSendMessage" shaped size="small" @click="handleClick('chatNow')"  >
        {{ $t('componentPage>chat>chatWindow.chatNow') }}
    </app-button>

    <app-button v-else shaped size="small" @click="handleClick('keepChatting')"  >
        {{ $t('componentPage>chat>chatWindow.keepChatting') }}
    </app-button>
</div>
</template>

<script>
export default {
    props: {
        randomNum: {type: String},
        username: {type: String, default: 'Unknown'},
        uid: {type: String},
        avatar: {type: String, default: '/static/chat/profile-circled-test.png'},
    },
    data() {
        return {
            canSendMessage: false,
        };
    },
    mounted() {
        this.$bus.$on(
            'updateCanSendMessage' + this.randomNum,
            (status) => {
                this.canSendMessage = status;
            }
        );
    },
    methods: {
        handleClick(action) {
            if(action === 'chatNow' || action === 'keepChatting') {
                this.$bus.$emit(
                    'giftToggle' + this.randomNum,
                    true
                );
            }
        },
    }
};
</script>
<style scoped>
.chat-header {
    display: flex;
    align-items: center;
    padding: 10px 20px 10px 21px;
    background-color: #f3f2f6;
    height: 42px;
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
