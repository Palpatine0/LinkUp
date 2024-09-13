<template>
<div class="page">
    <!-- Heading section -->
    <div style="display: flex; align-items: center; justify-content: space-between;">
        <app-title type="h1" bold="true">我的订单</app-title>
        <img src="/static/order/add.svg" style="width: 28px; height: 28px;" @click="orderInitiateRedirect"/>
    </div>

    <!-- Search input -->
    <app-input mode="text" placeholder="搜索" col="12" class="mb-2"/>

    <!-- Orders Container using app-container -->
    <scroll-view :scroll-top="0" scroll-y="true" style="height: 80vh" class="mt-4">
        <div class="container" v-for="order in orders" :key="order.id" @click="orderDetailRedirect(order.id)">
            <div class="order-content">
                <div style="width: 100%;">
                    <div style="display: flex; align-items: center; justify-content: space-between;">
                        <app-title bold="true" type="h3">{{ order.title }}</app-title>
                        <span :class="['status-dot', order.completeStatus === 1 ? 'green-dot' : 'red-dot']"></span>
                    </div>
                    <div class="order-info">
                        <div class="respondent-count">
                            抢单人数: {{ order.respondentCount }}
                        </div>
                        <span style="font-size: 14px;color: gray;">{{ order.createTime }}</span>
                    </div>
                </div>
            </div>
        </div>
    </scroll-view>
</div>
</template>

<script>
import orderDetail from "./order-detail/order-detail.vue";

export default {
    computed: {
        orderDetail() {
            return orderDetail
        }
    },
    data() {
        return {
            orders: [
                {id: 1, title: 'VDqef', respondentCount: 143, createTime: '2024-10-10', completeStatus: 0},
                {id: 2, title: 'Gqef', respondentCount: 89, createTime: '2024-06-19', completeStatus: 1},
                {id: 3, title: 'JGwefew', respondentCount: 89, createTime: '2024-06-19', completeStatus: 1},
                {id: 4, title: 'Fwdw', respondentCount: 89, createTime: '2024-06-19', completeStatus: 0},
                {id: 5, title: 'Bwqeaefcdc', respondentCount: 89, createTime: '2024-06-19', completeStatus: 1},
                {id: 6, title: 'Jrrbhv', respondentCount: 89, createTime: '2024-06-19', completeStatus: 0},
                {id: 7, title: 'Vhncxb', respondentCount: 89, createTime: '2024-06-19', completeStatus: 0},
            ]
        };
    },
    methods: {
        orderInitiateRedirect() {
            /*uni.navigateTo({
                url: '/pages/order/order-initiate/order-initiate'
            });*/
            uni.navigateTo({
                url: '/pages/order/order-servant-selection/order-servant-selection'
            });
        },
        orderDetailRedirect(oid) {
            uni.navigateTo({
                url: '/pages/order/order-detail/order-detail?oid=' + oid
            });
        }
    }
};
</script>

<style scoped>
.order-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.order-icon {
    width: 40px;
    height: 40px;
    margin-right: 10px;
}

.order-info {
    margin-top: 5px;
}

.status-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-left: 10px;
}

.green-dot {
    background-color: #27b459;
}

.red-dot {
    background-color: red;
}

.order-status {
    margin-right: 10px;
}

.respondent-count {
    color: white;
    background-color: #007aff;
    border-radius: 5px;
    font-weight: bold;
    padding: 2px;
    width: 92px;
    font-size: 14px;
    margin-bottom: 4px;
}
</style>