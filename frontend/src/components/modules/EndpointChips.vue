<script setup>
import StatusBadge from '../atoms/StatusBadge.vue'

defineProps({
  actions: {
    type: Array,
    required: true,
  },
})
</script>

<template>
  <div class="flex flex-wrap gap-2">
    <StatusBadge
      v-for="action in actions"
      :key="`${action.method}-${action.path}`"
      :label="`${action.method} ${action.path}`"
      tone="sky"
    />
  </div>
</template>
