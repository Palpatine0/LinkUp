<template>
<div class="page">
    <app-title type="h1" bold="true">{{ $t('order>orderServantSelection.selectServantType') }}</app-title>
    <div v-for="item in servantTypeList" :key="item.id">
        <div class="app-container service-info" :style="{backgroundImage: `url(${item.coverImg})`, backgroundRepeat: 'no-repeat',backgroundPosition: 'center center',backgroundSize: '135%'}"  @click="orderInitiateRedirect(item.id,item.name,item.nameCn)">
            <div class="gradient-overlay" style="border-bottom-right-radius: 15px;border-bottom-left-radius: 15px"></div>
            <div class="service-info-text">
                {{  language != "zh-Hans" ? item.name : item.nameCn }}
            </div>
        </div>
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
.service-info {
    height: 160px;
    position: relative;
}

.service-info-text {
    position: absolute;
    bottom: 40px;
    width: 100%;
    z-index: 100;
    height: 20px;
    color: white;
    font-size: 35px;
    font-weight: bold;
}
</style>