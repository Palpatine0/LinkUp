<template>
<div :class="'col-' + col + ' container-common'" :style="{ backgroundColor: color }">
    <slot/>
</div>
</template>

<script>
export default {
    props: {
        color: { type: String, default: 'white' },
        col: { type: String, default: '12' }
    }
}
</script>

<style>
.container-common {
    padding: 15px;
    border-radius: 15px;
    background-color: #f0f0f0; /* Shared background for containers */
    box-sizing: border-box; /* Ensure padding and border are included in width */
    margin-top: 10px;
    margin-bottom: 10px;
}

/* Customize styles specifically for app-container */
</style>
