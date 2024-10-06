<template>
<div :style="{ width: colPercentage + '%' }" class="input-container">
    <!-- Text Input -->
    <input
        v-if="mode === 'text'"
        :value="value"
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
        :class="{'input-focused': isFocused}"
        @focus="isFocused = true"
        @blur="isFocused = false"
        @input="onInput"
        class="input-common textarea"
        placeholder=" "
    ></textarea>

    <!-- Number Input -->
    <input
        v-if="mode === 'number'"
        type="number"
        :value="value"
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
    name: "app-input",
    data() {
        return {
            isFocused: false,
        };
    },
    props: {
        value: {type: [String, Number], default: ''},
        mode: {type: String, default: 'text'},
        col: {type: String, default: '12'},
        placeholder: {type: String, default: ''}
    },
    computed: {
        colPercentage() {
            return (this.col / 12) * 100;
        },
        hasValue() {
            // Check if value is not null, undefined, or an empty string
            return this.value !== null && this.value !== undefined && this.value !== '';
        }
    },
    methods: {
        onInput(event) {
            this.$emit('input', event.target.value);
        },
        onNumberInput(event) {
            const value = event.target.value;
            const numericValue = parseFloat(value); // Convert to a float
            if(!isNaN(numericValue)) {
                this.$emit('input', numericValue); // Emit the numeric value directly
            }
        }
    }
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

.input-text, .input-number {
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
    //transform: translateY(-150%); /* Moves the placeholder above the input */
    //font-size: 12px;
    //color: #000;
}

.input-focused {
    /* Add styles for focused input if necessary */
}
</style>
