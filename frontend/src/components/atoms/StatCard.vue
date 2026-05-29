<script setup>
import AppCard from './AppCard.vue'

defineProps({
  label: {
    type: String,
    required: true,
  },
  value: {
    type: String,
    required: true,
  },
  detail: {
    type: String,
    default: '',
  },
  icon: {
    type: Object,
    required: true,
  },
  tone: {
    type: String,
    default: 'sky',
  },
})

const tones = {
  sky: 'bg-sky-100 text-sky-700',
  cyan: 'bg-cyan-100 text-cyan-700',
  blue: 'bg-blue-100 text-blue-700',
  emerald: 'bg-emerald-100 text-emerald-700',
}
</script>

<template>
  <AppCard>
    <div class="flex items-start justify-between gap-4">
      <div>
        <p class="text-sm font-medium text-slate-500">{{ label }}</p>
        <p class="mt-2 text-2xl font-semibold text-slate-950">{{ value }}</p>
      </div>
      <div class="icon-tile" :class="tones[tone]">
        <component :is="icon" class="h-5 w-5" />
      </div>
    </div>
    <p class="mt-4 text-sm text-slate-500">{{ detail }}</p>
  </AppCard>
</template>
