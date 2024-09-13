<template>
<div class="page">
    <app-title type="h1" bold="true">选择你的服务者类型</app-title>
    <span @click="orderInitiateRedirect(1)">
    <app-container color="#f3f2f6" col="12">
        导游
    </app-container>
    </span>
</div>
</template>

<script>
export default {
    name: "order-servant-selection",
    data() {
        return {}
    },
    methods: {
        orderInitiateRedirect(servantType) {
            console.log("orderInitiateRedirect(servantType) {")
            uni.navigateTo({
                url: '/pages/order/order-initiate/order-initiate?servantType=' + servantType
            });
        },
    }
}
</script>

<style scoped>

</style>