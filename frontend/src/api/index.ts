import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => {
    const data = response.data as ApiResponse<unknown>
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return response
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
      window.location.href = '/login'
      return Promise.reject(error)
    }

    const status = error.response?.status
    const serverMessage = error.response?.data?.message

    if (serverMessage) {
      ElMessage.error(serverMessage)
    } else if (!error.response) {
      if (error.code === 'ECONNABORTED') {
        ElMessage.error('请求超时，请稍后重试')
      } else {
        ElMessage.error('网络连接失败，请检查网络')
      }
    } else if (status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (status === 403) {
      ElMessage.error('没有权限执行此操作')
    } else if (status === 405) {
      ElMessage.error('请求方法不允许')
    } else if (status === 409) {
      ElMessage.error('数据冲突，请刷新后重试')
    } else if (status === 422) {
      ElMessage.error('请求参数错误')
    } else if (status === 429) {
      ElMessage.error('请求过于频繁，请稍后重试')
    } else if (status && status >= 500) {
      ElMessage.error('服务器内部错误，请稍后重试')
    } else {
      ElMessage.error('请求失败，请稍后重试')
    }

    return Promise.reject(error)
  }
)

export const authAPI = {
  login: (data: { username: string; password: string }) => api.post('/auth/login', data),
  register: (data: { username: string; email: string; password: string; nickname?: string }) =>
    api.post('/auth/register', data),
  getProfile: () => api.get('/auth/profile'),
  updateProfile: (data: unknown) => api.put('/auth/profile', data),
  changePassword: (data: { oldPassword: string; newPassword: string }) =>
    api.put('/auth/change-password', data)
}

export const scheduleAPI = {
  create: (data: unknown) => api.post('/schedules', data),
  update: (id: number, data: unknown) => api.put(`/schedules/${id}`, data),
  delete: (id: number) => api.delete(`/schedules/${id}`),
  list: (start: string, end: string, type?: string, status?: string) =>
    api.get('/schedules', { params: { start, end, type, status } }),
  updateStatus: (id: number, status: string) =>
    api.patch(`/schedules/${id}/status`, null, { params: { status } }),
  checkConflict: (startTime: string, endTime: string, excludeScheduleId?: number) =>
    api.get('/schedules/check-conflict', { params: { startTime, endTime, excludeScheduleId } })
}

export const nlpAPI = {
  parseAndCreate: (text: string, userId: number) =>
    api.post('/nlp/parse-and-create', { text, userId }),
  parseAndCheck: (text: string, userId: number) =>
    api.post('/nlp/parse-and-check', { text, userId })
}

export const holidayAPI = {
  list: (year: number) => api.get('/public/holidays', { params: { year } })
}

export const statisticsAPI = {
  overview: () => api.get('/statistics/overview'),
  typeDistribution: () => api.get('/statistics/type-distribution'),
  weekly: () => api.get('/statistics/weekly')
}

export const todoAPI = {
  list: () => api.get('/todos'),
  create: (data: { title: string; important?: boolean }) => api.post('/todos', data),
  update: (id: number, data: { title?: string; important?: boolean }) => api.put(`/todos/${id}`, data),
  delete: (id: number) => api.delete(`/todos/${id}`),
  toggleComplete: (id: number) => api.patch(`/todos/${id}/toggle`)
}

export default api