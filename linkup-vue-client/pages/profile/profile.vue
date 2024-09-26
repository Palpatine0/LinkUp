<template>
<div class="page" style="background-color: #f3f2f6;">
    <!-- Profile Section -->
    <div class="profile-section">
        <div class="profile-header center_h">
            <div class="center_h">
                <img :src="user.avatar" alt="Profile Photo" class="profile-photo"/>
            </div>
            <div class="profile-info">
                <h1 class="profile-name">{{ user.nickname }}</h1>
            </div>
        </div>
    </div>

    <app-container color="#fff" col="12" @click="orderRedirect">
        <img src="/static/page/profile/order.png" alt="" class="link-icon">
        <span class="link-text">{{$t('profile.order')}}</span>
    </app-container>

    <!-- Other Options with Icons -->
    <app-container color="#fff" col="12" type="list">
        <div
            v-for="(item, index) in linkItemsB"
            :key="index"
            class="link-item"
            :class="{ 'no-border': index === linkItemsB.length - 1 }"
            @click="handleLinkClick(item.click)"
        >
            <img :src="item.icon" alt="" class="link-icon">
            <span class="link-text">{{$t(item.label)}}</span>


        </div>
    </app-container>
    <app-container color="#fff" col="12" type="list">
        <div
            v-for="(item, index) in linkItemsC"
            :key="index"
            class="link-item"
            :class="{ 'no-border': index === linkItemsC.length - 1 }"
            @click="handleLinkClick(item.click)"
        >
            <img :src="item.icon" alt="" class="link-icon">
            <span class="link-text">{{$t(item.label)}}</span>
        </div>
    </app-container>
</div>
</template>


<script>
export default {
    data() {
        return {
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
        this.getUser();
    },

    methods: {
        getUser() {
            uni.request({
                url: getApp().globalData.requestUrl + '/user/search',
                method: 'POST',
                data: {
                    id: uni.getStorageSync('userId')
                },
                success: (res) => {
                    this.user = res.data.list[0]
                },
            });
        },

        handleLinkClick(methodName) {
            if (this[methodName] && typeof this[methodName] === 'function') {
                this[methodName]();
            } else {
                console.warn(`Method ${methodName} is not defined.`);
            }
        },

        languageToggle(){
            uni.showActionSheet({
                itemList: [
                    this.$t('pub.lang.en'),
                    this.$t('pub.lang.zh'),
                ],
                success:  (res)=>{
                    uni.navigateTo({
                        url: '/pages/profile/profile'
                    });
                    const language = [
                        {text:'英文',code:'en'},
                        {text:'中文简体',code:'zh-Hans'},
                    ]
                    uni.setStorage({key:'language',data:language[res.tapIndex].code})
                    this.$i18n.locale = language[res.tapIndex].code
                    uni.setLocale(language[res.tapIndex].code)
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

.settings-links {
    background-color: white;
    padding: 0px 18px 0px 18px;
    border-radius: 10px;
}

.link-item {
    padding: 10px 0;
    border-bottom: 1px solid #e5e5e5;
}

.link-item.no-border {
    border-bottom: none; /* Remove the border for the last item */
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
</style>