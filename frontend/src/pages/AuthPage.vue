<script setup>
import { reactive, ref } from 'vue'
import { LockKeyhole, UserRound } from '@lucide/vue'
import AppButton from '../components/atoms/AppButton.vue'
import AppCard from '../components/atoms/AppCard.vue'
import AppInput from '../components/atoms/AppInput.vue'
import PageHeader from '../components/atoms/PageHeader.vue'
import EndpointChips from '../components/modules/EndpointChips.vue'
import { authResource } from '../data/resources'
import { authState, loadCurrentUser, login, logout } from '../stores/auth'

const credentials = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function submitLogin() {
  loading.value = true
  error.value = ''
  try {
    await login(credentials)
  } catch (requestError) {
    error.value = requestError.message
  } finally {
    loading.value = false
  }
}

async function refreshUser() {
  loading.value = true
  error.value = ''
  try {
    await loadCurrentUser()
  } catch (requestError) {
    error.value = requestError.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="page-section">
    <PageHeader action-label="Iniciar sesion" eyebrow="Seguridad" title="Autenticacion" :show-search="false" />

    <div class="grid gap-6 lg:grid-cols-[420px_1fr]">
      <AppCard>
        <div class="flex items-center gap-3">
          <span class="icon-tile bg-sky-100 text-sky-700">
            <LockKeyhole class="h-5 w-5" />
          </span>
          <div>
            <h2 class="section-title">Acceso</h2>
            <p class="muted-text">{{ authResource.endpoint }}</p>
          </div>
        </div>
        <div v-if="error" class="mt-4 rounded-lg border border-rose-100 bg-rose-50 p-3 text-sm text-rose-700">
          {{ error }}
        </div>
        <form class="mt-5 space-y-4" @submit.prevent="submitLogin">
          <AppInput :field="authResource.fields[0]" v-model="credentials.username" />
          <AppInput :field="authResource.fields[1]" v-model="credentials.password" />
          <button class="btn btn-primary" :disabled="loading" type="submit">
            <UserRound class="h-4 w-4" />
            {{ loading ? 'Entrando...' : 'Entrar' }}
          </button>
        </form>
      </AppCard>

      <AppCard>
        <div class="card-header">
          <div>
            <h2 class="section-title">Sesion actual</h2>
            <p class="muted-text">JWT y usuario autenticado</p>
          </div>
          <EndpointChips :actions="authResource.relatedActions" />
        </div>
        <div class="rounded-lg bg-sky-50 p-4">
          <p class="text-sm font-semibold text-slate-700">Usuario</p>
          <p class="mt-1 text-lg font-semibold text-slate-950">{{ authState.username || 'Sin sesion' }}</p>
          <p class="mt-3 text-sm font-semibold text-slate-700">Roles</p>
          <p class="mt-1 text-sm text-slate-600">{{ authState.roles.length ? authState.roles.join(', ') : 'Ninguno' }}</p>
        </div>
        <div class="mt-4 flex flex-wrap gap-2">
          <AppButton variant="secondary" @click="refreshUser">Validar /me</AppButton>
          <AppButton variant="ghost" @click="logout">Cerrar sesion</AppButton>
        </div>
      </AppCard>
    </div>
  </section>
</template>
