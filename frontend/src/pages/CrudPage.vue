<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { AlertCircle, ClipboardList, FileCheck2, Plus, RefreshCw, Save, Trash2, UsersRound, WalletCards } from '@lucide/vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppCard from '../components/atoms/AppCard.vue'
import AppInput from '../components/atoms/AppInput.vue'
import DataTable from '../components/atoms/DataTable.vue'
import PageHeader from '../components/atoms/PageHeader.vue'
import StatusBadge from '../components/atoms/StatusBadge.vue'
import CrudFormPreview from '../components/modules/CrudFormPreview.vue'
import ResourceSummary from '../components/modules/ResourceSummary.vue'
import { api } from '../api/client'
import { useCrudResource } from '../composables/useCrudResource'
import { date, money, paymentFormFields, propertyOwnerFields } from '../data/resources'

const props = defineProps({
  resource: {
    type: Object,
    required: true,
  },
})

const references = reactive({})
const selected = ref(null)
const filters = reactive({})
const actionError = ref('')
const actionSuccess = ref('')
const relationLoading = ref(false)
const propertyOwners = ref([])
const payments = ref([])
const paymentForm = reactive({})
const ownerForm = reactive({})
const editingOwner = ref(null)

const actionLabel = computed(() => `Nuevo ${props.resource.singular}`)
const {
  rows,
  loading,
  saving,
  error,
  editing,
  form,
  canCreate,
  canUpdate,
  canDelete,
  load,
  save,
  remove,
  fillForm,
  resetForm,
} = useCrudResource(props.resource)

const displayFields = computed(() => withReferenceOptions(props.resource.fields))
const filterFields = computed(() => withReferenceOptions(props.resource.filters || []))
const ownerFields = computed(() => withReferenceOptions(propertyOwnerFields))
const paymentFields = computed(() => paymentFormFields)
const selectedDetails = computed(() => selected.value ? Object.entries(selected.value).filter(([key]) => key !== 'details') : [])

function withReferenceOptions(fields) {
  return fields.map((field) => {
    if (!field.source) {
      return field
    }
    return {
      ...field,
      options: references[field.source.endpoint] || [],
    }
  })
}

function labelFor(item, labelKey) {
  return typeof labelKey === 'function' ? labelKey(item) : item[labelKey]
}

async function loadReferences(fields = []) {
  const sources = fields.map((field) => field.source).filter(Boolean)
  await Promise.all([...new Map(sources.map((item) => [item.endpoint, item])).values()].map(async (source) => {
    const items = await api.get(source.endpoint)
    references[source.endpoint] = items.map((item) => ({
      label: labelFor(item, source.labelKey),
      value: item[source.valueKey],
    }))
  }))
}

function resetActionForms() {
  Object.assign(paymentForm, { amount: '', paymentMethod: '', reference: '' })
  Object.assign(ownerForm, { ownerId: '', ownershipPercentage: '' })
  editingOwner.value = null
  actionError.value = ''
  actionSuccess.value = ''
}

async function refreshResource() {
  await load(filters)
}

async function applyFilters() {
  selected.value = null
  await refreshResource()
}

function updateField(key, value) {
  form[key] = value
}

function updatePaymentField(key, value) {
  paymentForm[key] = value
}

function updateOwnerField(key, value) {
  ownerForm[key] = value
}

function editRecord(row) {
  selected.value = row
  fillForm(row)
}

async function selectRecord(row) {
  selected.value = row
  resetActionForms()
  if (props.resource.key === 'properties') {
    await loadPropertyOwners()
  }
  if (props.resource.key === 'liquidations') {
    await loadPayments()
  }
}

async function loadPropertyOwners() {
  if (!selected.value?.id) return
  relationLoading.value = true
  actionError.value = ''
  try {
    propertyOwners.value = await api.get(`/api/v1/properties/${selected.value.id}/owners`)
  } catch (requestError) {
    actionError.value = requestError.message
  } finally {
    relationLoading.value = false
  }
}

function fillOwnerAssignment(assignment) {
  editingOwner.value = assignment
  ownerForm.ownerId = assignment.owner?.id ?? ''
  ownerForm.ownershipPercentage = assignment.ownershipPercentage ?? ''
}

async function saveOwnerAssignment() {
  if (!selected.value?.id) return
  relationLoading.value = true
  actionError.value = ''
  actionSuccess.value = ''
  const payload = {
    ownerId: Number(ownerForm.ownerId),
    ownershipPercentage: Number(ownerForm.ownershipPercentage),
  }
  try {
    if (editingOwner.value?.owner?.id) {
      await api.put(`/api/v1/properties/${selected.value.id}/owners/${editingOwner.value.owner.id}`, payload)
      actionSuccess.value = 'Asignacion actualizada.'
    } else {
      await api.post(`/api/v1/properties/${selected.value.id}/owners`, payload)
      actionSuccess.value = 'Propietario asignado.'
    }
    Object.assign(ownerForm, { ownerId: '', ownershipPercentage: '' })
    editingOwner.value = null
    await loadPropertyOwners()
  } catch (requestError) {
    actionError.value = requestError.message
  } finally {
    relationLoading.value = false
  }
}

async function removeOwnerAssignment(assignment) {
  if (!selected.value?.id || !window.confirm('Eliminar la asignacion de propietario?')) return
  relationLoading.value = true
  actionError.value = ''
  try {
    await api.delete(`/api/v1/properties/${selected.value.id}/owners/${assignment.owner.id}`)
    actionSuccess.value = 'Asignacion eliminada.'
    await loadPropertyOwners()
  } catch (requestError) {
    actionError.value = requestError.message
  } finally {
    relationLoading.value = false
  }
}

async function loadPayments() {
  if (!selected.value?.id) return
  relationLoading.value = true
  actionError.value = ''
  try {
    payments.value = await api.get(`/api/v1/liquidations/${selected.value.id}/payments`)
  } catch (requestError) {
    actionError.value = requestError.message
  } finally {
    relationLoading.value = false
  }
}

async function registerPayment() {
  if (!selected.value?.id) return
  relationLoading.value = true
  actionError.value = ''
  actionSuccess.value = ''
  try {
    await api.post(`/api/v1/liquidations/${selected.value.id}/payments`, {
      amount: Number(paymentForm.amount),
      paymentMethod: paymentForm.paymentMethod,
      reference: paymentForm.reference || null,
    })
    actionSuccess.value = 'Pago registrado.'
    Object.assign(paymentForm, { amount: '', paymentMethod: '', reference: '' })
    await loadPayments()
    await refreshResource()
  } catch (requestError) {
    actionError.value = requestError.message
  } finally {
    relationLoading.value = false
  }
}

async function generateCertificate() {
  if (!selected.value?.id) return
  relationLoading.value = true
  actionError.value = ''
  actionSuccess.value = ''
  try {
    const certificate = await api.post(`/api/v1/liquidations/${selected.value.id}/clearance-certificates`, {})
    actionSuccess.value = `Certificado generado: ${certificate.certificateNumber}`
  } catch (requestError) {
    actionError.value = requestError.message
  } finally {
    relationLoading.value = false
  }
}

watch(() => props.resource.key, async () => {
  selected.value = null
  Object.keys(filters).forEach((key) => delete filters[key])
  resetActionForms()
  props.resource.filters?.forEach((field) => { filters[field.key] = '' })
  await loadReferences([...(props.resource.fields || []), ...(props.resource.filters || []), ...propertyOwnerFields])
})

watch(rows, (currentRows) => {
  if (selected.value?.id) {
    selected.value = currentRows.find((row) => row.id === selected.value.id) || selected.value
  }
})

onMounted(async () => {
  props.resource.filters?.forEach((field) => { filters[field.key] = '' })
  resetActionForms()
  await loadReferences([...(props.resource.fields || []), ...(props.resource.filters || []), ...propertyOwnerFields])
})
</script>

<template>
  <section class="page-section">
    <PageHeader
      :action-label="actionLabel"
      :eyebrow="resource.eyebrow"
      :show-action="canCreate"
      :title="resource.title"
      @action="resetForm"
    />

    <div v-if="error" class="mb-5 rounded-lg border border-rose-200 bg-rose-50 p-4 text-sm font-medium text-rose-700" role="alert">
      <div class="flex gap-2">
        <AlertCircle class="h-5 w-5 shrink-0" />
        <span>{{ error }}</span>
      </div>
    </div>

    <div v-if="filterFields.length" class="mb-5 rounded-lg border border-sky-100 bg-white p-4 shadow-soft">
      <form class="grid gap-4 md:grid-cols-[repeat(2,minmax(0,1fr))_auto]" @submit.prevent="applyFilters">
        <AppInput
          v-for="field in filterFields"
          :key="field.key"
          :field="{ ...field, required: false }"
          v-model="filters[field.key]"
        />
        <div class="flex items-end">
          <AppButton :icon="RefreshCw" type="submit" variant="secondary">Aplicar filtros</AppButton>
        </div>
      </form>
    </div>

    <div class="grid gap-6 xl:grid-cols-[minmax(0,1fr)_420px]">
      <AppCard>
        <div class="card-header">
          <div>
            <h2 class="section-title">Registros</h2>
            <p class="muted-text">{{ rows.length }} registros disponibles</p>
          </div>
          <div class="flex gap-2">
            <AppButton :disabled="loading" :icon="RefreshCw" variant="secondary" @click="refreshResource">Refrescar</AppButton>
            <AppButton v-if="canCreate" :icon="Plus" @click="resetForm">Crear</AppButton>
          </div>
        </div>
        <div v-if="loading" class="py-12 text-center text-sm text-slate-500">Cargando registros...</div>
        <DataTable
          v-else
          :can-delete="canDelete"
          :can-update="canUpdate"
          :columns="resource.columns"
          :rows="rows"
          :selected-id="selected?.id"
          @delete="remove"
          @edit="editRecord"
          @select="selectRecord"
        />
      </AppCard>

      <div class="space-y-6">
        <ResourceSummary :resource="resource" />

        <AppCard v-if="canCreate || canUpdate">
          <h2 class="section-title">{{ editing ? `Editar ${resource.singular}` : `Crear ${resource.singular}` }}</h2>
          <p class="muted-text">{{ editing ? `Registro #${editing.id}` : 'Complete los campos requeridos.' }}</p>
          <div class="mt-4">
            <CrudFormPreview
              :editing="editing"
              :fields="displayFields"
              :form="form"
              :saving="saving"
              @cancel="resetForm"
              @save="save"
              @update:field="updateField"
            />
          </div>
        </AppCard>

        <AppCard v-if="selected">
          <div class="flex items-center gap-3">
            <span class="icon-tile bg-sky-100 text-sky-700">
              <ClipboardList class="h-5 w-5" />
            </span>
            <div>
              <h2 class="section-title">Detalle del registro</h2>
              <p class="muted-text">Seleccion #{{ selected.id }}</p>
            </div>
          </div>
          <dl class="mt-4 grid gap-3 text-sm">
            <div v-for="[key, value] in selectedDetails" :key="key" class="rounded-lg bg-sky-50 p-3">
              <dt class="font-semibold text-slate-700">{{ key }}</dt>
              <dd class="mt-1 break-words text-slate-600">{{ typeof value === 'object' && value !== null ? JSON.stringify(value) : value ?? '-' }}</dd>
            </div>
          </dl>
        </AppCard>

        <AppCard v-if="resource.key === 'properties' && selected">
          <div class="card-header">
            <div class="flex items-center gap-3">
              <span class="icon-tile bg-cyan-100 text-cyan-700">
                <UsersRound class="h-5 w-5" />
              </span>
              <div>
                <h2 class="section-title">Propietarios del predio</h2>
                <p class="muted-text">{{ selected.cadastralCode }}</p>
              </div>
            </div>
            <AppButton :disabled="relationLoading" :icon="RefreshCw" variant="secondary" @click="loadPropertyOwners">Actualizar</AppButton>
          </div>

          <div v-if="actionError" class="mb-4 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">{{ actionError }}</div>
          <div v-if="actionSuccess" class="mb-4 rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">{{ actionSuccess }}</div>

          <form class="grid gap-4" @submit.prevent="saveOwnerAssignment">
            <AppInput
              v-for="field in ownerFields"
              :key="field.key"
              :field="field"
              :model-value="ownerForm[field.key]"
              @update:model-value="updateOwnerField(field.key, $event)"
            />
            <div class="flex flex-wrap gap-2">
              <button class="btn btn-primary" :disabled="relationLoading" type="submit">
                <Save class="h-4 w-4" />
                {{ editingOwner ? 'Actualizar asignacion' : 'Asignar propietario' }}
              </button>
              <AppButton v-if="editingOwner" variant="secondary" @click="resetActionForms">Cancelar</AppButton>
            </div>
          </form>

          <div class="mt-5 space-y-3">
            <div v-for="assignment in propertyOwners" :key="assignment.id" class="rounded-lg border border-sky-100 p-3">
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p class="font-semibold text-slate-900">{{ assignment.owner.fullName }}</p>
                  <p class="text-sm text-slate-500">{{ assignment.owner.identificationNumber }} · {{ assignment.ownershipPercentage }}%</p>
                </div>
                <div class="flex gap-2">
                  <button class="table-link" type="button" @click="fillOwnerAssignment(assignment)">Editar</button>
                  <button class="table-link danger" type="button" @click="removeOwnerAssignment(assignment)">
                    <Trash2 class="inline h-4 w-4" />
                    Eliminar
                  </button>
                </div>
              </div>
            </div>
            <p v-if="propertyOwners.length === 0" class="text-sm text-slate-500">No hay propietarios asignados.</p>
          </div>
        </AppCard>

        <AppCard v-if="resource.key === 'liquidations' && selected">
          <div class="card-header">
            <div class="flex items-center gap-3">
              <span class="icon-tile bg-blue-100 text-blue-700">
                <WalletCards class="h-5 w-5" />
              </span>
              <div>
                <h2 class="section-title">Pagos y certificado</h2>
                <p class="muted-text">{{ selected.cadastralCode }} · saldo {{ money(selected.balance) }}</p>
              </div>
            </div>
            <StatusBadge :label="String(selected.status)" tone="sky" />
          </div>

          <div v-if="actionError" class="mb-4 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">{{ actionError }}</div>
          <div v-if="actionSuccess" class="mb-4 rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">{{ actionSuccess }}</div>

          <form class="grid gap-4" @submit.prevent="registerPayment">
            <AppInput
              v-for="field in paymentFields"
              :key="field.key"
              :field="field"
              :model-value="paymentForm[field.key]"
              @update:model-value="updatePaymentField(field.key, $event)"
            />
            <button class="btn btn-primary" :disabled="relationLoading" type="submit">
              <Save class="h-4 w-4" />
              Registrar pago
            </button>
          </form>

          <div class="mt-5 space-y-3">
            <div v-for="payment in payments" :key="payment.id" class="rounded-lg border border-sky-100 p-3">
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p class="font-semibold text-slate-900">{{ money(payment.amount) }}</p>
                  <p class="text-sm text-slate-500">{{ payment.paymentMethod }} · {{ date(payment.paymentDate) }}</p>
                </div>
                <StatusBadge :label="String(payment.status)" tone="emerald" />
              </div>
            </div>
            <p v-if="payments.length === 0" class="text-sm text-slate-500">No hay pagos registrados.</p>
          </div>

          <button class="btn btn-secondary mt-5 w-full" :disabled="relationLoading" type="button" @click="generateCertificate">
            <FileCheck2 class="h-4 w-4" />
            Generar paz y salvo
          </button>
        </AppCard>
      </div>
    </div>
  </section>
</template>
