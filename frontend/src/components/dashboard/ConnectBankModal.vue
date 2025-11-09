<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <button class="close-button" @click="close">&times;</button>
      <h3>Подключить новый банк</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="bankLogin">Логин от банка</label>
          <input type="text" id="bankLogin" v-model="bankLogin" required>
        </div>
        <div class="form-group">
          <label for="bankPassword">Пароль от банка</label>
          <input type="password" id="bankPassword" v-model="bankPassword" required>
        </div>
        <div class="form-group">
          <label for="bank">Банк</label>
          <select id="bank" v-model="bank" required>
            <option disabled value="">Выберите банк</option>
            <option value="VBANK">V-Bank</option>
            <option value="ABANK">A-Bank</option>
          </select>
        </div>
        <div class="form-actions">
          <button type="submit" class="submit-button" :disabled="isLoading">
            {{ isLoading ? 'Подключение...' : 'Подключить' }}
          </button>
        </div>
        <p v-if="error" class="error-message">{{ error }}</p>
        <p v-if="isSuccess" class="success-message">Банк успешно подключен!</p>
      </form>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { authService } from '@/services/authService';
const props = defineProps<{
  show: boolean;
}>();
const emit = defineEmits(['close']);
const bankLogin = ref('');
const bankPassword = ref('');
const bank = ref('');
const isLoading = ref(false);
const error = ref<string | null>(null);
const isSuccess = ref(false);
const close = () => {
  emit('close');
};
const handleSubmit = async () => {
  isLoading.value = true;
  error.value = null;
  isSuccess.value = false;
  try {
    await authService.connectBank({
      bankLogin: bankLogin.value,
      bankPassword: bankPassword.value,
      bank: bank.value,
    });
    isSuccess.value = true;
    setTimeout(() => {
      close();
      window.location.reload();
    }, 5000);
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Не удалось подключить банк. Пожалуйста, проверьте свои данные.';
    isLoading.value = false;
  }
};
</script>
<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  background-color: #2c2f48;
  padding: 30px;
  border-radius: 12px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  position: relative;
}
.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  color: #fff;
  font-size: 24px;
  cursor: pointer;
}
h3 {
  color: #e0e0e0;
  margin-bottom: 20px;
  text-align: center;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  color: #a0a0a0;
  margin-bottom: 8px;
}
.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #4a4e69;
  background-color: #1e2139;
  color: #e0e0e0;
}
.submit-button {
  width: 100%;
  padding: 12px;
  border-radius: 6px;
  border: none;
  background-color: #007bff;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.submit-button:hover {
  background-color: #0056b3;
}
.submit-button:disabled {
  background-color: #555;
  cursor: not-allowed;
}
.error-message {
  color: #ff4d4d;
  text-align: center;
  margin-top: 15px;
}
.success-message {
  color: #28a745;
  text-align: center;
  margin-top: 15px;
}
</style>