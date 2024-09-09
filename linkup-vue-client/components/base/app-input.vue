<template>
<div :style="{ width: colPercentage + '%' }">
    <input v-if="mode === 'text'" :placeholder="placeholder" class="input-common input-text" />
    <textarea v-else-if="mode === 'textarea'" :placeholder="placeholder" class="input-common textarea" />
    <!-- Add more conditions for other modes like select, search, etc. -->
</div>
</template>

<script>
export default {
    props: {
        mode: { type: String, default: 'text' },
        col: { type: String, default: '12' }, // This determines the width (12 being 100%)
        placeholder: { type: String, default: '' }
    },
    computed: {
        colPercentage() {
            return (this.col / 12) * 100; // Convert col value to percentage
        }
    }
}
</script>

<style>
.input-common {
    padding: 10px;
    background-color: #ebecf0;
    width: 100%;
    border-radius: 10px;
    box-sizing: border-box; /* Ensure padding and border are included in the width */
    margin-top: 10px;
    margin-bottom: 10px;
}

.input-text {
    height: 40px; /* Adjust the height for text inputs */
}

.textarea {
    height: 100px; /* Adjust the height for textareas */
}
</style>