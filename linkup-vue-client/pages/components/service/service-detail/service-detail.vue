<template>
<div style="height: 100vh;background-color: #f6f6f6">
    <div class="user-card">
        <!-- Top Section with Image/Background -->
        <div class="top-section" :style="{'height': topSectionHeight + 'vh', 'background-image': `url(${serviceType.introImg})`}">
            <!-- Gradient Overlay -->
            <div class="gradient-overlay"></div>
            <div class="user-info">
                <p class="username">{{ serviceType.name }}</p>
            </div>
        </div>

        <!-- Tab Buttons -->
        <div class="tabs">
            <button class="tab-button" :class="{'active': activeTab === 'details'}" @click="changeTab('details')">
                <i class="fas fa-car"></i> 项目详情
            </button>
            <button class="tab-button" :class="{'active': activeTab === 'order'}" @click="changeTab('order')">
                <i class="fas fa-walking"></i> 下单须知
            </button>
        </div>
        <!-- Content Below Tabs -->
        <scroll-view :scroll-top="0" scroll-y="true" @scroll="onScroll" :style="{'height': scrollViewHeight + 'vh'}">
            <div class="middle-section">
                <!-- Project Details -->
                <div v-if="activeTab === 'details'">
                    <p>这里是项目详情的内容...</p>
                    <div class="detail-placeholder" v-for="n in 50" :key="n"></div>
                </div>

                <!-- Order Notes -->
                <div v-if="activeTab === 'order'">
                    <p>这里是下单须知的内容...</p>
                    <div class="detail-placeholder" v-for="n in 50" :key="n"></div>
                </div>
            </div>
        </scroll-view>

        <div class="action-button app-button" @click="servantListRedirect">立即下单</div>
    </div>
</div>
</template>

<script>
export default {
    data() {
        return {
            serviceTypeId: '',
            serviceType: {},
            userServant: {},
            topSectionHeight: 46,
            scrollViewHeight: 54,
            maxScroll: 30,
            activeTab: 'details'
        };
    },
    onLoad(params) {
        this.serviceTypeId = params.serviceTypeId;
        this.getServiceType();
    },
    methods: {
        getServiceType() {
            uni.request({
                url: getApp().globalData.requestUrl + '/service-type/search',
                method: 'POST',
                data: {id: this.serviceTypeId},
                success: (res) => {
                    this.serviceType = res.data.serviceTypeList[0];
                }
            });
        },

        // Change tab method
        changeTab(tab) {
            this.activeTab = tab; // Switch between 'details' and 'balance'
        },

        onScroll(event) {
            const scrollTop = event.target.scrollTop;
            let newTopHeight = 46 - (scrollTop / 10);
            let newScrollViewHeight = 54 + (scrollTop / 10);

            if (newTopHeight < 25) {
                newTopHeight = 25;
            }
            if (newScrollViewHeight > 75) {
                newScrollViewHeight = 75;
            }
            this.topSectionHeight = newTopHeight;
            this.scrollViewHeight = newScrollViewHeight;
        },

        servantListRedirect() {
            uni.navigateTo({
                url: '/pages/components/service/service-detail/servant-list/servant-list?serviceTypeId=' + this.serviceTypeId
            });
        }
    }
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
    transition: height 0.1s ease-out;
    overflow: hidden;
    min-height: 25vh;
}

.gradient-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 40%;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.7));
    z-index: 1;
}

.user-info {
    position: absolute;
    bottom: 20px;
    padding-left: 20px;
    width: 100%;
    z-index: 2;
}

.username {
    font-size: 40px;
    font-weight: bold;
    color: white;
}

/* Tabs */
.tabs {
    display: flex;
    justify-content: space-around;
}

.tab-button {
    flex: 1;
    text-align: center;
    font-size: 16px;
    cursor: pointer;
    background-color: transparent;
    border: none;
    color: #999;
    border-radius: 10px;
    margin: 10px;
}

.tab-button.active {
    background-color: #007BFF; /* Active background color (blue) */
    color: white; /* Active text color */
    border-radius: 10px;
    margin: 10px
}

.middle-section {
    flex-grow: 1;
    padding: 20px;
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

.detail-placeholder {
    background-color: #ccc;
    height: 16px;
    margin-bottom: 15px;
    border-radius: 4px;
}

</style>