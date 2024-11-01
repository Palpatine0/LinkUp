<template>
<div v-if="isVisible" class="share-container">
    <div class="mask" @click="close"></div>
    <div class="widget-center coup-anim">
        <div class="widget-title">{{ widget.title }}</div>
        <div class="widget-container">
            <div class="widget-content">{{ widget.content }}</div>
            <div class="widget-tips">{{ widget.tips }}</div>
        </div>
    </div>
</div>
</template>

<script>
export default {
    name: "app-widget",
    props: {
        widget: {
            type: Object,
            default: () => ({
                title: "Title",
                content: "content",
                tips: "Tips",
                loading: false,
            })
        }
    },
    data() {
        return {
            isVisible: false // Manage visibility here
        };
    },
    methods: {
        open() {
            this.isVisible = true;
        },
        close() {
            this.isVisible = false;
        }
    }
};
</script>

<style scoped>
.share-container {
    z-index: 1000;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
}


.widget-title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 20px;
}

.widget-container {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.widget-content {
    font-size: 16px;
    margin-bottom: 20px;
}

.widget-tips {
    color: #949393;
}
</style>