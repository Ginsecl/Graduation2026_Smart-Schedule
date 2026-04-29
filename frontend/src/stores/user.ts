import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authAPI } from '@/api'
import type { User } from '@/types'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(
    JSON.parse(localStorage.getItem('user') || 'null')
  )
  const token = ref<string | null>(localStorage.getItem('accessToken'))

  async function login(username: string, password: string) {
    const res = await authAPI.login({ username, password })
    const data = res.data.data
    token.value = data.accessToken
    user.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('user', JSON.stringify(data.user))
  }

  async function register(username: string, email: string, password: string) {
    const res = await authAPI.register({ username, email, password })
    const data = res.data.data
    token.value = data.accessToken
    user.value = data.user
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('user', JSON.stringify(data.user))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('user')
  }

  return { user, token, login, register, logout }
})