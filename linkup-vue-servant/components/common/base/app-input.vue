<template>
<div :style="{ width: colPercentage + '%' }" class="input-container">
    <!-- Text Input -->
    <input
        v-if="mode === 'text'"
        :value="value"
        :style="{ backgroundColor: color }"
        :class="{'input-focused': isFocused, 'input-disabled': disabled}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @input="onInput"
        class="input-common input-text"
        placeholder=" "
        :disabled="disabled"
    />

    <!-- Textarea -->
    <textarea
        v-else-if="mode === 'textarea'"
        :value="value"
        :style="{ backgroundColor: color }"
        :class="{'input-focused': isFocused, 'input-disabled': disabled}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @input="onInput"
        class="input-common textarea"
        placeholder=" "
        :disabled="disabled"
    ></textarea>

    <!-- Number Input -->
    <input
        v-else-if="mode === 'number'"
        type="number"
        :value="value"
        :style="{ backgroundColor: color }"
        :class="{'input-focused': isFocused, 'input-disabled': disabled}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @input="onNumberInput"
        class="input-common input-number"
        placeholder=" "
        :disabled="disabled"
    />

    <label :class="{ 'placeholder-move': isFocused || hasValue }" class="placeholder">{{ placeholder }}</label>
    <div v-if="validationMessage" class="validation-message">{{ validationMessage }}</div>
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
        value: {type: [String, Number], default: ''},
        mode: {type: String, default: 'text'},
        placeholder: {type: String, default: ''},
        color: {type: String, default: '#f3f2f6'},
        col: {type: String, default: '12'},
        validationMessage: {type: String, default: ''},
        disabled: {type: Boolean, default: false}
    },

    computed: {
        colPercentage() {
            return (this.col / 12) * 100;
        },
        hasValue() {
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
            const numericValue = value.replace(/[^0-9]/g, '');
            this.$emit('input', numericValue);
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

.validation-message {
    color: red;
    font-size: 12px;
    margin-top: 4px;
    position: absolute;
    top: 34px;
    left: 10px;
}

.input-common:disabled {
    background-color: #e0e0e0 !important;;
    color: #a0a0a0 !important;;
    cursor: not-allowed;
}

.input-disabled{
    color: #a0a0a0 !important;;
    cursor: not-allowed;
}
</style>
