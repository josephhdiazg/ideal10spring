<script setup>
import { onMounted, onUnmounted } from 'vue'
import { X } from '@lucide/vue'

defineProps({
  open: Boolean,
  title: { type: String, default: '' },
  subtitle: { type: String, default: '' },
  maxWidth: { type: String, default: 'max-w-2xl' },
})

const emit = defineEmits(['close'])

function onKey(e) {
  if (e.key === 'Escape') emit('close')
}

onMounted(() => document.addEventListener('keydown', onKey))
onUnmounted(() => document.removeEventListener('keydown', onKey))
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="open" class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto p-4 sm:p-8">
        <div class="fixed inset-0 bg-slate-900/50 backdrop-blur-sm" @click="$emit('close')" />
        <div
          class="relative my-auto w-full rounded-xl bg-white shadow-2xl ring-1 ring-sky-100"
          :class="maxWidth"
        >
          <div class="flex items-start justify-between border-b border-sky-100 px-6 py-4">
            <div>
              <h2 class="text-base font-semibold text-slate-950">{{ title }}</h2>
              <p v-if="subtitle" class="mt-0.5 text-sm text-slate-500">{{ subtitle }}</p>
            </div>
            <button
              class="icon-button -mr-1 -mt-1 shrink-0"
              type="button"
              aria-label="Cerrar"
              @click="$emit('close')"
            >
              <X class="h-5 w-5" />
            </button>
          </div>
          <div class="px-6 py-5">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>
