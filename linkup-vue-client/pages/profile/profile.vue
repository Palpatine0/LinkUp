<template>
<div class="page" style="background-color: #f3f2f6;">
    <!-- Profile Section -->
    <div v-if="isUserLoggedIn" class="mb-4 center-h">
        <div class="profile-header">
            <img :src="user.avatar" alt="Profile Photo" class="profile-photo"/>
            <img v-if="isUserVerified" style="width: 25px;height: 25px;position: absolute;margin-top: 74px;margin-left: -24px;" :src="app.globalData.data.ossIconRequestUrl+'/page/profile/badge-check.svg'" @click="widgetToggle"/>
            <h1 class="profile-name">{{ user.nickname }}</h1>
            <h3 class="hidden" style="color: #8B8B8B" @click="$common.addToClipboard(user.identifier)">ID: {{ user.identifier }}</h3>
        </div>
    </div>
    <div v-if="!isUserLoggedIn" class="mb-4">
        <div class="profile-header center-h">
            <div class="center-h">
                <img :src="app.globalData.data.ossIconRequestUrl+'/page/profile/logo.jpg'" alt="Profile Photo" class="profile-photo"/>
            </div>
            <div class="profile-info center-h">
                <app-button shaped size="small" width="120px" @click="signIn">
                    {{ $t('profile.signIn') }}
                </app-button>
            </div>
        </div>
    </div>

    <app-container class="justify-SB" v-if="isUserLoggedIn&&!isUserVerified" color="#2676f7" col="12" style="margin-top: -20px" @click="realNameAuthenticationRedirect">
        <app-title bold style="color: #FFF">{{ $t('profile.authRequest') }}</app-title>
        <app-button size="small" color="#FFF" font-color="#2676f7" shaped bold>{{ $t('pub.button.getStarted') }}</app-button>
    </app-container>

    <app-container v-if="isUserLoggedIn" color="#fff" col="12" @click="profileRedirect">
        <img :src="app.globalData.data.ossIconRequestUrl+'/page/profile/profile.png'" alt="" class="link-icon">
        <span class="link-text">{{ $t('profile.profile') }}</span>
    </app-container>


    <!-- Other Options with Icons -->
    <app-container v-if="isUserLoggedIn" color="#fff" col="12" type="list">
        <div v-for="(item, index) in linkItemsB" :key="index">
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

    <div v-if="isUserLoggedIn" class="sign-out-button app-button" @click="signOut">{{ $t('profile.signOut') }}</div>
    <RealNameAuthentication v-if="realNameAuthenticationVisible"/>
    <app-widget ref="appWidget" :widget="appWidget"/>
</div>
</template>

<script>
import $common from "../../utils/common";
import app from "../../App.vue";

export default {
    computed: {
        $common() {
            return $common
        },
        app() {
            return app
        }
    },
    data() {
        return {
            isUserLoggedIn: false,
            isUserVerified: false,
            user: {},


            linkItemsB: [
                {label: "profile.balance", icon: app.globalData.data.ossIconRequestUrl + "/page/profile/card.png", click: "balanceRedirect"},
                {label: "profile.address", icon: app.globalData.data.ossIconRequestUrl + "/page/profile/addr.jpg", click: "addressRedirect"},
                {label: "profile.myFavorite", icon: app.globalData.data.ossIconRequestUrl + "/page/profile/bookmark.jpg", click: "favoritesRedirect"},
                // {label: "profile.security", icon: app.globalData.data.ossIconRequestUrl + "/page/profile/lock-keyhole.jpg", click: "securityRedirect"},
                // {label: "profile.tag", icon: app.globalData.data.ossIconRequestUrl+"/page/profile/tag.svg", click: "tagsRedirect"},
            ],
            linkItemsC: [
                {label: "profile.data", icon: app.globalData.data.ossIconRequestUrl + "/page/profile/db.jpg", click: "dataRedirect"},
                {label: "pub.lang.curLang", icon: app.globalData.data.ossIconRequestUrl + "/page/profile/globe.jpg", click: "languageSelector"},
            ],

            appWidget: {
                visible: false,
                title: this.$t('profile.appWidget.title'),
                content: this.$t('profile.appWidget.content'),
            },

            realNameAuthenticationVisible: false
        };
    },
    async onShow() {
        this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey)
        this.isUserLoggedIn = this.$common.isUserLoggedIn()
        this.isUserVerified = this.$common.isUserVerified()
    },
    methods: {
        handleLinkClick(methodName) {
            if(this[methodName] && typeof this[methodName] === 'function') {
                this[methodName]();
            } else {
                console.warn(`Method ${methodName} is not defined.`);
            }
        },

        async signIn() {
            uni.showLoading({title: this.$t('pub.showLoading.loading')});
            await getApp().globalData.signIn()
            this.user = uni.getStorageSync(getApp().globalData.data.userInfoKey)
            this.isUserLoggedIn = this.$common.isUserLoggedIn()
            uni.hideLoading();
        },
        async signOut() {
            await getApp().globalData.signOut()
            this.user = {}
            this.isUserLoggedIn = false
            this.$webSocket.closeWebSocket()
            uni.switchTab({
                url: '/pages/profile/profile'
            });
        },

        languageSelector() {
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
                fail: function(res) {
                }
            });
        },

        // Toggle
        widgetToggle() {
            this.$refs.appWidget.open();
        },

        // Redirects
        profileRedirect() {
            uni.navigateTo({
                url: './profile/profile',
            });
        },
        balanceRedirect() {
            uni.navigateTo({
                url: './balance/balance',
            });
        },
        addressRedirect() {
            uni.navigateTo({
                url: './address/address',
            });
        },
        dataRedirect() {
            uni.navigateTo({
                url: './data/data',
            });
        },
        securityRedirect() {
            uni.navigateTo({
                url: './security/security',
            });
        },
        realNameAuthenticationRedirect() {
            uni.navigateTo({
                url: './real-name-authentication/real-name-authentication',
            });
        }
    }
};
</script>

<style scoped>
.profile-header {
    text-align: center;
    justify-content: space-between;
}

.profile-photo {
    width: 100px;
    height: 100px;
    border-radius: 50%;
}

.profile-info {
    width: 100vw;
}

.profile-name {
    font-size: 20px;
    font-weight: bold;
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