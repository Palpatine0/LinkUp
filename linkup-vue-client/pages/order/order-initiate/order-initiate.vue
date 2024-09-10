<template>
<div style="padding: 18px;">
    <app-title type="h1" bold="true">发布订单</app-title>
    <app-title bold="true">订单标题</app-title>
    <app-input mode="text" placeholder="输入您的订单标题"/>
    <app-title bold="true">订单开放时间</app-title>
    <uni-datetime-picker v-model="duration" type="daterange" @change="durationSelection()"/>
    <app-title bold="true">订单描述</app-title>
    <app-input mode="textarea" placeholder="输入您的订单描述"/>

    <div class="center_h">
        <app-button>发布</app-button>
    </div>

</div>
</template>

<script>
export default {
    data() {
        return {
            duration: []
        }
    },
    methods: {
        durationSelection(e) {
            console.log(this.duration)
        }
    }

}
</script>
