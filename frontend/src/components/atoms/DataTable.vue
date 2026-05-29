<script setup>
import StatusBadge from './StatusBadge.vue'

defineProps({
  columns: {
    type: Array,
    required: true,
  },
  rows: {
    type: Array,
    required: true,
  },
  canUpdate: Boolean,
  canDelete: Boolean,
})

defineEmits(['edit', 'delete'])

function cellValue(row, column) {
  if (column.value) {
    return column.value(row)
  }
  return column.key.split('.').reduce((value, key) => value?.[key], row) ?? '-'
}

function isPositiveStatus(value) {
  return ['ACTIVE', 'PAID', 'ISSUED', true, 'true'].includes(value)
}
</script>

<template>
  <div class="overflow-x-auto">
    <table class="data-table">
      <thead>
        <tr>
          <th v-for="column in columns" :key="column.key" :class="column.align === 'right' ? 'text-right' : ''">
            {{ column.label }}
          </th>
          <th class="text-right">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, rowIndex) in rows" :key="rowIndex">
          <td
            v-for="column in columns"
            :key="`${rowIndex}-${column.key}`"
            :class="column.align === 'right' ? 'text-right' : ''"
          >
            <StatusBadge
              v-if="column.type === 'status'"
              :label="String(cellValue(row, column))"
              :tone="isPositiveStatus(cellValue(row, column)) ? 'emerald' : 'amber'"
            />
            <span v-else :class="column.key === columns[0].key ? 'font-semibold text-sky-700' : 'text-slate-600'">
              {{ cellValue(row, column) }}
            </span>
          </td>
          <td class="text-right">
            <button v-if="canUpdate" class="table-link" type="button" @click="$emit('edit', row)">Editar</button>
            <button v-if="canDelete" class="table-link danger" type="button" @click="$emit('delete', row)">Eliminar</button>
            <span v-if="!canUpdate && !canDelete" class="text-sm text-slate-400">Solo lectura</span>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="rows.length === 0" class="px-5 py-8 text-center text-sm text-slate-500">
      No hay registros para mostrar.
    </div>
  </div>
</template>
