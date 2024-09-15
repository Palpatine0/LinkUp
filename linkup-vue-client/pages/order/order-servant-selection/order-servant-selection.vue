<template>
<div class="page">
    <app-title type="h1" bold="true">选择你的服务者类型</app-title>
    <div v-for="item in servantTypeList" :key="item.id">
        <app-container color="#f3f2f6" col="12" @click="orderInitiateRedirect(item.id)">
            <app-title type="h1">{{ item.name }}</app-title>
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
                url: getApp().globalData.requestUrl + '/servant-type/search',
                method: 'POST',
                data: {

                },
                success: (res) => {
                    this.servantTypeList = res.data.servantTypeList
                },
            });
        },
        orderInitiateRedirect(servantType) {
            uni.navigateTo({
                url: '/pages/order/order-initiate/order-initiate?servantType=' + servantType
            });
        },

    }
}
</script>

<style scoped>

</style>