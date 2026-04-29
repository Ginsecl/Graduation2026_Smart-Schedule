<template>
  <div class="todo-page">
    <div class="todo-header">
      <h2 class="page-title">待办事项</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>新建待办
      </el-button>
    </div>

    <div class="new-todo-bar">
      <el-input
        v-model="newTodoTitle"
        placeholder="输入待办内容，按回车创建"
        @keyup.enter="handleQuickCreate"
      >
        <template #append>
          <el-button @click="handleQuickCreate">添加</el-button>
        </template>
      </el-input>
    </div>

    <div class="todo-body" v-loading="todoStore.loading">
      <div class="todo-section">
        <div class="section-header" @click="showImportant = !showImportant">
          <el-icon :class="{ rotated: showImportant }"><ArrowRight /></el-icon>
          <span class="section-title">重要待办</span>
          <span class="section-count">{{ importantTodos.length }}</span>
        </div>
        <transition name="collapse">
          <div v-if="showImportant" class="section-body">
            <div v-for="todo in importantTodos" :key="todo.id"
              class="todo-item"
              :class="{ 'is-completed': todo.completed }"
            >
              <el-checkbox
                :model-value="todo.completed"
                @change="handleToggleComplete(todo)"
                size="large"
              />
              <span class="todo-title" :class="{ 'title-done': todo.completed }">
                {{ todo.title }}
              </span>
              <el-icon
                class="star-btn"
                :class="{ starred: todo.important }"
                @click="handleToggleImportant(todo)"
              >
                <StarFilled />
              </el-icon>
              <el-button text size="small" @click="handleEdit(todo)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button text size="small" type="danger" @click="handleDelete(todo)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-empty v-if="importantTodos.length === 0" description="暂无重要待办" :image-size="60" />
          </div>
        </transition>
      </div>

      <div class="todo-section">
        <div class="section-header" @click="showNormal = !showNormal">
          <el-icon :class="{ rotated: showNormal }"><ArrowRight /></el-icon>
          <span class="section-title">普通待办</span>
          <span class="section-count">{{ normalTodos.length }}</span>
        </div>
        <transition name="collapse">
          <div v-if="showNormal" class="section-body">
            <div v-for="todo in normalTodos" :key="todo.id"
              class="todo-item"
              :class="{ 'is-completed': todo.completed }"
            >
              <el-checkbox
                :model-value="todo.completed"
                @change="handleToggleComplete(todo)"
                size="large"
              />
              <span class="todo-title" :class="{ 'title-done': todo.completed }">
                {{ todo.title }}
              </span>
              <el-icon
                class="star-btn"
                :class="{ starred: todo.important }"
                @click="handleToggleImportant(todo)"
              >
                <StarFilled />
              </el-icon>
              <el-button text size="small" @click="handleEdit(todo)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button text size="small" type="danger" @click="handleDelete(todo)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-empty v-if="normalTodos.length === 0" description="暂无普通待办" :image-size="60" />
          </div>
        </transition>
      </div>

      <div class="todo-section">
        <div class="section-header" @click="showCompleted = !showCompleted">
          <el-icon :class="{ rotated: showCompleted }"><ArrowRight /></el-icon>
          <span class="section-title">已完成待办</span>
          <span class="section-count">{{ completedTodos.length }}</span>
        </div>
        <transition name="collapse">
          <div v-if="showCompleted" class="section-body">
            <div v-for="todo in completedTodos" :key="todo.id"
              class="todo-item is-completed"
            >
              <el-checkbox
                :model-value="todo.completed"
                @change="handleToggleComplete(todo)"
                size="large"
              />
              <span class="todo-title title-done">{{ todo.title }}</span>
              <el-icon
                class="star-btn"
                :class="{ starred: todo.important }"
                @click="handleToggleImportant(todo)"
              >
                <StarFilled />
              </el-icon>
              <el-button text size="small" type="danger" @click="handleDelete(todo)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-empty v-if="completedTodos.length === 0" description="暂无已完成待办" :image-size="60" />
          </div>
        </transition>
      </div>
    </div>

    <el-dialog v-model="showCreateDialog" :title="editingTodoId ? '编辑待办' : '新建待办'" width="420px" destroy-on-close @closed="resetForm">
      <el-form :model="todoForm" label-width="80px">
        <el-form-item label="内容" required>
          <el-input v-model="todoForm.title" placeholder="待办内容" />
        </el-form-item>
        <el-form-item label="重要待办">
          <el-switch v-model="todoForm.important" active-text="是" inactive-text="否" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          {{ editingTodoId ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useTodoStore } from '@/stores/todo'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Todo } from '@/types'

const todoStore = useTodoStore()

const showCreateDialog = ref(false)
const editingTodoId = ref<number | null>(null)
const saving = ref(false)
const newTodoTitle = ref('')
const showImportant = ref(true)
const showNormal = ref(true)
const showCompleted = ref(false)

const todoForm = ref({ title: '', important: false })

const importantTodos = computed(() =>
  todoStore.todos.filter(t => t.important && !t.completed)
)

const normalTodos = computed(() =>
  todoStore.todos.filter(t => !t.important && !t.completed)
)

const completedTodos = computed(() =>
  todoStore.todos.filter(t => t.completed)
)

async function handleQuickCreate() {
  const title = newTodoTitle.value.trim()
  if (!title) return
  try {
    await todoStore.createTodo({ title })
    ElMessage.success('已添加')
    newTodoTitle.value = ''
  } catch {
    // handled
  }
}

async function handleToggleComplete(todo: Todo) {
  try {
    await todoStore.toggleComplete(todo.id)
  } catch {
    // handled
  }
}

async function handleToggleImportant(todo: Todo) {
  try {
    await todoStore.updateTodo(todo.id, { important: !todo.important })
  } catch {
    // handled
  }
}

function handleEdit(todo: Todo) {
  editingTodoId.value = todo.id
  todoForm.value = { title: todo.title, important: todo.important || false }
  showCreateDialog.value = true
}

async function handleSave() {
  if (!todoForm.value.title.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  saving.value = true
  try {
    if (editingTodoId.value) {
      await todoStore.updateTodo(editingTodoId.value, todoForm.value)
      ElMessage.success('已更新')
    } else {
      await todoStore.createTodo(todoForm.value)
      ElMessage.success('创建成功')
    }
    showCreateDialog.value = false
    resetForm()
  } catch {
    // handled
  } finally {
    saving.value = false
  }
}

async function handleDelete(todo: Todo) {
  try {
    await ElMessageBox.confirm('确定删除此待办？', '确认删除', { type: 'warning' })
    await todoStore.deleteTodo(todo.id)
    ElMessage.success('已删除')
  } catch {
    // cancelled
  }
}

function resetForm() {
  editingTodoId.value = null
  todoForm.value = { title: '', important: false }
}

onMounted(() => {
  todoStore.fetchTodos()
})
</script>

<style scoped lang="scss">
.todo-page {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin: 0;
}

.new-todo-bar {
  margin-bottom: 12px;
}

.todo-body {
  flex: 1;
  overflow-y: auto;
}

.todo-section {
  margin-bottom: 8px;
  background: #fff;
  border-radius: $radius-md;
  overflow: hidden;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  cursor: pointer;
  user-select: none;
  border-bottom: 1px solid $border-color;

  .el-icon {
    transition: transform 0.2s;
    font-size: 13px;
    color: $text-secondary;

    &.rotated {
      transform: rotate(90deg);
    }
  }
}

.section-title {
  flex: 1;
  font-size: 13px;
  font-weight: 600;
  color: $text-primary;
}

.section-count {
  font-size: 11px;
  color: $text-secondary;
  background: #f0f0f0;
  border-radius: 8px;
  padding: 1px 7px;
}

.section-body {
  padding: 4px 0;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  transition: background 0.15s;

  &:hover {
    background: #f5f7fa;
  }

  &.is-completed {
    color: #bbb;

    .todo-title {
      text-decoration: line-through;
    }
  }
}

.todo-title {
  flex: 1;
  font-size: 13px;
  color: $text-primary;

  &.title-done {
    color: #bbb;
    text-decoration: line-through;
  }
}

.star-btn {
  font-size: 14px;
  color: #ccc;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: #FFB300;
  }

  &.starred {
    color: #FFB300;
  }
}

.collapse-enter-active,
.collapse-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  opacity: 0;
}
</style>
