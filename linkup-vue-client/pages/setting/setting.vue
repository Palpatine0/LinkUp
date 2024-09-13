<template>
<div class="page" style="background-color: #f3f2f6;">
    <!-- Profile Section -->
    <div class="profile-section">
        <div class="profile-header center_h">
            <div class="center_h">
                <img :src="user.avatar" alt="Profile Photo" class="profile-photo" />
            </div>
            <div class="profile-info">
                <h1 class="profile-name">{{ user.name }}</h1>
            </div>
        </div>
    </div>

    <!-- Other Options with Icons -->
    <app-container color="#fff" col="12"  >
        <div v-for="(item, index) in linkItems" :key="index" class="link-item" :class="{ 'no-border': index === linkItems.length - 1 }">
            <img :src="item.icon" alt="" class="link-icon">
            <span>{{ item.label }}</span>
        </div>
    </app-container>

</div>
</template>

<script>
export default {
    data() {
        return {
            user: {
                name: "Sheev Palpatine",
                phone: "+86 191 2438 0336",
                username: "@PJN979380638",
                avatar: "/static/chat/profile-circled-test.png",
            },
            linkItems: [
                { label: "余额", icon: "/static/setting/card.svg" },
                { label: "收藏", icon: "/static/setting/bookmark.svg" },
                { label: "标签", icon: "/static/setting/tag.svg" },
                { label: "数据", icon: "/static/setting/data.svg" },
            ]
        };
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
    margin-left: 15px;
}

.profile-name {
    font-size: 20px;
    font-weight: bold;
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
    border-bottom: none;  /* Remove the border for the last item */
}

.link-icon {
    width: 20px;
    height: 20px;
    margin-right: 18px;
    position: relative;
    top: 4px;
}
</style>