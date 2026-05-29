<script setup>
import { FileCheck2, ReceiptText, WalletCards } from '@lucide/vue'
import AppCard from '../atoms/AppCard.vue'

const activity = [
  { title: 'Pago aplicado', detail: 'Predio MED-001-447', time: '09:42', icon: WalletCards },
  { title: 'Liquidacion generada', detail: 'Vigencia 2026', time: '10:18', icon: ReceiptText },
  { title: 'Certificado emitido', detail: 'Paz y salvo aprobado', time: '11:05', icon: FileCheck2 },
]
</script>

<template>
  <AppCard>
    <h2 class="section-title">Actividad</h2>
    <div class="mt-5 space-y-4">
      <div v-for="event in activity" :key="event.title" class="flex gap-3">
        <span class="icon-tile bg-sky-50 text-sky-600">
          <component :is="event.icon" class="h-5 w-5" />
        </span>
        <div class="min-w-0 flex-1">
          <div class="flex items-center justify-between gap-3">
            <p class="truncate text-sm font-semibold text-slate-800">{{ event.title }}</p>
            <span class="text-xs text-slate-400">{{ event.time }}</span>
          </div>
          <p class="truncate text-sm text-slate-500">{{ event.detail }}</p>
        </div>
      </div>
    </div>
  </AppCard>
</template>
