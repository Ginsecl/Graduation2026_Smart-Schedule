import { defineStore } from 'pinia'
import { ref } from 'vue'
import { todoAPI } from '@/api'
import type { Todo } from '@/types'

export const useTodoStore = defineStore('todo', () => {
  const todos = ref<Todo[]>([])
  const loading = ref(false)

  async function fetchTodos() {
    loading.value = true
    try {
      const res = await todoAPI.list()
      todos.value = res.data.data
    } finally {
      loading.value = false
    }
  }

  async function createTodo(data: { title: string; important?: boolean }) {
    const res = await todoAPI.create(data)
    const todo = res.data.data as Todo
    todos.value.unshift(todo)
    return todo
  }

  async function updateTodo(id: number, data: { title?: string; important?: boolean }) {
    const res = await todoAPI.update(id, data)
    const updated = res.data.data as Todo
    const idx = todos.value.findIndex(t => t.id === id)
    if (idx >= 0) todos.value[idx] = updated
    return updated
  }

  async function toggleComplete(id: number) {
    const res = await todoAPI.toggleComplete(id)
    const updated = res.data.data as Todo
    const idx = todos.value.findIndex(t => t.id === id)
    if (idx >= 0) todos.value[idx] = updated
    return updated
  }

  async function deleteTodo(id: number) {
    await todoAPI.delete(id)
    todos.value = todos.value.filter(t => t.id !== id)
  }

  return {
    todos, loading,
    fetchTodos, createTodo, updateTodo, toggleComplete, deleteTodo
  }
})
