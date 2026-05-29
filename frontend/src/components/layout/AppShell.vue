<script setup>
import { computed, ref } from 'vue'
import { LogOut, Menu, ShieldCheck, X } from '@lucide/vue'
import { useRouter } from 'vue-router'
import { navGroups } from '../../data/resources'
import { authState, logout } from '../../stores/auth'

const router = useRouter()
const sidebarOpen = ref(false)
const roleLabel = computed(() => authState.roles?.[0]?.replace('ROLE_', '').replaceAll('_', ' ') || 'Usuario')

function endSession() {
  logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="min-h-screen bg-sky-50 text-slate-800">
    <div class="flex min-h-screen">
      <aside class="app-sidebar" :class="sidebarOpen ? 'is-open' : ''" aria-label="Navegacion principal">
        <RouterLink class="brand-lockup" to="/">
          <span class="brand-mark">
            <ShieldCheck class="h-6 w-6" />
          </span>
          <span>
            <span class="block text-lg font-semibold text-slate-950">IDEAL Predial</span>
            <span class="block text-sm text-slate-500">Gestion tributaria municipal</span>
          </span>
        </RouterLink>

        <button class="icon-button absolute right-4 top-4 lg:hidden" type="button" aria-label="Cerrar menu" @click="sidebarOpen = false">
          <X class="h-5 w-5" />
        </button>

        <nav class="mt-8 space-y-6">
          <section v-for="group in navGroups" :key="group.label">
            <p class="nav-group">{{ group.label }}</p>
            <div class="mt-2 space-y-1">
              <RouterLink
                v-for="item in group.items"
                :key="item.label || item.title"
                class="nav-link"
                :to="item.path === '/' ? '/' : `/${item.path}`"
                @click="sidebarOpen = false"
              >
                <component :is="item.icon" class="h-5 w-5" />
                <span>{{ item.label || item.title }}</span>
              </RouterLink>
            </div>
          </section>
        </nav>

        <div class="mt-8 rounded-lg border border-sky-100 bg-sky-50 p-4">
          <div class="flex items-center gap-3">
            <span class="icon-tile bg-white text-sky-600">
              <ShieldCheck class="h-5 w-5" />
            </span>
            <div>
              <p class="text-sm font-semibold text-slate-900">{{ authState.username }}</p>
              <p class="text-xs text-slate-500">{{ roleLabel }}</p>
            </div>
          </div>
          <button class="mt-4 inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-sky-700" type="button" @click="endSession">
            <LogOut class="h-4 w-4" />
            Cerrar sesion
          </button>
        </div>
      </aside>
      <div v-if="sidebarOpen" class="fixed inset-0 z-30 bg-slate-900/40 lg:hidden" @click="sidebarOpen = false"></div>

      <main class="flex min-w-0 flex-1 flex-col">
        <header class="app-topbar">
          <button class="icon-button lg:hidden" type="button" aria-label="Abrir menu" @click="sidebarOpen = true">
            <Menu class="h-5 w-5" />
          </button>
          <div class="min-w-0">
            <p class="section-eyebrow">IDEAL 10</p>
            <p class="truncate text-sm text-slate-500">Predial, cartera, certificados y configuracion tributaria</p>
          </div>
          <div class="ml-auto flex items-center gap-2">
            <div class="hidden h-10 items-center rounded-lg bg-sky-100 px-3 text-sm font-semibold text-sky-800 sm:flex">
              {{ roleLabel }}
            </div>
          </div>
        </header>

        <RouterView :key="$route.name" />
      </main>
    </div>
  </div>
</template>
