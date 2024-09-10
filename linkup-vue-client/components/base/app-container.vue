<template>
<div :style="{ width: colPercentage, backgroundColor: color }" class="container-common">
    <slot />
</div>
</template>

<script>
export default {
    props: {
        color: { type: String, default: 'white' }, // Color prop used for background color
        col: { type: Number, default: 12 } // Column size for width calculation
    },
    computed: {
        colPercentage() {
            return this.col ? `${(this.col / 12) * 100}%` : '100%'; // Compute width based on column size
        }
    }
};
</script>

<style scoped>
.container-common {
    padding: 15px;
    border-radius: 15px;
    box-sizing: border-box; /* Ensure padding and border are included in width */
    margin-top: 10px;
    margin-bottom: 10px;
}
</style>
