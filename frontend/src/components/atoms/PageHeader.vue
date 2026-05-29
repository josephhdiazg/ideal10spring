<script setup>
import { Plus, Search } from '@lucide/vue'
import AppButton from './AppButton.vue'

defineProps({
  eyebrow: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    required: true,
  },
  actionLabel: {
    type: String,
    default: 'Nuevo',
  },
  showSearch: {
    type: Boolean,
    default: true,
  },
})
</script>

<template>
  <div class="page-header">
    <div>
      <p v-if="eyebrow" class="section-eyebrow">{{ eyebrow }}</p>
      <h1 class="page-title">{{ title }}</h1>
    </div>
    <div class="flex flex-wrap items-center gap-2">
      <div v-if="showSearch" class="search-box">
        <Search class="h-4 w-4 text-slate-400" />
        <input class="search-input" placeholder="Buscar" type="search" />
      </div>
      <AppButton :icon="Plus">{{ actionLabel }}</AppButton>
    </div>
  </div>
</template>
