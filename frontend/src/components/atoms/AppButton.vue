<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
  },
  icon: {
    type: Object,
    default: null,
  },
  disabled: Boolean,
  type: {
    type: String,
    default: 'button',
  },
})

const variants = {
  primary: 'btn-primary',
  secondary: 'btn-secondary',
  ghost: 'btn-ghost',
}
</script>

<template>
  <button :type="type" class="btn" :class="variants[variant]" :disabled="disabled">
    <component :is="icon" v-if="icon" class="h-4 w-4" />
    <slot />
  </button>
</template>
