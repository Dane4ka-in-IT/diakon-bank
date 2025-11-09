<template>
  <div class="register-container">
    <div class="register-box">
      <h1>Регистрация</h1>
      <form @submit.prevent="handleRegister">
        <div class="input-group">
          <input type="text" v-model="fullName" placeholder="Введите ФИО" required />
        </div>
        <div class="input-group">
          <input type="text" v-model="username" placeholder="Введите e-mail или телефон" required />
        </div>
        <div class="input-group">
          <input type="password" v-model="password" placeholder="Введите пароль" required />
        </div>
        <div class="input-group">
          <input type="password" v-model="confirmPassword" placeholder="Повторите пароль" required />
        </div>
        <button type="submit" class="register-button">Создать аккаунт</button>
      </form>
      <div class="links">
        <router-link to="/login">Уже есть аккаунт? Войти</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { authService } from '@/services/authService';

const fullName = ref('');
const username = ref('');
const password = ref('');
const confirmPassword = ref('');
const router = useRouter();

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    alert("Passwords do not match!");
    return;
  }
  try {
    // Note: The provided API for register only takes username and password.
    // The "fullName" field is included to match the UI but is not sent to the API.
    await authService.register(username.value, password.value);
    // After successful registration, log the user in automatically
    await authService.login(username.value, password.value);
    router.push({ name: 'dashboard' });
  } catch (error) {
    console.error('Registration failed:', error);
    // Here you could show an error message to the user
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1; /* This makes it fill the flex container from the layout */
  width: 100%;
}

.register-box {
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

.register-button {
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

.register-button:hover {
  background-color: #0056b3;
}

.links {
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
.main-layout:not(.dark-theme) .register-box {
  background-color: #ffffff;
  border-color: #dee2e6;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}
.main-layout:not(.dark-theme) .register-box h1 {
  color: #333;
}
.main-layout:not(.dark-theme) .register-box input {
  background-color: #f8f9fa;
  border-color: #ced4da;
  color: #495057;
}
.main-layout:not(.dark-theme) .register-box input::placeholder {
  color: #6c757d;
}
.main-layout:not(.dark-theme) .register-box .links a {
  color: #007bff;
}
</style> 