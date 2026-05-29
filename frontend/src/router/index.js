import { createRouter, createWebHistory } from 'vue-router'
import AppShell from '../components/layout/AppShell.vue'
import DashboardPage from '../pages/DashboardPage.vue'
import CrudPage from '../pages/CrudPage.vue'
import AuthPage from '../pages/AuthPage.vue'
import { authResource, resources } from '../data/resources'

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
      path: '/',
      component: AppShell,
      children: [
        { path: '', name: 'dashboard', component: DashboardPage },
        ...crudRoutes,
        { path: authResource.path, name: authResource.key, component: AuthPage },
      ],
    },
  ],
})
