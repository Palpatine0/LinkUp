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
        <span class="link-text">订单</span>
    </app-container>

    <!-- Other Options with Icons -->
    <app-container color="#fff" col="12" type="list">
        <div
            v-for="(item, index) in linkItems"
            :key="index"
            class="link-item"
            :class="{ 'no-border': index === linkItems.length - 1 }"
            @click="handleLinkClick(item.click)"
        >
            <img :src="item.icon" alt="" class="link-icon">
            <span class="link-text">{{ item.label }}</span>
        </div>
    </app-container>
</div>
</template>


<script>
export default {
    data() {
        return {
            user: {},
            linkItems: [
                {label: "我的余额", icon: "/static/page/profile/card.png", click: "balanceRedirect"},
                {label: "地址信息", icon: "/static/page/profile/addr.jpg", click: "addressRedirect"},
                {label: "我的收藏", icon: "/static/page/profile/bookmark.jpg", click: "favoritesRedirect"},
                {label: "数据与存储", icon: "/static/page/profile/db.jpg", click: "dataRedirect"},
                // {label: "标签", icon: "/static/page/profile/tag.svg", click: "tagsRedirect"},
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
                    console.log("this.user")
                    console.log(this.user)
                    console.log("res.data.list[0]")
                    console.log(res.data.list[0])
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