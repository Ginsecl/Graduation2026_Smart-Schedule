import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/login/index.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/calendar',
      children: [
        {
          path: 'calendar',
          name: 'Calendar',
          component: () => import('@/pages/calendar/index.vue'),
          meta: { title: '日历' }
        },
        {
          path: 'todos',
          name: 'Todos',
          component: () => import('@/pages/todo/index.vue'),
          meta: { title: '待办' }
        },
        {
          path: 'statistics',
          name: 'Statistics',
          component: () => import('@/pages/statistics/index.vue'),
          meta: { title: '统计' }
        },
        {
          path: 'settings',
          name: 'Settings',
          component: () => import('@/pages/settings/index.vue'),
          meta: { title: '设置' }
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('accessToken')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/calendar')
  } else {
    next()
  }
})

export default router