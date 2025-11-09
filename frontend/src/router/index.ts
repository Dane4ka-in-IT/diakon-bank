import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AboutView from '../views/AboutView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import TariffsView from '../views/TariffsView.vue'
import FinancialPulseView from '../views/FinancialPulseView.vue'
import { useAuthStore } from '@/stores/auth'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/tariffs',
      name: 'tariffs',
      component: TariffsView
    },
    {
      path: '/financial-pulse',
      name: 'financial-pulse',
      component: FinancialPulseView,
      meta: { requiresAuth: true }
    }
  ]
})
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuthenticated = !!authStore.token
  if (to.name === 'home') {
    if (isAuthenticated) {
      return next({ name: 'dashboard' })
    } else {
      return next({ name: 'about' })
    }
  }
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
  } else if ((to.name === 'login' || to.name === 'register') && isAuthenticated) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})
export default router