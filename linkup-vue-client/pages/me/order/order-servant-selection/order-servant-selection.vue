<template>
<div class="page">
    <app-title type="h1" bold="true">选择你的服务者类型</app-title>
    <div v-for="item in servantTypeList" :key="item.id">
        <app-container color="#f3f2f6" col="12" @click="orderInitiateRedirect(item.id)">
            <div style="height: 100px">
                <app-title type="h1" style="position: relative;bottom: -55px">{{ item.name }}</app-title>
            </div>
        </app-container>
    </div>
</div>
</template>

<script>
export default {
    name: "order-servant-selection",
    data() {
        return {
            servantTypeList: {},
        }
    },
    onLoad() {
        this.getServantTypeList()
    },
    methods: {
        getServantTypeList() {
            uni.request({
                url: getApp().globalData.requestUrl + '/service-type/search',
                method: 'POST',
                data: {

                },
                success: (res) => {
                    this.servantTypeList = res.data.serviceTypeList
                },
            });
        },
        orderInitiateRedirect(servantType) {
            uni.navigateTo({
                url: '/pages/me/order/order-initiate/order-initiate?servantType=' + servantType
            });
        },

    }
}
</script>

<style scoped>

</style>