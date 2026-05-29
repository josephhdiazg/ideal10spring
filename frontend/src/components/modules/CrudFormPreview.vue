<script setup>
import AppButton from '../atoms/AppButton.vue'
import AppInput from '../atoms/AppInput.vue'
import { RotateCcw, Save } from '@lucide/vue'

defineProps({
  fields: {
    type: Array,
    required: true,
  },
  form: {
    type: Object,
    required: true,
  },
  saving: Boolean,
  editing: {
    type: Object,
    default: null,
  },
})

defineEmits(['save', 'cancel', 'update:field'])
</script>

<template>
  <form class="grid gap-4 md:grid-cols-2" @submit.prevent="$emit('save')">
    <AppInput
      v-for="field in fields"
      :key="field"
      :field="field"
      :model-value="form[field.key]"
      @update:model-value="$emit('update:field', field.key, $event)"
    />
    <div class="flex flex-wrap items-end gap-2 md:col-span-2">
      <button class="btn btn-primary" :disabled="saving" type="submit">
        <Save class="h-4 w-4" />
        {{ saving ? 'Guardando...' : editing ? 'Actualizar' : 'Crear' }}
      </button>
      <AppButton v-if="editing" :icon="RotateCcw" variant="secondary" @click="$emit('cancel')">Cancelar</AppButton>
    </div>
  </form>
</template>
