<template>
<div>
    <h1 v-if="type === 'h1'" :class="{ 'bold-text': bold }"><slot /></h1>
    <h2 v-else-if="type === 'h2'" :class="{ 'bold-text': bold }"><slot /></h2>
    <h3 v-else-if="type === 'h3'" :class="{ 'bold-text': bold }"><slot /></h3>
    <p v-else :class="{ 'bold-text': bold }"><slot /></p>
</div>
</template>

<script>
export default {
    props: {
        type: {type: String, default: 'p'},
        bold: {type: Boolean, default: false}
    }
}
</script>

<style>
h1 {
    font-size: 2rem;
}

h2 {
    font-size: 1.5rem;
}

h3 {
    font-size: 1.25rem;
}

p {
    font-size: 1rem;
}

.bold-text {
    font-weight: bold;
}
</style>

<style>
@import '/style/common.css';
</style>
