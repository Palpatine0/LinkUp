<template>
<div class="page">

</div>
</template>

<script>
import common from "../../../../utils/common";
import app from "../../../../App.vue";

export default {
    name: "withdraw",
    computed: {
        common() {
            return common;
        },
        app() {
            return app
        }
    },
    data() {
        return {

        };
    },
    onLoad() {
        this.reload();
    },
    methods: {
        reload() {
        },
    }
};
</script>

<style scoped   >

</style>