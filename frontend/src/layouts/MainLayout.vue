<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="logo">
        <el-icon :size="22"><Calendar /></el-icon>
        <span class="logo-text">SmartSchedule</span>
      </div>
      <nav class="nav-menu">
        <router-link to="/calendar" class="nav-item" :class="{ active: $route.path === '/calendar' }">
          <el-icon :size="18"><Clock /></el-icon>
          <span>日历</span>
        </router-link>
        <router-link to="/todos" class="nav-item" :class="{ active: $route.path === '/todos' }">
          <el-icon :size="18"><List /></el-icon>
          <span>待办</span>
        </router-link>
        <router-link to="/statistics" class="nav-item" :class="{ active: $route.path === '/statistics' }">
          <el-icon :size="18"><DataAnalysis /></el-icon>
          <span>统计</span>
        </router-link>
        <router-link to="/settings" class="nav-item" :class="{ active: $route.path === '/settings' }">
          <el-icon :size="18"><Setting /></el-icon>
          <span>设置</span>
        </router-link>
      </nav>
      <div class="user-section">
        <el-dropdown trigger="click">
          <div class="user-avatar">
            <el-avatar :size="30" :icon="UserFilled" />
            <span class="username">{{ userStore.user?.nickname || '用户' }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { Calendar, Clock, List, DataAnalysis, Setting, UserFilled, SwitchButton } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 170px;
  background: #fff;
  border-right: 1px solid $border-color;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.logo {
  height: 48px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 8px;
  border-bottom: 1px solid $border-color;
  color: $primary-color;
}

.logo-text {
  font-size: 15px;
  font-weight: 700;
}

.nav-menu {
  flex: 1;
  padding: 8px 6px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: $radius-md;
  text-decoration: none;
  color: $text-regular;
  font-size: 13px;
  transition: all 0.2s;

  &:hover {
    background: #ecf5ff;
    color: $primary-color;
  }

  &.active {
    background: #ecf5ff;
    color: $primary-color;
    font-weight: 600;
  }
}

.user-section {
  padding: 10px 12px;
  border-top: 1px solid $border-color;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 13px;
  color: $text-primary;
}

.main-content {
  flex: 1;
  overflow: hidden;
  background: $bg-color;
}
</style>