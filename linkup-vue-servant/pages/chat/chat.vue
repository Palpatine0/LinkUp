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
        <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh">
            <div v-for="(contact, index) in dataList" :key="contact.id">
                <div
                    @click="contactRedirect(contact.id)"
                    class="contact-item"
                >
                    <img :src="contact.avatar" alt="contact.name" class="avatar">
                    <div class="info">
                        <h2 class="name">{{ contact.nickname }}</h2>
                        <p class="contact-number">{{ contact.lastMessage }}</p>
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
export default {
    data() {
        return {
            isUserLogin: false,
            userId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
            dataList: [],
        };
    },
    onShow() {
        this.isUserLogin = uni.getStorageSync(getApp().globalData.data.userLoginKey) == true ? true : false;
        if(this.isUserLogin) {
            this.reload();
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
                servantId: uni.getStorageSync(getApp().globalData.data.userInfoKey).id,
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
            if(this.loading || !this.hasMore) return
            this.loading = true;
            const {url, method, data} = this.buildApiParams();

            uni.request({
                url: url,
                method: method,
                data: data,
                success: (res) => {
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

.info .name {
    font-size: 16px;
    font-weight: bold;
    margin: 0;
}

.info .contact-number {
    font-size: 14px;
    color: #666;
}
</style>