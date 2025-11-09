<template>
  <div class="my-accounts-card">
    <h3 class="card-title">Мои счета</h3>
    <div class="accounts-list" v-if="accounts.length > 0">
      <ul>
        <li v-for="account in accounts" :key="account.id" class="account-item">
          <span class="account-name">{{ account.nickname || account.bankName }}</span>
          <span class="account-balance">{{ formatCurrency(account.balance, account.currency) }}</span>
        </li>
      </ul>
    </div>
    <div v-else class="no-accounts">
      <p>Не найдено счетов.</p>
    </div>
    <button class="add-account-btn" @click="showModal = true">+</button>
  </div>
  <ConnectBankModal :show="showModal" @close="showModal = false" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { BankAccount } from '@/stores/dashboard';
import ConnectBankModal from './ConnectBankModal.vue';

defineProps<{
  accounts: BankAccount[];
}>();

const showModal = ref(false);

const formatCurrency = (value: number, currency: string) => {
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: currency || 'RUB',
  }).format(value || 0);
};
</script>

<style scoped>
.my-accounts-card {
  background-color: #2c2f48;
  border-radius: 12px;
  padding: 24px;
  color: #e0e0e0;
  position: relative;
  min-height: 250px;
  display: flex;
  flex-direction: column;
}

.card-title {
  margin: 0 0 15px 0;
  font-weight: 500;
  font-size: 1.2rem;
}

.accounts-list {
  flex-grow: 1;
  overflow-y: auto;
}

.accounts-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.account-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #4a4e69;
}
.account-item:last-child {
  border-bottom: none;
}
.account-name {
  font-weight: 500;
}

.add-account-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background-color: #007bff;
  color: white;
  border: none;
  font-size: 28px;
  line-height: 44px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
  transition: background-color 0.2s;
}

.add-account-btn:hover {
  background-color: #0056b3;
}

.no-accounts {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #888;
  height: 100%;
}
</style> 