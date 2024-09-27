<template>
<div class="page">
    <app-title type="h1" bold="true">{{$t('profile>order>orderServantSelection.selectServantType')}}</app-title>
    <div v-for="item in servantTypeList" :key="item.id">
        <app-container color="#f3f2f6" col="12" @click="orderInitiateRedirect(item.id,item.name)">
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
                url: getApp().globalData.data.requestUrl + '/service-type/search',
                method: 'POST',
                data: {},
                success: (res) => {
                    this.servantTypeList = res.data.list
                },
            });
        },
        orderInitiateRedirect(serviceType, serviceName) {
            uni.navigateTo({
                url: '/pages/profile/order/order-initiate/order-initiate?serviceType=' + serviceType + '&serviceName=' + serviceName
            });
        },

    }
}
</script>

<style scoped>

</style>