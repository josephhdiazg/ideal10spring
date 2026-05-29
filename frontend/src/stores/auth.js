import { reactive } from 'vue'
import { api } from '../api/client'

export const authState = reactive({
  token: localStorage.getItem('ideal10.token'),
  username: localStorage.getItem('ideal10.username'),
  roles: JSON.parse(localStorage.getItem('ideal10.roles') || '[]'),
})

export async function login(credentials) {
  const response = await api.post('/api/v1/auth/login', credentials)
  authState.token = response.token
  authState.username = response.username
  authState.roles = response.roles || []
  localStorage.setItem('ideal10.token', response.token)
  localStorage.setItem('ideal10.username', response.username)
  localStorage.setItem('ideal10.roles', JSON.stringify(authState.roles))
  return response
}

export async function loadCurrentUser() {
  const response = await api.get('/api/v1/auth/me')
  authState.username = response.username
  authState.roles = response.roles || []
  localStorage.setItem('ideal10.username', response.username)
  localStorage.setItem('ideal10.roles', JSON.stringify(authState.roles))
  return response
}

export function logout() {
  authState.token = null
  authState.username = null
  authState.roles = []
  localStorage.removeItem('ideal10.token')
  localStorage.removeItem('ideal10.username')
  localStorage.removeItem('ideal10.roles')
}
