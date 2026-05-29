<script setup>
import AppCard from '../atoms/AppCard.vue'
import StatusBadge from '../atoms/StatusBadge.vue'

defineProps({
  resource: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <AppCard>
    <div class="flex items-center gap-3">
      <span class="icon-tile bg-sky-100 text-sky-700">
        <component :is="resource.icon" class="h-5 w-5" />
      </span>
      <div class="min-w-0">
        <p class="truncate text-sm font-semibold text-slate-950">{{ resource.title }}</p>
        <p class="text-sm text-slate-500">{{ resource.endpoint }}</p>
      </div>
    </div>
    <div class="mt-4 flex flex-wrap gap-2">
      <StatusBadge :label="resource.eyebrow" :tone="resource.accent" />
      <StatusBadge :label="resource.title" tone="sky" />
    </div>
  </AppCard>
</template>
