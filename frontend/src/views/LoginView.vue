<template>
  <div class="login-container">
    <div class="login-box">
      <h1>Вход</h1>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <input type="text" v-model="username" placeholder="Введите e-mail или телефон" required />
        </div>
        <div class="input-group">
          <input type="password" v-model="password" placeholder="Введите пароль" required />
        </div>
        <button type="submit" class="login-button">Войти</button>
      </form>
      <div class="links">
        <router-link to="/register">Нет аккаунта? Регистрация</router-link>
        <a href="#">Забыли пароль?</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { authService } from '@/services/authService';

const username = ref('');
const password = ref('');
const router = useRouter();

const handleLogin = async () => {
  try {
    await authService.login(username.value, password.value);
    router.push({ name: 'dashboard' });
  } catch (error) {
    console.error('Login failed:', error);
    // Here you could show an error message to the user
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1; /* This makes it fill the flex container from the layout */
  width: 100%;
}

.login-box {
  background-color: #343a40;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  text-align: center;
  width: 100%;
  max-width: 400px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

h1 {
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 300;
}

.input-group {
  margin-bottom: 20px;
}

input {
  width: 100%;
  padding: 15px;
  border: 1px solid #495057;
  border-radius: 8px;
  background-color: #495057;
  color: white;
  box-sizing: border-box;
  font-size: 16px;
}

input::placeholder {
  color: #adb5bd;
}

input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.login-button {
  width: 100%;
  padding: 15px;
  border: none;
  border-radius: 8px;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-bottom: 25px;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #0056b3;
}

.links {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.links a {
  color: #6c757d;
  text-decoration: none;
}

.links a:hover {
  color: #00aaff;
  text-decoration: underline;
}

/* Light Theme Adjustments */
.main-layout:not(.dark-theme) .login-box {
  background-color: #ffffff;
  border-color: #dee2e6;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}
.main-layout:not(.dark-theme) .login-box h1 {
  color: #333;
}
.main-layout:not(.dark-theme) .login-box input {
  background-color: #f8f9fa;
  border-color: #ced4da;
  color: #495057;
}
.main-layout:not(.dark-theme) .login-box input::placeholder {
  color: #6c757d;
}
.main-layout:not(.dark-theme) .login-box .links a {
  color: #007bff;
}
</style> 