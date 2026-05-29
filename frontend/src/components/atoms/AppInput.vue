<script setup>
defineProps({
  field: {
    type: Object,
    required: true,
  },
  modelValue: {
    type: [String, Number, Boolean],
    default: '',
  },
})

defineEmits(['update:modelValue'])
</script>

<template>
  <label class="block">
    <span class="field-label">{{ field.label }}</span>
    <select
      v-if="field.type === 'select'"
      class="field-control"
      :required="field.required"
      :value="modelValue"
      @change="$emit('update:modelValue', $event.target.value === 'true' ? true : $event.target.value === 'false' ? false : $event.target.value)"
    >
      <option v-if="field.nullable" value="">Sin valor</option>
      <option
        v-for="option in field.options"
        :key="typeof option === 'object' ? option.value : option"
        :value="typeof option === 'object' ? option.value : option"
      >
        {{ typeof option === 'object' ? option.label : option }}
      </option>
    </select>
    <textarea
      v-else-if="field.type === 'textarea'"
      class="field-control min-h-24 py-3"
      :required="field.required"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    <input
      v-else
      class="field-control"
      :required="field.required"
      :type="field.type || 'text'"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
    />
  </label>
</template>
