<script setup>
import { computed, onMounted, ref } from 'vue'
import { AlertCircle, BadgeCheck, Home, ReceiptText, RefreshCw, WalletCards } from '@lucide/vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppCard from '../components/atoms/AppCard.vue'
import DataTable from '../components/atoms/DataTable.vue'
import PageHeader from '../components/atoms/PageHeader.vue'
import StatCard from '../components/atoms/StatCard.vue'
import StatusBadge from '../components/atoms/StatusBadge.vue'
import { api } from '../api/client'
import { dashboardFallbackStats, money, resources } from '../data/resources'

const properties = resources.find((resource) => resource.key === 'properties')
const liquidations = resources.find((resource) => resource.key === 'liquidations')
const certificates = resources.find((resource) => resource.key === 'clearance-certificates')

const dashboard = ref(null)
const propertyRows = ref([])
const liquidationRows = ref([])
const certificateRows = ref([])
const loading = ref(false)
const error = ref('')

const stats = computed(() => {
  if (!dashboard.value) {
    return dashboardFallbackStats
  }
  const activeAssessments = Object.values(dashboard.value.assessmentsByStatus || {}).reduce((total, value) => total + Number(value), 0)
  return [
    { label: 'Total liquidado', value: money(dashboard.value.totalLiquidated), detail: 'liquidaciones registradas', icon: ReceiptText, tone: 'sky' },
    { label: 'Total recaudado', value: money(dashboard.value.totalCollected), detail: 'pagos aplicados', icon: WalletCards, tone: 'cyan' },
    { label: 'Cartera pendiente', value: money(dashboard.value.pendingPortfolio), detail: 'saldo por recaudar', icon: Home, tone: 'blue' },
    { label: 'Liquidaciones activas', value: String(activeAssessments), detail: 'agrupadas por estado', icon: BadgeCheck, tone: 'emerald' },
  ]
})

async function loadDashboard() {
  loading.value = true
  error.value = ''
  try {
    const [metrics, propertiesData, liquidationsData, certificatesData] = await Promise.all([
      api.get('/api/v1/dashboard/predial'),
      api.get('/api/v1/properties'),
      api.get('/api/v1/liquidations'),
      api.get('/api/v1/clearance-certificates'),
    ])
    dashboard.value = metrics
    propertyRows.value = propertiesData.slice(0, 6)
    liquidationRows.value = liquidationsData.slice(0, 6)
    certificateRows.value = certificatesData.slice(0, 4)
  } catch (requestError) {
    error.value = requestError.message
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>

<template>
  <section class="page-section">
    <PageHeader
      action-label="Actualizar"
      eyebrow="Operacion"
      title="Panel predial"
      @action="loadDashboard"
    />

    <div v-if="error" class="mb-5 rounded-lg border border-rose-200 bg-rose-50 p-4 text-sm font-medium text-rose-700" role="alert">
      <div class="flex gap-2">
        <AlertCircle class="h-5 w-5 shrink-0" />
        <span>{{ error }}</span>
      </div>
    </div>

    <div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
      <StatCard
        v-for="stat in stats"
        :key="stat.label"
        :detail="stat.detail"
        :icon="stat.icon"
        :label="stat.label"
        :tone="stat.tone"
        :value="stat.value"
      />
    </div>

    <div class="mt-6 grid gap-6 xl:grid-cols-[minmax(0,1fr)_420px]">
      <AppCard>
        <div class="card-header">
          <div>
            <h2 class="section-title">Predios recientes</h2>
            <p class="muted-text">Ultimos registros catastrales disponibles</p>
          </div>
          <AppButton :disabled="loading" :icon="RefreshCw" variant="secondary" @click="loadDashboard">Refrescar</AppButton>
        </div>
        <div v-if="loading" class="py-12 text-center text-sm text-slate-500">Cargando informacion...</div>
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
              <h2 class="section-title">Estados de liquidacion</h2>
              <p class="muted-text">Distribucion actual por estado</p>
            </div>
            <ReceiptText class="h-5 w-5 text-sky-500" />
          </div>
          <div class="mt-5 space-y-3">
            <div
              v-for="[status, count] in Object.entries(dashboard?.assessmentsByStatus || {})"
              :key="status"
              class="flex items-center justify-between rounded-lg bg-sky-50 p-3"
            >
              <StatusBadge :label="status" tone="sky" />
              <span class="text-sm font-semibold text-slate-900">{{ count }}</span>
            </div>
            <p v-if="!dashboard?.assessmentsByStatus || Object.keys(dashboard.assessmentsByStatus).length === 0" class="text-sm text-slate-500">
              No hay liquidaciones registradas.
            </p>
          </div>
        </AppCard>

        <AppCard>
          <h2 class="section-title">Certificados recientes</h2>
          <p class="muted-text">Paz y salvo emitidos</p>
          <div class="mt-4 space-y-3">
            <div v-for="certificate in certificateRows" :key="certificate.id" class="rounded-lg border border-sky-100 p-3">
              <p class="font-semibold text-slate-900">{{ certificate.certificateNumber }}</p>
              <p class="text-sm text-slate-500">{{ certificate.cadastralCode }}</p>
            </div>
            <p v-if="certificateRows.length === 0" class="text-sm text-slate-500">No hay certificados emitidos.</p>
          </div>
        </AppCard>
      </div>
    </div>

    <div class="mt-6">
      <AppCard>
        <div class="card-header">
          <div>
            <h2 class="section-title">Liquidaciones recientes</h2>
            <p class="muted-text">Seguimiento de cartera y saldos</p>
          </div>
        </div>
        <DataTable
          :can-delete="false"
          :can-update="false"
          :columns="liquidations.columns"
          :rows="liquidationRows"
        />
      </AppCard>
    </div>
  </section>
</template>
