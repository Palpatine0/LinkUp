<template>
<div :style="{ width: colPercentage + '%' }" class="input-container">
    <input
        v-if="mode === 'text'"
        v-model="inputValue"
        :class="{'input-focused': isFocused}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        class="input-common input-text"
        placeholder=" "
    />
    <!-- Adding textarea support -->
    <textarea
        v-else-if="mode === 'textarea'"
        v-model="inputValue"
        :class="{'input-focused': isFocused}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        class="input-common textarea"
        placeholder=" "
    ></textarea>

    <label :class="{ 'placeholder-move': isFocused || inputValue }" class="placeholder">{{ placeholder }}</label>
</div>
</template>

<script>
export default {
    data() {
        return {
            isFocused: false,
            inputValue: ''
        };
    },
    props: {
        mode: { type: String, default: 'text' }, // Mode can be either 'text' or 'textarea'
        col: { type: String, default: '12' },
        placeholder: { type: String, default: 'Search...' }
    },
    computed: {
        colPercentage() {
            return (this.col / 12) * 100;
        }
    }
};
</script>

<style scoped>
.input-container {
    position: relative;
}

.input-common {
    padding: 10px;
    background-color: #f3f2f6;
    width: 100%;
    border-radius: 10px;
    box-sizing: border-box;
    margin-top: 6px;
    margin-bottom: 12px;
    font-size: 16px;
    border: 1px solid transparent;
}

.input-text {
    height: 40px;
}

/* Add styling for the textarea */
.textarea {
    height: 100px; /* You can adjust this height as needed */
    resize: none; /* Prevent users from resizing the textarea */
}

.placeholder {
    position: absolute;
    top: 10px;
    left: 10px;
    color: #aaa;
    transition: all 0.2s ease;
    pointer-events: none; /* Ignore mouse events on the label */
}

.placeholder-move {
    //top: -17px;
    //left: 10px;
    //font-size: 12px;
    //color: #007bff;
    display: none;
}

.input-focused {
    //outline: none;
    //border-color: #007bff;
}
</style>
