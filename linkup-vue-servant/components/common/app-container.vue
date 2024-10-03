<template>
<div :style="{ width: colPercentage, backgroundColor: color, padding: computedPadding }" class="container-common" @click="$emit('click')">
    <slot />
</div>
</template>

<script>
export default {
    props: {
        color: { type: String, default: 'white' }, // Color prop used for background color
        col: { type: Number, default: 12 }, // Column size for width calculation
        type: { type: String, default: '' } // Type attribute
    },
    computed: {
        colPercentage() {
            return this.col ? `${(this.col / 12) * 100}%` : '100%'; // Compute width based on column size
        },
        computedPadding() {
            // Check if type is 'list' to adjust padding, otherwise use default
            return this.type === 'list' ? '2px 15px 2px 15px' : '15px';
        }
    }
};
</script>

<style scoped>
.container-common {
    border-radius: 15px;
    box-sizing: border-box; /* Ensure padding and border are included in width */
    margin-top: 10px;
    margin-bottom: 10px;
}
</style>