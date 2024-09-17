<template>
<div class="user-card">
    <!-- Top Section with Image/Background -->
    <div class="top-section" :style="{'height': topSectionHeight + 'vh', 'background-image': `url(${user.avatar})`}">
        <div class="user-info">
            <p class="username">{{ user.nickname }}</p>
            <div class="flex" style="margin: 3px 0 30px -6px">
                <div class="gender-icon-wrapper">
                    <div v-if="user.gender==0">
                        <img class="gender-icon" src="/static/miscellaneous/male.svg" style="margin: 1px 0 0 10px !important;">
                    </div>
                    <div v-else>
                        <img class="gender-icon" src="/static/miscellaneous/female.svg" style="margin: 1px 0 0 10px !important;">
                    </div>
                </div>
                <app-title type="h3" bold="true" style="color: white;position: relative;left: 10px;top: -2px;">{{ user.age }}</app-title>
            </div>
        </div>
    </div>

    <!-- Middle Section (Details/Description) -->
    <scroll-view
        :scroll-top="0"
        scroll-y="true"
        @scroll="onScroll"
        :style="{'height': scrollViewHeight + 'vh'}"
        class="mt-4">
        <div class="middle-section">
            <div class="detail-placeholder" v-for="n in 50" :key="n"></div>
        </div>
    </scroll-view>

    <!-- Button Section (Fixed at Bottom) -->
    <div class="action-button app-button" @click="contactRedirect">发起聊天</div>
</div>
</template>

<script>
export default {
    data() {
        return {
            topSectionHeight: 46, // Initial height of the top section
            scrollViewHeight: 54, // Initial height of the scroll view
            maxScroll: 30, // Maximum amount the top section can shrink
        };
    },
    props: {
        user: {
            type: Object,
            required: true
        },
        userServant: {
            type: Object,
            required: true
        }
    },
    methods: {
        onScroll(event) {
            const scrollTop = event.target.scrollTop;
            const maxScrollDistance = this.maxScroll; // Define how much the top section should shrink

            // Calculate the new height for the top section and scroll view
            let newTopHeight = 46 - (scrollTop / 10); // Divide by 10 to slow down the shrink speed
            let newScrollViewHeight = 54 + (scrollTop / 10);

            // Set limits for the height so they don't go beyond a certain point
            if (newTopHeight < 14) {
                newTopHeight = 14; // Minimum top section height
            }
            if (newScrollViewHeight > 76) {
                newScrollViewHeight = 76; // Maximum scroll view height
            }

            // Update the heights
            this.topSectionHeight = newTopHeight;
            this.scrollViewHeight = newScrollViewHeight;
        },
        contactRedirect() {
            this.$emit("contactRedirect");
        }
    }
}
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
}

.user-info {
    position: absolute;
    bottom: 10px;
    left: 20px;
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
    width: 40px;
    height: 22px;
}
</style>