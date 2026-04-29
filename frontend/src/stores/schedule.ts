import { defineStore } from 'pinia'
import { ref } from 'vue'
import { scheduleAPI } from '@/api'
import type { Schedule } from '@/types'

export const useScheduleStore = defineStore('schedule', () => {
  const schedules = ref<Schedule[]>([])
  const loading = ref(false)

  async function fetchSchedules(start: string, end: string) {
    loading.value = true
    try {
      const res = await scheduleAPI.list(start, end)
      schedules.value = res.data.data
    } finally {
      loading.value = false
    }
  }

  async function createSchedule(data: Partial<Schedule>) {
    const res = await scheduleAPI.create(data)
    return res.data.data as Schedule
  }

  async function updateSchedule(id: number, data: Partial<Schedule>) {
    const res = await scheduleAPI.update(id, data)
    return res.data.data as Schedule
  }

  async function completeSchedule(id: number) {
    const res = await scheduleAPI.updateStatus(id, 'COMPLETED')
    const updated = res.data.data as Schedule
    const idx = schedules.value.findIndex(s => s.id === id)
    if (idx >= 0) schedules.value[idx] = updated
    return updated
  }

  async function uncompleteSchedule(id: number) {
    const res = await scheduleAPI.updateStatus(id, 'SCHEDULED')
    const updated = res.data.data as Schedule
    const idx = schedules.value.findIndex(s => s.id === id)
    if (idx >= 0) schedules.value[idx] = updated
    return updated
  }

  async function deleteSchedule(id: number) {
    await scheduleAPI.delete(id)
  }

  return {
    schedules, loading,
    fetchSchedules, createSchedule, updateSchedule, completeSchedule, uncompleteSchedule, deleteSchedule
  }
})