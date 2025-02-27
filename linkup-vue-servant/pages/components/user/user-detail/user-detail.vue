<template>
<div style="height: 100vh;">
    <div class="user-card">
        <!-- Top Section with Image/Background -->
        <div class="top-section" :style="{'height': topSectionHeight + 'vh', 'background-image': `url(${user.avatar})`}">
            <!-- Gradient Overlay -->
            <div class="gradient-overlay"></div>

            <div class="user-info">
                <p class="username">{{ user.nickname }}</p>
                <div class="flex" style="margin: 3px 0 30px -6px">
                    <div class="gender-icon-wrapper">
                        <div v-if="user.gender == 0">
                            <div class="gender-icon">👨‍💻</div>
                        </div>
                        <div v-else>
                            <div class="gender-icon">👩‍💻</div>
                        </div>
                    </div>
                    <app-title type="h3" bold="true" style="color: white; position: relative; left: 10px; top: -2px;">
                        {{ user.age }}
                    </app-title>
                </div>
            </div>
        </div>

        <!-- Middle Section (Details/Description) -->
        <scroll-view :scroll-top="0" scroll-y="true" @scroll="onScroll" :style="{'height': scrollViewHeight + 'vh'}" class="mt-4">
            <div class="middle-section">
                <div class="detail-placeholder" v-for="n in 50" :key="n"></div>
            </div>
        </scroll-view>

        <!-- Button Section (Fixed at Bottom) -->
        <div v-if="showChatBtn" class="fix-bottom">
            <div class="app-button" @click="chatWindowRedirect">{{ $t('componentPage>user>userDetail.startChatting') }}</div>
        </div>
    </div>
</div>
</template>

<script>
export default {
    data() {
        return {
            userId: '',
            user: {},
            userServant: {},

            topSectionHeight: 66,
            scrollViewHeight: 24,
            maxScroll: 30,

            showChatBtn: true
        };
    },
    onLoad(params) {
        this.userId = params.userId;
        if(!this.$common.isEmpty(params.showChatBtn)) {
            this.showChatBtn = params.showChatBtn == "false" ? false : true;
        }
        this.getUser();
        this.getUserServant();
    },
    methods: {
        // Fetch user details
        getUser() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.user.search,
                method: 'POST',
                data: {
                    id: this.userId
                },
                success: (res) => {
                    this.user = res.data.list[0];
                }
            });
        },

        // Fetch servant details
        getUserServant() {
            uni.request({
                url: getApp().globalData.data.requestUrl + this.$API.userServant.search,
                method: 'POST',
                data: {
                    userId: this.userId
                },
                success: (res) => {
                    this.userServant = res.data.list[0];
                }
            });
        },

        onScroll(event) {
            const scrollTop = event.target.scrollTop;
            let newTopHeight = 66 - (scrollTop / 10);
            let newScrollViewHeight = 24 + (scrollTop / 10);

            if(newTopHeight < 25) {
                newTopHeight = 25;
            }
            if(newScrollViewHeight > 75) {
                newScrollViewHeight = 75;
            }
            this.topSectionHeight = newTopHeight;
            this.scrollViewHeight = newScrollViewHeight;
        },

        chatWindowRedirect() {
            uni.navigateTo({
                url: '/pages/components/chat/chat-window/chat-window?contactId=' + this.userId
            });
        },
    },
};
</script>

<style scoped>
.user-card {
    display: flex;
    flex-direction: column;
    height: 100vh;
    position: relative;
}

.top-section {
    position: relative;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    display: flex;
    transition: height 0.1s ease-out; /* Smooth transition */
    border-bottom-left-radius: 25px;
    border-bottom-right-radius: 25px;
    overflow: hidden; /* Ensure gradient doesn't overflow */
    min-height: 25vh; /* Ensure it never shrinks beyond a certain height */
}

.gradient-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 40%;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.7)); /* Gradient */
    z-index: 1; /* Make sure it's above the background */
}

.user-info {
    position: absolute;
    bottom: 0;
    padding-left: 20px;
    width: 100%;
    z-index: 2; /* Make sure user info is above the gradient */
}

.username {
    font-size: 30px;
    font-weight: bold;
    color: white;
}

.middle-section {
    flex-grow: 1;
    background-color: white;
    padding: 20px;
}

.detail-placeholder {
    background-color: #ccc;
    height: 16px;
    margin-bottom: 15px;
    border-radius: 4px;
}

.action-button {
    position: fixed;
    bottom: 20px;
    left: 20px;
    right: 20px;
    padding: 10px 30px;
    cursor: pointer;
    text-align: center;
    z-index: 100;
    width: auto;
}

.gender-icon-wrapper {
    background-color: white;
    border-radius: 15px;
    width: 42px;
    height: 22px;
}

.gender-icon {
    margin: -4px 0 0 10px;
    font-size: 20px;
}
</style>