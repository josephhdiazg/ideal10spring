<script setup>
const props = defineProps({
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

const inputId = `field-${Math.random().toString(36).slice(2)}`

function optionValue(option) {
  return typeof option === 'object' ? option.value : option
}

function optionLabel(option) {
  return typeof option === 'object' ? option.label : option
}

function updateValue(event) {
  const value = event.target.value
  if (value === 'true') {
    return true
  }
  if (value === 'false') {
    return false
  }
  return value
}
</script>

<template>
  <label class="block" :for="inputId">
    <span class="field-label">
      {{ field.label }}
      <span v-if="field.required" class="text-rose-600" aria-hidden="true">*</span>
    </span>
    <select
      v-if="field.type === 'select'"
      :id="inputId"
      class="field-control"
      :aria-describedby="field.help ? `${inputId}-help` : undefined"
      :required="field.required"
      :value="modelValue"
      @change="$emit('update:modelValue', updateValue($event))"
    >
      <option v-if="!field.required || field.nullable" value="">Sin valor</option>
      <option v-else value="" disabled>Seleccione una opcion</option>
      <option
        v-for="option in field.options"
        :key="optionValue(option)"
        :value="optionValue(option)"
      >
        {{ optionLabel(option) }}
      </option>
    </select>
    <textarea
      v-else-if="field.type === 'textarea'"
      :id="inputId"
      class="field-control min-h-24 py-3"
      :aria-describedby="field.help ? `${inputId}-help` : undefined"
      :maxlength="field.maxlength"
      :required="field.required"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    <input
      v-else
      :id="inputId"
      class="field-control"
      :aria-describedby="field.help ? `${inputId}-help` : undefined"
      :autocomplete="field.autocomplete"
      :max="field.max"
      :maxlength="field.maxlength"
      :min="field.min"
      :required="field.required"
      :step="field.step"
      :type="field.type || 'text'"
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    <p v-if="field.help" :id="`${inputId}-help`" class="mt-1 text-xs text-slate-500">{{ field.help }}</p>
  </label>
</template>
