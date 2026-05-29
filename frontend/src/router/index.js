import { createRouter, createWebHistory } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import DashboardPage from '../pages/DashboardPage.vue'
import CrudPage from '../pages/CrudPage.vue'
import AuthPage from '../pages/AuthPage.vue'
import ContribuyentePage from '../pages/ContribuyentePage.vue'
import { resources } from '../data/resources'
import { authState, isAuthenticated, restoreSession } from '../stores/auth'

const isContribuyente = () => authState.roles.includes('ROLE_CONTRIBUYENTE')

const crudRoutes = resources.map((resource) => ({
  path: resource.path,
  name: resource.key,
  component: CrudPage,
  props: { resource },
}))

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: AuthPage,
      meta: { public: true },
    },
    {
      path: '/contribuyente',
      name: 'contribuyente',
      component: ContribuyentePage,
    },
    {
      path: '/',
      component: AppShell,
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'dashboard', component: DashboardPage },
        ...crudRoutes,
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  if (!authState.ready) {
    await restoreSession()
  }

  // Login page: redirect authenticated users away
  if (to.meta.public && isAuthenticated.value) {
    return isContribuyente() ? { name: 'contribuyente' } : { name: 'dashboard' }
  }

  // Admin area: block unauthenticated users and CONTRIBUYENTE role
  if (to.meta.requiresAuth) {
    if (!isAuthenticated.value || isContribuyente()) {
      return { name: 'contribuyente' }
    }
  }

  // Contribuyente area: block authenticated non-CONTRIBUYENTE users
  if (to.name === 'contribuyente' && isAuthenticated.value && !isContribuyente()) {
    return { name: 'dashboard' }
  }

  return true
})

window.addEventListener('ideal10:unauthorized', () => {
  const current = router.currentRoute.value.name
  if (current !== 'login' && current !== 'contribuyente') {
    router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
  }
})
