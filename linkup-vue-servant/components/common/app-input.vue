<template>
<div :style="{ width: colPercentage + '%' }" class="input-container">
    <!-- Text Input -->
    <input
        v-if="mode === 'text'"
        :value="value"
        :style="{ backgroundColor: color }"
        :class="{'input-focused': isFocused}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @input="onInput"
        class="input-common input-text"
        placeholder=" "
    />

    <!-- Textarea -->
    <textarea
        v-else-if="mode === 'textarea'"
        :value="value"
        :style="{ backgroundColor: color }"
        :class="{'input-focused': isFocused}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @input="onInput"
        class="input-common textarea"
        placeholder=" "
    ></textarea>

    <!-- Number Input -->
    <input
        v-else-if="mode === 'number'"
        type="number"
        :value="value"
        :style="{ backgroundColor: color }"
        :class="{'input-focused': isFocused}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @input="onNumberInput"
        class="input-common input-number"
        placeholder=" "
    />
    <label :class="{ 'placeholder-move': isFocused || hasValue }" class="placeholder">{{ placeholder }}</label>
</div>
</template>

<script>
export default {
    data() {
        return {
            isFocused: false,
        };
    },
    props: {
        value: { type: [String, Number], default: '' },
        mode: { type: String, default: 'text' },
        col: { type: String, default: '12' },
        placeholder: { type: String, default: '' },
        color: { type: String, default: '#f3f2f6' }, // Prop for background color
    },
    computed: {
        colPercentage() {
            return (this.col / 12) * 100;
        },
        hasValue() {
            // Check if value is not null, undefined, or an empty string
            return this.value !== null && this.value !== undefined && this.value !== '';
        },
    },
    methods: {
        onInput(event) {
            this.$emit('input', event.target.value);
        },
        onNumberInput(event) {
            const value = event.target.value;
            // Ensure the input is numeric only
            const numericValue = value.replace(/[^0-9]/g, ''); // Removes any non-numeric characters
            this.$emit('input', numericValue); // Emit only numeric value
        },
    },
};
</script>

<style scoped>
.input-container {
    position: relative;
}

.input-common {
    padding: 0 10px 0 10px;
    background-color: #f3f2f6;
    width: 100%;
    border-radius: 10px;
    box-sizing: border-box;
    margin-top: 6px;
    margin-bottom: 12px;
    font-size: 16px;
    border: 1px solid transparent;
    line-height: normal;
}

.input-text,
.input-number {
    height: 40px;
}

.textarea {
    height: 100px;
    resize: none;
}

/* Placeholder Styling */
.placeholder {
    position: absolute;
    top: 50%;
    left: 10px;
    transform: translateY(-50%);
    color: #aaa;
    transition: all 0.2s ease;
    pointer-events: none; /* Ignore mouse events on the label */
}

.placeholder-move {
    display: none;
}

.input-focused {
    /* Add styles for focused input if necessary */
}
</style>
