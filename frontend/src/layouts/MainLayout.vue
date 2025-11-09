<template>
  <div class="main-layout" :class="{ 'dark-theme': isDarkMode }">
    <header class="app-header">
      <div class="logo">Diakon<span>bank</span></div>
      <nav class="main-nav">
        <!-- Add nav links here if needed in the future -->
      </nav>
      <div class="header-right">
        <div v-if="isLoggedIn" class="user-info">
          <router-link to="/dashboard" class="username-link">{{ user?.username }}</router-link>
          <button class="icon-button">🔔</button>
          <button @click="handleLogout" class="logout-button">Выйти</button>
        </div>
        <button v-else @click="goToLogin" class="login-button">Войти</button>
        <button @click="toggleTheme" class="theme-switcher">
          <span v-if="isDarkMode">🌙</span>
          <span v-else>☀️</span>
        </button>
      </div>
    </header>
    <main class="app-content">
      <router-view />
    </main>
    <footer class="app-footer">
      <div class="footer-links">
        <router-link to="/tariffs">Тарифы</router-link>
        <router-link to="/about">О проекте</router-link>
        <router-link to="/support">Поддержка</router-link>
      </div>
      <div class="copyright">
        © Diakon-bank 2025
      </div>
    </footer>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
const isDarkMode = ref(true);
const router = useRouter();
const authStore = useAuthStore();
const user = computed(() => authStore.user);
const isLoggedIn = computed(() => !!authStore.token);
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value;
};
const handleLogout = () => {
  authStore.logout();
  window.location.href = '/login';
};
const goToLogin = () => {
  router.push({ name: 'login' });
}
</script>
<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: background-color 0.3s, color 0.3s;
}
.main-layout:not(.dark-theme) {
  background-color: #f0f8ff;
  color: #333;
}
.main-layout:not(.dark-theme) .app-header,
.main-layout:not(.dark-theme) .app-footer {
  background-color: #007bff;
  color: white;
}
.main-layout:not(.dark-theme) .logo {
  color: white;
}
.main-layout:not(.dark-theme) .logout-button {
  background-color: #dc3545;
}
.dark-theme {
  background-color: #1a1a2e;
  color: #e0e0e0;
}
.dark-theme .app-header,
.dark-theme .app-footer {
  background-color: #0f3460;
  color: white;
}
.dark-theme .logo {
  color: white;
}
.dark-theme .logout-button {
  background-color: #c82333;
}
.dark-theme .login-button {
  background-color: #007bff;
}
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.logo {
  font-size: 24px;
  font-weight: bold;
}
.logo span {
  font-weight: normal;
  color: #a0c4ff;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.username-link {
  color: white;
  text-decoration: none;
  font-weight: bold;
  padding: 5px;
  border-radius: 4px;
}
.username-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
.icon-button, .theme-switcher, .logout-button, .login-button {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 20px;
  padding: 5px;
}
.logout-button, .login-button {
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 5px;
  color: white;
}
.app-content {
  flex: 1;
  padding: 20px;
  display: flex;
}
.app-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
}
.footer-links {
  display: flex;
  gap: 30px;
}
.footer-links a {
  color: white;
  text-decoration: none;
}
.footer-links a:hover {
  text-decoration: underline;
}
.copyright {
  font-size: 14px;
}
</style>