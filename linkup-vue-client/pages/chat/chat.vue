<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">{{ $t('chat.chats') }}</app-title>
    </div>

    <div v-if="!isUserLogin" class="center-h">
        <div class="background-icon">
            <img src="/static/page/chat/messages.svg">
        </div>
        <div style="margin-top: -40px;">
            <app-button shaped @click="signIn">{{ $t('chat.signIn') }}</app-button>
        </div>
    </div>
    <div v-if="isUserLogin">
        <!-- Search input -->
        <app-input mode="text" :placeholder="$t('pub.page.search')" col="12" class="mb-2"/>
        <!-- Contact List -->
        <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh" @scrolltoupper="reload" @scrolltolower="onReachBottom">
            <div v-for="(contact, index) in dataList" :key="contact.id">
                <div class="contact-item" @click="contactRedirect(contact.id)">
                    <div style="width: 50px;" class="mr-2 flex">
                        <div style="width: 50px;"><img :src="contact.avatar" alt="contact.name" class="avatar"></div>
                        <div v-if="contact.unreadMessageCount > 0" >
                            <div class="unread-badge center">{{ contact.unreadMessageCount }}</div>
                        </div>
                    </div>
                    <div class="justify-SB" style="width: 100%;">
                        <div>
                            <h2 class="name">{{ contact.nickname }}</h2>
                            <p class="latest-msg">{{ contact.latestMessage.content }}</p>
                        </div>
                        <div>
                            <div class="latest-msg-time">{{ $common.timeToStampRecord(contact.latestMessage.createdAt) }}</div>
                        </div>
                    </div>
                </div>
                <!-- Separator div instead of border-bottom -->
                <div v-if="index !== dataList.length - 1" class="separator"></div>
            </div>
        </scroll-view>
    </div>
</div>
</template>

<script>
import $common from "../../utils/common";

export default {
    computed: {
        $common() {
            return $common
        }
    },
    data() {
        return {
            isUserLogin: false,
            userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
            dataList: [],
            messageHandler: null,
        };
    },
    onShow() {
        this.isUserLogin = uni.getStorageSync(getApp().globalData.data.userLoginKey) === true;
        if(this.isUserLogin) {
            this.$webSocket.connectWebSocket(uni.getStorageSync(getApp().globalData.data.userInfoKey).id);

            this.reload();

            this.messageHandler = this.handleWebSocketMessage.bind(this);
            this.$webSocket.addMessageHandler(this.messageHandler);
        }
    },
    onHide() {
        if(this.messageHandler) {
            this.$webSocket.removeMessageHandler(this.messageHandler);
            this.messageHandler = null;
        }
    },
    methods: {
        reload() {
            if(this.isUserLogin) {
                this.resetPagination();
                this.getDataList();
            }
        },
        buildApiParams() {
            let url = getApp().globalData.data.requestUrl + this.$API.conversation.searchContacts;
            let method = 'POST';
            let baseData = {
                clientId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
                page: this.page,
                size: this.pageSize,
            };
            let data = {};

            if(this.searchKeyword && this.searchKeyword.trim() !== '') {
                data = {
                    ...baseData,
                    keyword: this.searchKeyword,
                };
            } else {
                data = {
                    ...baseData,
                };
            }
            return {url, method, data};
        },
        onSearchInput() {
            this.reload();
        },
        getDataList() {
            console.log("START getDataList() {")
            if(this.loading || !this.hasMore) return;
            this.loading = true;
            const {url, method, data} = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
                    console.log("DONE getDataList() {")
                    const contacts = res.data.list;
                    if(this.page === 1) {
                        this.dataList = [];
                    }
                    if(contacts.length < this.pageSize) {
                        this.hasMore = false;
                    }
                    this.dataList = this.dataList.concat(contacts);

                    this.page += 1;
                },
                complete: (err) => {
                    this.loading = false;
                }
            });
        },
        handleWebSocketMessage(data) {
            if(data.type === 'contactUpdate') {
                this.updateContactList(data.data);
            }
        },
        updateContactList(updateData) {
            console.log("updateData")
            console.log(updateData)
            const contactIndex = this.dataList.findIndex(contact => contact.id == updateData.servantId);
            console.log("contactIndex")
            console.log(contactIndex)
            if (contactIndex !== -1) {
                this.$set(this.dataList[contactIndex], 'latestMessage', {
                    content: updateData.latestMessage,
                });
                this.$set(this.dataList[contactIndex], 'messageTime', updateData.messageTime);
                this.$set(this.dataList[contactIndex], 'unreadMessageCount', updateData.unreadMessageCount);
            } else {
                const newContact = {
                    id: updateData.conversationId,
                    nickname: updateData.nickname || "New Contact",
                    avatar: updateData.avatar || "/path/to/default-avatar.jpg",
                    latestMessage: {
                        content: updateData.latestMessage,
                    },
                    messageTime: updateData.messageTime,
                    unreadMessageCount: updateData.unreadMessageCount,
                };
                this.dataList.unshift(newContact);
            }
        },

        async signIn() {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            await getApp().globalData.signIn()
            this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey)
            this.isUserLogin = uni.getStorageSync(getApp().globalData.data.userLoginKey)
            uni.hideLoading();
        },

        // Redirects
        contactRedirect(contactId) {
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
}

.separator {
    height: 1px;
    background-color: #ccc;
    margin-left: 68px;
    width: 70vw;
}

.avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.name {
    font-size: 16px;
    font-weight: bold;
    margin: 0;
}

.latest-msg {
    font-size: 14px;
    color: #666;
}

.latest-msg-time {
    font-size: 12px;
    color: gray;
}

.unread-badge {
    background-color: #2676f7;
    color: white;
    border-radius: 50%;
    font-size: 12px;
    width: 16px;
    height: 16px;
    border: 2px solid;
    position: relative;
    top: 36px;
    left: -16px;
}
</style>