<script setup>
defineProps({
  label: {
    type: String,
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
  amber: 'bg-amber-100 text-amber-700',
  rose: 'bg-rose-100 text-rose-700',
  slate: 'bg-slate-100 text-slate-700',
  violet: 'bg-violet-100 text-violet-700',
  indigo: 'bg-indigo-100 text-indigo-700',
  teal: 'bg-teal-100 text-teal-700',
}
</script>

<template>
  <span class="status-badge" :class="tones[tone]">
    {{ label }}
  </span>
</template>
