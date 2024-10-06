<template>
<div
    :class="['button-common', shaped ? 'button-shaped' : 'button-normal', sizeClass]"
    @click="$emit('click')"
    :style="{ width: width, backgroundColor: color }"
>
    <slot />
</div>
</template>

<script>
export default {
    props: {
        shaped: { type: Boolean, default: false },
        size: { type: String, default: 'medium' },
        width: { type: String, default: '100%' },
        color: { type: String, default: '#007BFF' },
    },
    computed: {
        sizeClass() {
            switch (this.size) {
                case 'very-small':
                    return 'button-very-small';
                case 'small':
                    return 'button-small';
                case 'medium':
                    return 'button-medium';
                case 'large':
                    return 'button-large';
                case 'very-large':
                    return 'button-very-large';
                case 'huge':
                    return 'button-huge';
                default:
                    return 'button-medium'; // Default size
            }
        },
    },
    // Ensure there's no 'size' in data
    data() {
        return {
            // other data properties
        };
    },
};
</script>
