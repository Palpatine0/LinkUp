<template>
<div class="page">
    <app-title type="h1" bold="true">{{ $t('profile>order>orderServantSelection.selectServantType') }}</app-title>
    <div v-for="item in servantTypeList" :key="item.id">
        <app-container color="#f3f2f6" col="12" @click="orderInitiateRedirect(item.id,item.name,item.nameCn)">
            <div style="height: 100px">
                <app-title type="h1" style="position: relative;bottom: -55px">
                    {{  language != "zh-Hans" ? item.name : item.nameCn }}
                </app-title>
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
                url: getApp().globalData.data.requestUrl + this.$API.serviceType.search,
                method: 'POST',
                data: {},
                success: (res) => {
                    this.servantTypeList = res.data.list
                },
            });
        },
        orderInitiateRedirect(serviceType, serviceName, serviceNameCn) {
            uni.redirectTo({
                url: '../order-initiate/order-initiate?serviceType=' + serviceType + '&serviceName=' + serviceName + '&serviceNameCn=' + serviceNameCn,
            });
        },

    }
}
</script>

<style scoped>

</style>