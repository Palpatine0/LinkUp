<template>
<div class="page" style="background-color: #f3f2f6;">
    <!-- Profile Section -->
    <div v-if="isUserLogin" class="profile-section">
        <div class="profile-header center-h">
            <div class="center-h">
                <img :src="user.avatar" alt="Profile Photo" class="profile-photo"/>
            </div>
            <div class="profile-info">
                <h1 class="profile-name">{{ user.nickname }}</h1>
            </div>
        </div>
    </div>
    <div v-if="!isUserLogin" class="profile-section">
        <div class="profile-header center-h">
            <div class="center-h">
                <img src="/static/page/profile/logo.jpg" alt="Profile Photo" class="profile-photo"/>
            </div>
            <div class="profile-info">
                <app-button shaped size="very-small" @click="signIn">
                    {{ $t('profile.signIn') }}
                </app-button>
            </div>
        </div>
    </div>

    <!-- Other Options with Icons -->
    <app-container v-if="isUserLogin" color="#fff" col="12" type="list">
        <div
            v-for="(item, index) in linkItemsB"
            :key="index"
        >
            <div class="link-item" @click="handleLinkClick(item.click)">
                <img :src="item.icon" alt="" class="link-icon">
                <span class="link-text">{{ $t(item.label) }}</span>
            </div>
            <!-- Separator div instead of border-bottom -->
            <div v-if="index !== linkItemsB.length - 1" class="separator"></div>
        </div>
    </app-container>
    <app-container color="#fff" col="12" type="list">
        <div
            v-for="(item, index) in linkItemsC"
            :key="index"
        >
            <div class="link-item" @click="handleLinkClick(item.click)">
                <img :src="item.icon" alt="" class="link-icon">
                <span class="link-text">{{ $t(item.label) }}</span>
            </div>
            <!-- Separator div instead of border-bottom -->
            <div v-if="index !== linkItemsC.length - 1" class="separator"></div>
        </div>
    </app-container>

    <div v-if="isUserLogin" class="sign-out-button app-button" @click="signOut">{{ $t('profile.signOut') }}</div>

</div>
</template>


<script>
import app from "../../App.vue";

export default {
    data() {
        return {
            isUserLogin: false,
            user: {},
            linkItemsB: [
                {label: "profile.balance", icon: "/static/page/profile/card.png", click: "balanceRedirect"},
                {label: "profile.address", icon: "/static/page/profile/addr.jpg", click: "addressRedirect"},
                {label: "profile.myFavorite", icon: "/static/page/profile/bookmark.jpg", click: "favoritesRedirect"},
                // {label: "profile.tag", icon: "/static/page/profile/tag.svg", click: "tagsRedirect"},
            ],
            linkItemsC: [
                {label: "profile.data", icon: "/static/page/profile/db.jpg", click: "dataRedirect"},
                {label: "pub.lang.curLang", icon: "/static/page/profile/globe.jpg", click: "languageToggle"},
                // {label: "profile.tag", icon: "/static/page/profile/tag.svg", click: "tagsRedirect"},
            ],
        };
    },
    onLoad() {
        this.user = uni.getStorageSync(app.globalData.data.userInfoKey)
        this.isUserLogin = uni.getStorageSync(app.globalData.data.userLoginKey)
    },
    methods: {
        handleLinkClick(methodName) {
            if (this[methodName] && typeof this[methodName] === 'function') {
                this[methodName]();
            } else {
                console.warn(`Method ${methodName} is not defined.`);
            }
        },

        async signIn() {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            await getApp().globalData.signIn()
            this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey)
            this.isUserLogin = uni.getStorageSync(getApp().globalData.data.userLoginKey)
            uni.hideLoading();
        },
        async signOut() {
            await getApp().globalData.signOut()
            this.user = {}
            this.isUserLogin = false
            uni.switchTab({
                url: '/pages/profile/profile'
            });
        },

        languageToggle() {
            uni.showActionSheet({
                itemList: [
                    this.$t('pub.lang.en'),
                    this.$t('pub.lang.zh'),
                ],
                success: (res) => {
                    uni.navigateTo({
                        url: '/pages/profile/profile'
                    });
                    const language = [
                        {text: '英文', code: 'en'},
                        {text: '中文简体', code: 'zh-Hans'},
                    ]
                    uni.setStorageSync('language', language[res.tapIndex].code)
                    this.$i18n.locale = language[res.tapIndex].code
                    uni.setLocale()
                },
                fail: function (res) {
                }
            });
        },
        orderRedirect() {
            uni.navigateTo({
                url: '/pages/profile/order/order',
            });
        },
        balanceRedirect() {
            uni.navigateTo({
                url: '/pages/profile/balance/balance',
            });
        },
        addressRedirect() {
            uni.navigateTo({
                url: '/pages/profile/address/address',
            });
        },
        dataRedirect() {
            uni.navigateTo({
                url: '/pages/profile/data/data',
            });
        }
    }
};
</script>

<style scoped>
.profile-section {
    margin-bottom: 40px;
}

.profile-header {
    align-items: center;
    justify-content: space-between;
}

.profile-photo {
    width: 100px;
    height: 100px;
    border-radius: 50%;
}

.profile-info {
}

.profile-name {
    font-size: 20px;
    font-weight: bold;
    text-align: center;
}

.link-item {
    padding: 10px 0;
}

.link-text {
    position: relative;
    top: -6px;
}

.link-icon {
    width: 28px;
    height: 28px;
    margin-right: 18px;
    position: relative;
    top: 2px;
}

.sign-out-button {
    background-color: white;
    color: red;
}

.separator {
    height: 1px;
    background-color: #e5e5e5;
    margin-left: 42px;
    margin-right: 22px;
}

</style>