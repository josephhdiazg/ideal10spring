<script setup>
import { computed } from 'vue'
import { AlertCircle, Database, Plus, RefreshCw } from '@lucide/vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppCard from '../components/atoms/AppCard.vue'
import DataTable from '../components/atoms/DataTable.vue'
import PageHeader from '../components/atoms/PageHeader.vue'
import CrudFormPreview from '../components/modules/CrudFormPreview.vue'
import EndpointChips from '../components/modules/EndpointChips.vue'
import ResourceSummary from '../components/modules/ResourceSummary.vue'
import { useCrudResource } from '../composables/useCrudResource'

const props = defineProps({
  resource: {
    type: Object,
    required: true,
  },
})

const actionLabel = computed(() => `Nuevo ${props.resource.title.toLowerCase()}`)
const controllerActions = computed(() => [
  { method: 'GET', path: props.resource.endpoint },
  ...(props.resource.operations.create ? [{ method: 'POST', path: props.resource.endpoint }] : []),
  { method: 'GET', path: `${props.resource.endpoint}/{id}` },
  ...(props.resource.operations.update ? [{ method: 'PUT', path: `${props.resource.endpoint}/{id}` }] : []),
  ...(props.resource.operations.delete ? [{ method: 'DELETE', path: `${props.resource.endpoint}/{id}` }] : []),
  ...(props.resource.extraReads || []),
  ...(props.resource.relatedActions || []),
])

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

function updateField(key, value) {
  form[key] = value
}
</script>

<template>
  <section class="page-section">
    <PageHeader :action-label="actionLabel" :eyebrow="resource.eyebrow" :title="resource.title" />

    <div v-if="error" class="mb-5 rounded-lg border border-rose-100 bg-rose-50 p-4 text-sm text-rose-700">
      <div class="flex gap-2">
        <AlertCircle class="h-5 w-5 shrink-0" />
        <span>{{ error }}</span>
      </div>
    </div>

    <div class="grid gap-6 xl:grid-cols-[1fr_360px]">
      <AppCard>
        <div class="card-header">
          <div>
            <h2 class="section-title">Registros</h2>
            <p class="muted-text">{{ resource.endpoint }}</p>
          </div>
          <div class="flex gap-2">
            <AppButton :icon="RefreshCw" variant="secondary" @click="load">Refrescar</AppButton>
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
          @delete="remove"
          @edit="fillForm"
        />
      </AppCard>

      <div class="space-y-6">
        <ResourceSummary :resource="resource" />

        <AppCard>
          <div class="flex items-center gap-3">
            <span class="icon-tile bg-sky-100 text-sky-700">
              <Database class="h-5 w-5" />
            </span>
            <div>
              <h2 class="section-title">Operaciones</h2>
              <p class="muted-text">Controlador backend</p>
            </div>
          </div>
          <div class="mt-4">
            <EndpointChips :actions="controllerActions" />
          </div>
        </AppCard>

        <AppCard v-if="canCreate || canUpdate">
          <h2 class="section-title">Formulario</h2>
          <p class="muted-text">{{ editing ? `Editando #${editing.id}` : `Crear ${resource.singular}` }}</p>
          <div class="mt-4">
            <CrudFormPreview
              :editing="editing"
              :fields="resource.fields"
              :form="form"
              :saving="saving"
              @cancel="resetForm"
              @save="save"
              @update:field="updateField"
            />
          </div>
        </AppCard>
      </div>
    </div>
  </section>
</template>
