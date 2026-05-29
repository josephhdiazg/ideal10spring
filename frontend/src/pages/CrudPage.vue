<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { AlertCircle, ClipboardList, FileCheck2, RefreshCw, Save, Trash2, UsersRound, WalletCards } from '@lucide/vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppCard from '../components/atoms/AppCard.vue'
import AppInput from '../components/atoms/AppInput.vue'
import CrudModal from '../components/atoms/CrudModal.vue'
import DataTable from '../components/atoms/DataTable.vue'
import PageHeader from '../components/atoms/PageHeader.vue'
import StatusBadge from '../components/atoms/StatusBadge.vue'
import CrudFormPreview from '../components/modules/CrudFormPreview.vue'
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
const formOpen = ref(false)
const detailsOpen = ref(false)

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
const selectedDetails = computed(() =>
  selected.value ? Object.entries(selected.value).filter(([key]) => key !== 'details') : []
)
const formTitle = computed(() =>
  editing.value ? `Editar ${props.resource.singular}` : `Nuevo ${props.resource.singular}`
)
const formSubtitle = computed(() =>
  editing.value ? `Registro #${editing.value.id}` : 'Complete los campos requeridos.'
)

function withReferenceOptions(fields) {
  return fields.map((field) => {
    if (!field.source) return field
    return { ...field, options: references[field.source.endpoint] || [] }
  })
}

function labelFor(item, labelKey) {
  return typeof labelKey === 'function' ? labelKey(item) : item[labelKey]
}

async function loadReferences(fields = []) {
  const sources = fields.map((field) => field.source).filter(Boolean)
  await Promise.all(
    [...new Map(sources.map((item) => [item.endpoint, item])).values()].map(async (source) => {
      const items = await api.get(source.endpoint)
      references[source.endpoint] = items.map((item) => ({
        label: labelFor(item, source.labelKey),
        value: item[source.valueKey],
      }))
    })
  )
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

function openCreateForm() {
  resetForm()
  formOpen.value = true
}

function closeForm() {
  resetForm()
  formOpen.value = false
}

function editRecord(row) {
  fillForm(row)
  formOpen.value = true
}

async function saveAndClose() {
  await save()
  if (!error.value) {
    formOpen.value = false
  }
}

async function selectRecord(row) {
  selected.value = row
  resetActionForms()
  detailsOpen.value = true
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
  formOpen.value = false
  detailsOpen.value = false
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
      :action-label="`Nuevo ${resource.singular}`"
      :eyebrow="resource.eyebrow"
      :show-action="canCreate"
      :title="resource.title"
      @action="openCreateForm"
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

    <AppCard>
      <div class="card-header">
        <div>
          <h2 class="section-title">Registros</h2>
          <p class="muted-text">{{ rows.length }} registros disponibles</p>
        </div>
        <div class="flex gap-2">
          <AppButton :disabled="loading" :icon="RefreshCw" variant="secondary" @click="refreshResource">
            Refrescar
          </AppButton>
          <AppButton v-if="canCreate" @click="openCreateForm">
            Nuevo {{ resource.singular }}
          </AppButton>
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

    <!-- Create / Edit modal -->
    <CrudModal
      :open="formOpen"
      :title="formTitle"
      :subtitle="formSubtitle"
      @close="closeForm"
    >
      <div v-if="error" class="mb-4 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">
        <div class="flex gap-2">
          <AlertCircle class="h-4 w-4 shrink-0 mt-0.5" />
          <span>{{ error }}</span>
        </div>
      </div>
      <CrudFormPreview
        :editing="editing"
        :fields="displayFields"
        :form="form"
        :saving="saving"
        @cancel="closeForm"
        @save="saveAndClose"
        @update:field="updateField"
      />
    </CrudModal>

    <!-- Record details modal -->
    <CrudModal
      :open="detailsOpen"
      :title="`Detalle — ${resource.singular}`"
      :subtitle="selected ? `Registro #${selected.id}` : ''"
      max-width="max-w-3xl"
      @close="detailsOpen = false"
    >
      <div v-if="actionError" class="mb-4 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">{{ actionError }}</div>
      <div v-if="actionSuccess" class="mb-4 rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700">{{ actionSuccess }}</div>

      <!-- Raw key-value details -->
      <div v-if="selected">
        <div class="flex items-center gap-3 mb-4">
          <span class="icon-tile bg-sky-100 text-sky-700">
            <ClipboardList class="h-5 w-5" />
          </span>
          <h3 class="section-title">Campos del registro</h3>
        </div>
        <dl class="grid gap-2 sm:grid-cols-2 mb-6">
          <div v-for="[key, value] in selectedDetails" :key="key" class="rounded-lg bg-sky-50 p-3">
            <dt class="text-xs font-semibold uppercase tracking-wide text-slate-500">{{ key }}</dt>
            <dd class="mt-1 break-words text-sm font-medium text-slate-800">
              {{ typeof value === 'object' && value !== null ? JSON.stringify(value) : value ?? '-' }}
            </dd>
          </div>
        </dl>
      </div>

      <!-- Property owners panel -->
      <template v-if="resource.key === 'properties' && selected">
        <div class="border-t border-sky-100 pt-5">
          <div class="card-header mb-4">
            <div class="flex items-center gap-3">
              <span class="icon-tile bg-cyan-100 text-cyan-700">
                <UsersRound class="h-5 w-5" />
              </span>
              <div>
                <h3 class="section-title">Propietarios del predio</h3>
                <p class="muted-text">{{ selected.cadastralCode }}</p>
              </div>
            </div>
            <AppButton :disabled="relationLoading" :icon="RefreshCw" variant="secondary" @click="loadPropertyOwners">
              Actualizar
            </AppButton>
          </div>

          <form class="grid gap-4 sm:grid-cols-2 mb-4" @submit.prevent="saveOwnerAssignment">
            <AppInput
              v-for="field in ownerFields"
              :key="field.key"
              :field="field"
              :model-value="ownerForm[field.key]"
              @update:model-value="updateOwnerField(field.key, $event)"
            />
            <div class="flex flex-wrap gap-2 sm:col-span-2">
              <button class="btn btn-primary" :disabled="relationLoading" type="submit">
                <Save class="h-4 w-4" />
                {{ editingOwner ? 'Actualizar asignacion' : 'Asignar propietario' }}
              </button>
              <AppButton v-if="editingOwner" variant="secondary" @click="resetActionForms">Cancelar</AppButton>
            </div>
          </form>

          <div class="space-y-3">
            <div
              v-for="assignment in propertyOwners"
              :key="assignment.id"
              class="rounded-lg border border-sky-100 p-3"
            >
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
        </div>
      </template>

      <!-- Liquidation payments panel -->
      <template v-if="resource.key === 'liquidations' && selected">
        <div class="border-t border-sky-100 pt-5">
          <div class="card-header mb-4">
            <div class="flex items-center gap-3">
              <span class="icon-tile bg-blue-100 text-blue-700">
                <WalletCards class="h-5 w-5" />
              </span>
              <div>
                <h3 class="section-title">Pagos</h3>
                <p class="muted-text">Saldo pendiente: {{ money(selected.balance) }}</p>
              </div>
            </div>
            <StatusBadge :label="String(selected.status)" tone="sky" />
          </div>

          <form class="grid gap-4 sm:grid-cols-2 mb-4" @submit.prevent="registerPayment">
            <AppInput
              v-for="field in paymentFields"
              :key="field.key"
              :field="field"
              :model-value="paymentForm[field.key]"
              @update:model-value="updatePaymentField(field.key, $event)"
            />
            <div class="sm:col-span-2">
              <button class="btn btn-primary" :disabled="relationLoading" type="submit">
                <Save class="h-4 w-4" />
                Registrar pago
              </button>
            </div>
          </form>

          <div class="space-y-3 mb-4">
            <div
              v-for="payment in payments"
              :key="payment.id"
              class="rounded-lg border border-sky-100 p-3"
            >
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

          <button class="btn btn-secondary w-full" :disabled="relationLoading" type="button" @click="generateCertificate">
            <FileCheck2 class="h-4 w-4" />
            Generar paz y salvo
          </button>
        </div>
      </template>
    </CrudModal>
  </section>
</template>
