<script setup>
import { onMounted, ref } from 'vue'
import { CircleDollarSign, FileCheck2, Plus, ReceiptText } from '@lucide/vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppCard from '../components/atoms/AppCard.vue'
import DataTable from '../components/atoms/DataTable.vue'
import PageHeader from '../components/atoms/PageHeader.vue'
import StatCard from '../components/atoms/StatCard.vue'
import DashboardActivity from '../components/modules/DashboardActivity.vue'
import { dashboardStats, resources } from '../data/resources'
import { api } from '../api/client'

const properties = resources.find((resource) => resource.key === 'properties')
const propertyRows = ref([])
const loadingProperties = ref(false)

async function loadProperties() {
  loadingProperties.value = true
  try {
    propertyRows.value = await api.get('/api/v1/properties')
  } catch {
    propertyRows.value = []
  } finally {
    loadingProperties.value = false
  }
}

onMounted(loadProperties)
const upcoming = [
  { label: 'Cierre de vigencia', date: 'Jun 12', progress: 82 },
  { label: 'Revision de beneficios', date: 'Jun 18', progress: 64 },
  { label: 'Corte cartera vencida', date: 'Jun 30', progress: 41 },
]
</script>

<template>
  <section class="page-section">
    <PageHeader eyebrow="Operacion" title="Panel predial" action-label="Nuevo registro" />

    <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <StatCard
        v-for="stat in dashboardStats"
        :key="stat.label"
        :detail="stat.detail"
        :icon="stat.icon"
        :label="stat.label"
        :tone="stat.tone"
        :value="stat.value"
      />
    </div>

    <div class="mt-6 grid gap-6 xl:grid-cols-[1fr_360px]">
      <AppCard>
        <div class="card-header">
          <div>
            <h2 class="section-title">Predios recientes</h2>
            <p class="muted-text">Registros catastrales con actividad reciente</p>
          </div>
          <AppButton :icon="Plus">Predio</AppButton>
        </div>
        <div v-if="loadingProperties" class="py-12 text-center text-sm text-slate-500">Cargando predios...</div>
        <DataTable
          v-else
          :can-delete="false"
          :can-update="false"
          :columns="properties.columns"
          :rows="propertyRows"
        />
      </AppCard>

      <div class="space-y-6">
        <AppCard>
          <div class="flex items-center justify-between">
            <div>
              <h2 class="section-title">Resumen fiscal</h2>
              <p class="muted-text">Vigencia 2026</p>
            </div>
            <CircleDollarSign class="h-5 w-5 text-sky-500" />
          </div>
          <div class="mt-5 rounded-lg bg-gradient-to-br from-sky-500 to-cyan-400 p-5 text-white">
            <p class="text-sm text-sky-50">Cumplimiento anual</p>
            <p class="mt-1 text-3xl font-semibold">68%</p>
            <div class="mt-4 h-2 rounded-full bg-white/30">
              <div class="h-2 w-[68%] rounded-full bg-white"></div>
            </div>
          </div>
          <div class="mt-5 space-y-4">
            <div v-for="item in upcoming" :key="item.label">
              <div class="mb-2 flex items-center justify-between text-sm">
                <span class="font-medium text-slate-700">{{ item.label }}</span>
                <span class="text-slate-500">{{ item.date }}</span>
              </div>
              <div class="h-2 rounded-full bg-sky-50">
                <div class="h-2 rounded-full bg-sky-400" :style="{ width: `${item.progress}%` }"></div>
              </div>
            </div>
          </div>
        </AppCard>

        <DashboardActivity />
      </div>
    </div>

    <div class="mt-6 grid gap-6 lg:grid-cols-2">
      <AppCard>
        <div class="flex items-center gap-3">
          <span class="icon-tile bg-blue-100 text-blue-700">
            <ReceiptText class="h-5 w-5" />
          </span>
          <div>
            <h2 class="section-title">Liquidacion rapida</h2>
            <p class="muted-text">Predio MED-001-447, vigencia 2026</p>
          </div>
        </div>
      </AppCard>
      <AppCard>
        <div class="flex items-center gap-3">
          <span class="icon-tile bg-emerald-100 text-emerald-700">
            <FileCheck2 class="h-5 w-5" />
          </span>
          <div>
            <h2 class="section-title">Certificados</h2>
            <p class="muted-text">746 documentos emitidos</p>
          </div>
        </div>
      </AppCard>
    </div>
  </section>
</template>
