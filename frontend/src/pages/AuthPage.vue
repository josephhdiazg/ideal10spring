<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { LockKeyhole, ShieldCheck } from '@lucide/vue'
import AppInput from '../components/atoms/AppInput.vue'
import { login } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const credentials = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

const fields = [
  { key: 'username', label: 'Correo institucional', type: 'email', required: true, autocomplete: 'username' },
  { key: 'password', label: 'Contrasena', type: 'password', required: true, autocomplete: 'current-password' },
]

async function submitLogin() {
  loading.value = true
  error.value = ''
  try {
    await login(credentials)
    await router.push(route.query.redirect || { name: 'dashboard' })
  } catch (requestError) {
    error.value = requestError.status === 401
      ? 'Las credenciales no son validas.'
      : requestError.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="grid min-h-screen bg-sky-50 px-4 py-8 lg:grid-cols-[minmax(0,1fr)_520px] lg:px-0 lg:py-0">
    <section class="hidden border-r border-sky-100 bg-white px-12 py-10 lg:flex lg:flex-col">
      <div class="brand-lockup px-0">
        <span class="brand-mark">
          <ShieldCheck class="h-6 w-6" />
        </span>
        <span>
          <span class="block text-lg font-semibold text-slate-950">IDEAL Predial</span>
          <span class="block text-sm text-slate-500">Gestion tributaria municipal</span>
        </span>
      </div>

      <div class="mt-auto max-w-xl pb-12">
        <p class="section-eyebrow">Acceso seguro</p>
        <h1 class="mt-3 text-4xl font-semibold leading-tight text-slate-950">Sistema corporativo para administracion predial.</h1>
        <p class="mt-4 text-base leading-7 text-slate-600">
          Ingrese con una cuenta autorizada para consultar predios, gestionar liquidaciones, registrar pagos y emitir certificados.
        </p>
      </div>
    </section>

    <section class="flex items-center justify-center">
      <form class="w-full max-w-md rounded-lg border border-sky-100 bg-white p-6 shadow-soft" @submit.prevent="submitLogin">
        <div class="flex items-center gap-3">
          <span class="icon-tile bg-sky-100 text-sky-700">
            <LockKeyhole class="h-5 w-5" />
          </span>
          <div>
            <p class="section-eyebrow">Autenticacion</p>
            <h2 class="section-title">Iniciar sesion</h2>
          </div>
        </div>

        <div v-if="error" class="mt-5 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm font-medium text-rose-700" role="alert">
          {{ error }}
        </div>

        <div class="mt-6 space-y-4">
          <AppInput
            v-for="field in fields"
            :key="field.key"
            :field="field"
            v-model="credentials[field.key]"
          />
        </div>

        <button class="btn btn-primary mt-6 w-full" :disabled="loading" type="submit">
          <LockKeyhole class="h-4 w-4" />
          {{ loading ? 'Validando credenciales...' : 'Entrar al sistema' }}
        </button>
      </form>
    </section>
  </main>
</template>
