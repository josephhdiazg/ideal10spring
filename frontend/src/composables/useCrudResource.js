import { computed, onMounted, reactive, ref, watch } from 'vue'
import { api } from '../api/client'

function emptyForm(fields) {
  return fields.reduce((values, field) => {
    values[field.key] = field.default ?? ''
    return values
  }, {})
}

function normalizeValue(value, field) {
  if ((field.type === 'number' || field.source) && value !== '') {
    return Number(value)
  }
  if (field.type === 'checkbox') {
    return Boolean(value)
  }
  return value === '' && field.nullable ? null : value
}

function toPayload(form, fields) {
  return fields.reduce((payload, field) => {
    if (field.readonly) {
      return payload
    }
    payload[field.key] = normalizeValue(form[field.key], field)
    return payload
  }, {})
}

export function useCrudResource(resource) {
  const rows = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref('')
  const editing = ref(null)
  const form = reactive(emptyForm(resource.fields))

  const canCreate = computed(() => resource.operations?.create !== false)
  const canUpdate = computed(() => resource.operations?.update !== false)
  const canDelete = computed(() => resource.operations?.delete !== false)

  function resetForm() {
    Object.assign(form, emptyForm(resource.fields))
    editing.value = null
  }

  function fillForm(row) {
    editing.value = row
    resource.fields.forEach((field) => {
      form[field.key] = field.fromRow ? field.fromRow(row) : (row[field.key] ?? field.default ?? '')
    })
  }

  async function load(params = {}) {
    loading.value = true
    error.value = ''
    try {
      const query = new URLSearchParams()
      Object.entries(params).forEach(([key, value]) => {
        if (value !== '' && value != null) {
          query.set(key, value)
        }
      })
      rows.value = await api.get(`${resource.endpoint}${query.toString() ? `?${query}` : ''}`)
    } catch (requestError) {
      error.value = requestError.message
    } finally {
      loading.value = false
    }
  }

  async function save() {
    saving.value = true
    error.value = ''
    const payload = toPayload(form, resource.fields)
    try {
      if (editing.value?.id && canUpdate.value) {
        await api.put(`${resource.endpoint}/${editing.value.id}`, payload)
      } else if (canCreate.value) {
        await api.post(resource.endpoint, payload)
      }
      resetForm()
      await load()
    } catch (requestError) {
      error.value = requestError.message
    } finally {
      saving.value = false
    }
  }

  async function remove(row) {
    if (!canDelete.value || !window.confirm(`Eliminar ${resource.title.toLowerCase()} #${row.id}?`)) {
      return
    }
    loading.value = true
    error.value = ''
    try {
      await api.delete(`${resource.endpoint}/${row.id}`)
      await load()
    } catch (requestError) {
      error.value = requestError.message
    } finally {
      loading.value = false
    }
  }

  watch(() => resource.key, () => {
    resetForm()
    load()
  })

  onMounted(load)

  return {
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
  }
}
