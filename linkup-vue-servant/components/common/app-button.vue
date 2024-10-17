<template>
<div :class="['button-common', shaped ? 'button-shaped' : 'button-normal', sizeClass]"
     @click="$emit('click')"
     :style="{ width: width, backgroundColor: color, color: fontColor }">
    <slot/>
</div>
</template>

<script>
export default {
    props: {
        shaped: { type: Boolean, default: false }, // Determines if the div has a "shaped" appearance
        size: { type: String, default: 'medium' }, // small, medium, large
        width: { type: String, default: '100%' },  // Button width, default is 100%
        color: { type: String, default: '#007BFF' }, // Button background color, default is blue
        fontColor: { type: String, default: '#FFFFFF' } // Font color, default is white
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
        }
    }
}
</script>

<style>
.button-common {
    text-align: center;
    display: inline-block; /* Makes it behave like a button */
    box-sizing: border-box;
    cursor: pointer; /* Makes it feel like a button */
    margin: 10px 0;
    width: 100%; /* Default width is 100% but can be overridden */
}

/* Shape styles */
.button-shaped {
    border-radius: 25px;
}

.button-normal {
    border-radius: 5px;
}

/* Define size classes */
.button-very-small {
    padding: 3px 10px;
    font-size: 16px;
    height: 30px;
}

.button-small {
    padding: 7px 14px;
    font-size: 14px;
    height: 35px;
}

.button-medium {
    padding: 8px 20px;
    font-size: 16px;
    height: 40px;
}

.button-large {
    padding: 12px 24px;
    font-size: 18px;
    height: 45px;
}

.button-very-large {
    padding: 12px 28px;
    font-size: 20px;
    height: 50px;
}

.button-huge {
    padding: 16px 32px;
    font-size: 24px;
    height: 60px;
}
</style>
