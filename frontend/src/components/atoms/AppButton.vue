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
})

const variants = {
  primary: 'btn-primary',
  secondary: 'btn-secondary',
  ghost: 'btn-ghost',
}
</script>

<template>
  <button type="button" class="btn" :class="variants[variant]">
    <component :is="icon" v-if="icon" class="h-4 w-4" />
    <slot />
  </button>
</template>
