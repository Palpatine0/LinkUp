<template>
<div class="page">
    <app-title type="h1" bold="true">发布订单</app-title>
    <app-title bold="true">标题</app-title>
    <app-input mode="text" placeholder="输入您的订单标题"/>
    <app-title bold="true">开放时间</app-title>
    <uni-datetime-picker v-model="duration" type="daterange" @change="durationSelection()"/>
    <app-title bold="true">服务者姓别</app-title>
    <app-input mode="text" placeholder="选择姓别"/>
    <app-title bold="true">服务者年龄</app-title>
    <app-input mode="text" placeholder="选择年龄"/>
    <app-title bold="true">服务时长</app-title>
    <app-input mode="text" placeholder="选择服务时长"/>
    <app-title type="h2" bold="true">价格</app-title>
    <app-input mode="text" placeholder="选择属性"/>


    <div class="center_h">
        <app-button>发布</app-button>
    </div>

</div>
</template>

<script>
export default {
    data() {
        return {
            duration: [],
            servantType: ''
        }
    },
    onLoad(param) {
        this.servantType = param.servantType
        console.log(this.servantType)
    },
    methods: {
        durationSelection(e) {
            console.log(this.duration)
        }
    }

}
</script>
