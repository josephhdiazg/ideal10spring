import { createRouter, createWebHistory } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import DashboardPage from '../pages/DashboardPage.vue'
import CrudPage from '../pages/CrudPage.vue'
import AuthPage from '../pages/AuthPage.vue'
import { resources } from '../data/resources'
import { authState, isAuthenticated, restoreSession } from '../stores/auth'

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

  if (to.meta.public && isAuthenticated.value) {
    return { name: 'dashboard' }
  }

  if (to.meta.requiresAuth && !isAuthenticated.value) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  return true
})

window.addEventListener('ideal10:unauthorized', () => {
  if (router.currentRoute.value.name !== 'login') {
    router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
  }
})
