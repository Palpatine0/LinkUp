<template>
<div class="page">
    <app-title type="h1" bold="true">{{  }}</app-title>


</div>
</template>

<script>

import app from "../../App.vue";
import common from "../../utils/common";

export default {
    name: "home",
    data() {
        return {
            // ** page vars ** //
            page: 1,
            size: 20,
            hasMore: true,
            loading: false,
            // ** /page vars ** //

            user: {},
            userList: [],
        };
    },
    onLoad(e) {

    },
    methods: {

    }
};
</script>

<style scoped>

</style>
