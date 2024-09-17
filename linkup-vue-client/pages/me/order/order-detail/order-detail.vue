<template>
<div class="page" style="background-color: #f3f2f6">
    <!-- Title -->
    <app-title type="h1" bold="true">{{ order.title }}</app-title>

    <!-- Price and Respondent Container -->
    <div class="app-container" style="background-color: white !important;">
        <div class="price-respondent-container">
            <!-- Price Section -->
            <div class="price-section">
                <app-title bold="true">报价</app-title>
                <p>{{ order.price }} ¥</p>
            </div>

            <!-- Divider -->
            <div class="divider"></div>

            <!-- Respondent Section -->
            <div class="respondent-section">
                <app-title bold="true">已抢单</app-title>
                <p>{{ order.candidateCount }} 人</p>
            </div>
        </div>
    </div>


    <!-- Respondent Users Title -->
    <div class="mt-4">
        <app-title bold="true" >已抢单用户</app-title>
        <div v-if="servantList.length>0">
            <z-swiper v-model="servantList" :options="{slidesPerView : 'auto',centeredSlides: true,spaceBetween: 14}" style="width: 100%">
                <z-swiper-item v-for="(user,index) in servantList" :key="index" :custom-style="{width:'500rpx'}">
                    <demo-item :item="user">
                        <app-container color="#fff" col="12" @click="userDetailRedirect(user.id)">
                            <div class="center_h" >
                                <image style="width: 160px; height: 160px;border-radius: 50%;margin: 30px 0 30px 0" :src="user.avatar" mode="aspectFill"></image>
                            </div>
                            <app-title type="h3" bold="true">{{ user.nickname }}</app-title>
                            <div class="flex" style="margin: 3px 0 30px -6px">
                                <div v-if="user.gender==0">
                                    <img class="gender-icon" src="/static/page/me/order/male.png">
                                </div>
                                <div v-else>
                                    <img class="gender-icon" src="/static/page/me/order/female.png">
                                </div>
                                <app-title type="h3" bold="true">{{ user.age }}</app-title>
                            </div>
                            <p style="margin-bottom: 10px"> {{ user.servantData.bio }}</p>
                        </app-container>
                    </demo-item>
                </z-swiper-item>
            </z-swiper>
        </div>
        <div v-else>
            <div class="info">
                暂时无人抢单
            </div>
        </div>
    </div>



</div>
</template>

<script>
export default {
    components: {},
    data() {
        return {
            orderId: '',
            order: {},
            servantList: [],

            slideCustomStyle: {
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                borderRadius: '36rpx'
            },
            options: {
                effect: 'cards'
            },
        };
    },
    onLoad(params) {
        this.orderId = params.orderId;
        this.getOrder();
        this.getServantList();
    },
    methods: {
        getOrder() {
            uni.request({
                url: getApp().globalData.requestUrl + '/order/search',
                method: 'POST',
                data: {
                    id: this.orderId
                },
                success: (res) => {
                    this.order = res.data.orderList[0]
                },
            });
        },
        getServantList() {
            uni.request({
                url: getApp().globalData.requestUrl + '/order-candidate/get-servants',
                method: 'POST',
                data: {
                    orderId: this.orderId
                },
                success: (res) => {
                    this.servantList = res.data.servantList;

                    // Fetch servantData for all users in parallel
                    const promises = this.servantList.map((user) => {
                        return new Promise((resolve) => {
                            uni.request({
                                url: getApp().globalData.requestUrl + '/user-servant/search',
                                method: 'POST',
                                data: {
                                    userId: user.id
                                },
                                success: (res) => {
                                    user.servantData = res.data.userServantList[0];
                                    resolve();
                                }
                            });
                        });
                    });

                    // Wait for all servantData to be fetched
                    Promise.all(promises).then(() => {
                        console.log("All servantData fetched", this.servantList);
                        // Trigger Vue to re-render with updated servantData
                        this.$forceUpdate();
                    });
                },
            });
        },

        userDetailRedirect(userId) {
            uni.navigateTo({
                url: '/pages/home/user-detail/user-detail?userIdId=' + userId,
            })
        }
    }

};
</script>

<style scoped>
/* Container for Price and Respondent Count */
.price-respondent-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: center;
}

/* Sections for Price and Respondent Count */
.price-section, .respondent-section {
    text-align: left;
    width: 45%;
}

.divider {
    height: 40px;
    width: 1px;
    background-color: #ddd;
    margin: 0 10px;
}

/* User Item */
.user-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #ddd;
}

.user-item.no-border {
    border-bottom: none;
}
</style>