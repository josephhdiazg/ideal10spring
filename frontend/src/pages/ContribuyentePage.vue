<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { AlertCircle, ClipboardList, Home, LogOut, LockKeyhole, Plus, ReceiptText, RefreshCw, ShieldCheck } from '@lucide/vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppInput from '../components/atoms/AppInput.vue'
import CrudModal from '../components/atoms/CrudModal.vue'
import StatusBadge from '../components/atoms/StatusBadge.vue'
import { api } from '../api/client'
import { authState, isAuthenticated, logout } from '../stores/auth'
import { date, money } from '../data/resources'

const isContribuyente = computed(() => authState.roles.includes('ROLE_CONTRIBUYENTE'))

function handleLogout() {
  logout()
}

// --- Censoring ---
function censor(value, show = 4) {
  if (value == null) return '-'
  const str = String(value)
  if (str.length <= show) return str
  return '•'.repeat(Math.min(str.length - show, 8)) + str.slice(-show)
}

// --- Properties ---
const properties = ref([])
const propertiesLoading = ref(false)
const propertiesError = ref('')

async function loadProperties() {
  propertiesLoading.value = true
  propertiesError.value = ''
  try {
    properties.value = await api.get('/api/v1/properties')
  } catch (e) {
    propertiesError.value = e.message
  } finally {
    propertiesLoading.value = false
  }
}

// --- Liquidations ---
const liquidations = ref([])
const liquidationsLoading = ref(false)
const liquidationsError = ref('')

async function loadLiquidations() {
  liquidationsLoading.value = true
  liquidationsError.value = ''
  try {
    liquidations.value = await api.get('/api/v1/liquidations')
  } catch (e) {
    liquidationsError.value = e.message
  } finally {
    liquidationsLoading.value = false
  }
}

// --- Create liquidation ---
const createOpen = ref(false)
const createError = ref('')
const createLoading = ref(false)
const createForm = reactive({ propertyId: '', fiscalYear: '', dueDate: '' })

const propertySelectOptions = computed(() =>
  properties.value.map((p) => ({
    label: `${censor(p.cadastralCode)} — ${censor(p.address)}`,
    value: p.id,
  }))
)

const createFields = computed(() => [
  { key: 'propertyId', label: 'Predio', type: 'select', options: propertySelectOptions.value, required: true },
  { key: 'fiscalYear', label: 'Año fiscal', type: 'number', required: true },
  { key: 'dueDate', label: 'Fecha vencimiento', type: 'date', nullable: true },
])

function openCreate() {
  Object.assign(createForm, { propertyId: '', fiscalYear: '', dueDate: '' })
  createError.value = ''
  createOpen.value = true
}

async function submitLiquidation() {
  createLoading.value = true
  createError.value = ''
  try {
    await api.post('/api/v1/liquidations', {
      propertyId: Number(createForm.propertyId),
      fiscalYear: Number(createForm.fiscalYear),
      dueDate: createForm.dueDate || null,
    })
    createOpen.value = false
    await loadLiquidations()
  } catch (e) {
    createError.value = e.message
  } finally {
    createLoading.value = false
  }
}

onMounted(() => {
  Promise.all([loadProperties(), loadLiquidations()])
})
</script>

<template>
  <div class="min-h-screen bg-sky-50 text-slate-800">

    <!-- Header -->
    <header class="border-b border-sky-100 bg-white px-6 py-4">
      <div class="mx-auto flex max-w-4xl items-center justify-between gap-4">
        <div class="flex items-center gap-3">
          <span class="brand-mark">
            <ShieldCheck class="h-6 w-6" />
          </span>
          <span>
            <span class="block text-base font-semibold text-slate-950">IDEAL Predial</span>
            <span class="block text-xs text-slate-500">Portal del contribuyente</span>
          </span>
        </div>

        <div class="flex items-center gap-3">
          <template v-if="isAuthenticated && isContribuyente">
            <span class="hidden text-sm text-slate-600 sm:block">{{ authState.username }}</span>
            <button
              class="inline-flex items-center gap-1.5 text-sm font-medium text-slate-500 hover:text-rose-600"
              type="button"
              @click="handleLogout"
            >
              <LogOut class="h-4 w-4" />
              Salir
            </button>
          </template>
          <RouterLink v-else to="/login" class="inline-flex items-center gap-1.5 text-sm font-medium text-sky-700 hover:text-sky-900">
            <LockKeyhole class="h-4 w-4" />
            Iniciar sesion
          </RouterLink>
        </div>
      </div>
    </header>

    <main class="mx-auto max-w-4xl px-4 py-8 space-y-8">

      <!-- Properties -->
      <section>
        <div class="mb-4 flex items-center justify-between gap-4">
          <div class="flex items-center gap-3">
            <span class="icon-tile bg-sky-100 text-sky-700">
              <Home class="h-5 w-5" />
            </span>
            <div>
              <p class="section-eyebrow">Catastro</p>
              <h2 class="section-title">Mis predios</h2>
            </div>
          </div>
          <AppButton :disabled="propertiesLoading" :icon="RefreshCw" variant="secondary" @click="loadProperties">
            Actualizar
          </AppButton>
        </div>

        <div v-if="propertiesError" class="mb-4 flex gap-2 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">
          <AlertCircle class="mt-0.5 h-4 w-4 shrink-0" />
          {{ propertiesError }}
        </div>

        <div v-if="propertiesLoading" class="rounded-lg border border-sky-100 bg-white py-10 text-center text-sm text-slate-500 shadow-soft">
          Cargando predios...
        </div>

        <div v-else-if="properties.length === 0" class="rounded-lg border border-sky-100 bg-white py-10 text-center text-sm text-slate-500 shadow-soft">
          No hay predios disponibles.
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="property in properties"
            :key="property.id"
            class="rounded-lg border border-sky-100 bg-white p-4 shadow-soft"
          >
            <div class="grid gap-3 sm:grid-cols-2 md:grid-cols-3">
              <div>
                <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Codigo catastral</p>
                <p class="mt-1 font-mono text-sm font-medium text-slate-800">{{ censor(property.cadastralCode) }}</p>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Direccion</p>
                <p class="mt-1 text-sm font-medium text-slate-800">{{ censor(property.address, 6) }}</p>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Municipio</p>
                <p class="mt-1 text-sm font-medium text-slate-800">{{ property.municipality?.name ?? '-' }}</p>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Uso</p>
                <p class="mt-1 text-sm font-medium text-slate-800">{{ property.propertyUse ?? '-' }}</p>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Valor catastral</p>
                <p class="mt-1 text-sm font-medium text-slate-800">{{ money(property.cadastralValue) }}</p>
              </div>
              <div>
                <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Estado</p>
                <div class="mt-1">
                  <StatusBadge :label="String(property.status)" tone="sky" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Liquidations -->
      <section>
        <div class="mb-4 flex items-center justify-between gap-4">
          <div class="flex items-center gap-3">
            <span class="icon-tile bg-rose-100 text-rose-700">
              <ReceiptText class="h-5 w-5" />
            </span>
            <div>
              <p class="section-eyebrow">Cartera</p>
              <h2 class="section-title">Mis liquidaciones</h2>
            </div>
          </div>
          <div class="flex gap-2">
            <AppButton :disabled="liquidationsLoading" :icon="RefreshCw" variant="secondary" @click="loadLiquidations">
              Actualizar
            </AppButton>
            <AppButton :icon="Plus" @click="openCreate">
              Nueva liquidacion
            </AppButton>
          </div>
        </div>

        <div v-if="liquidationsError" class="mb-4 flex gap-2 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">
          <AlertCircle class="mt-0.5 h-4 w-4 shrink-0" />
          {{ liquidationsError }}
        </div>

        <div v-if="liquidationsLoading" class="rounded-lg border border-sky-100 bg-white py-10 text-center text-sm text-slate-500 shadow-soft">
          Cargando liquidaciones...
        </div>

        <div v-else-if="liquidations.length === 0" class="rounded-lg border border-sky-100 bg-white py-10 text-center text-sm text-slate-500 shadow-soft">
          No hay liquidaciones disponibles.
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="liq in liquidations"
            :key="liq.id"
            class="rounded-lg border border-sky-100 bg-white p-4 shadow-soft"
          >
            <div class="flex items-start justify-between gap-3">
              <div class="grid flex-1 gap-3 sm:grid-cols-3">
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Predio</p>
                  <p class="mt-1 font-mono text-sm font-medium text-slate-800">{{ censor(liq.cadastralCode) }}</p>
                </div>
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Año fiscal</p>
                  <p class="mt-1 text-sm font-medium text-slate-800">{{ liq.fiscalYear }}</p>
                </div>
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Total</p>
                  <p class="mt-1 text-sm font-medium text-slate-800">{{ money(liq.totalAmount) }}</p>
                </div>
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Saldo pendiente</p>
                  <p class="mt-1 text-sm font-semibold" :class="liq.balance > 0 ? 'text-rose-600' : 'text-emerald-600'">
                    {{ money(liq.balance) }}
                  </p>
                </div>
                <div v-if="liq.dueDate">
                  <p class="text-xs font-semibold uppercase tracking-wide text-slate-400">Vencimiento</p>
                  <p class="mt-1 text-sm font-medium text-slate-800">{{ date(liq.dueDate) }}</p>
                </div>
              </div>
              <StatusBadge :label="String(liq.status)" tone="rose" />
            </div>
          </div>
        </div>
      </section>

    </main>

    <!-- Create liquidation modal -->
    <CrudModal
      :open="createOpen"
      title="Nueva liquidacion"
      subtitle="Complete los campos requeridos."
      @close="createOpen = false"
    >
      <form class="space-y-4" @submit.prevent="submitLiquidation">
        <div v-if="createError" class="flex gap-2 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">
          <AlertCircle class="mt-0.5 h-4 w-4 shrink-0" />
          {{ createError }}
        </div>

        <AppInput
          v-for="field in createFields"
          :key="field.key"
          :field="field"
          :model-value="createForm[field.key]"
          @update:model-value="createForm[field.key] = $event"
        />

        <div class="flex gap-2 pt-2">
          <button class="btn btn-primary" :disabled="createLoading" type="submit">
            <ClipboardList class="h-4 w-4" />
            {{ createLoading ? 'Guardando...' : 'Crear liquidacion' }}
          </button>
          <AppButton variant="secondary" type="button" @click="createOpen = false">Cancelar</AppButton>
        </div>
      </form>
    </CrudModal>

  </div>
</template>
