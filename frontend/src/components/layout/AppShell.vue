<script setup>
import { Bell, Landmark, Menu, ShieldCheck } from '@lucide/vue'
import { navGroups } from '../../data/resources'
</script>

<template>
  <div class="min-h-screen bg-sky-50 text-slate-800">
    <div class="flex min-h-screen">
      <aside class="app-sidebar">
        <RouterLink class="brand-lockup" to="/">
          <span class="brand-mark">
            <Landmark class="h-6 w-6" />
          </span>
          <span>
            <span class="block text-lg font-semibold text-slate-950">IDEAL Predial</span>
            <span class="block text-sm text-slate-500">Gestion municipal</span>
          </span>
        </RouterLink>

        <nav class="mt-8 space-y-6">
          <section v-for="group in navGroups" :key="group.label">
            <p class="nav-group">{{ group.label }}</p>
            <div class="mt-2 space-y-1">
              <RouterLink
                v-for="item in group.items"
                :key="item.label || item.title"
                class="nav-link"
                :to="item.path === '/' ? '/' : `/${item.path}`"
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
              <p class="text-sm font-semibold text-slate-900">Acceso seguro</p>
              <p class="text-xs text-slate-500">JWT + roles</p>
            </div>
          </div>
        </div>
      </aside>

      <main class="flex min-w-0 flex-1 flex-col">
        <header class="app-topbar">
          <button class="icon-button lg:hidden" type="button" aria-label="Abrir menu">
            <Menu class="h-5 w-5" />
          </button>
          <div class="min-w-0">
            <p class="section-eyebrow">IDEAL 10</p>
            <p class="truncate text-sm text-slate-500">Predial, cartera y certificados</p>
          </div>
          <div class="ml-auto flex items-center gap-2">
            <button class="icon-button" type="button" aria-label="Notificaciones">
              <Bell class="h-5 w-5" />
            </button>
            <div class="hidden h-10 items-center rounded-lg bg-sky-100 px-3 text-sm font-semibold text-sky-800 sm:flex">
              Administrador
            </div>
          </div>
        </header>

        <RouterView :key="$route.name" />
      </main>
    </div>
  </div>
</template>
