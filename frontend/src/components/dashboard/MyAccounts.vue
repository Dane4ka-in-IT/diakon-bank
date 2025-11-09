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
      <p>No accounts found.</p>
    </div>
    <button class="add-account-btn">+</button>
  </div>
</template>

<script setup lang="ts">
import type { BankAccount } from '@/stores/dashboard';

defineProps<{
  accounts: BankAccount[];
}>();

const formatCurrency = (value: number, currency: string) => {
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: currency || 'RUB',
  }).format(value || 0);
};
</script>

<style scoped>
.my-accounts-card {
  background-color: #3a3a4a;
  border-radius: 15px;
  padding: 20px;
  color: white;
  position: relative;
  height: 400px; /* Example fixed height */
}

.card-title {
  margin: 0 0 15px 0;
  font-weight: normal;
}

.accounts-list {
  flex-grow: 1;
  overflow-y: auto; /* For when there are many accounts */
}

.accounts-list ul {
  list-style: none;
  padding: 0;
}
.account-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #555;
}
.account-name {
  font-weight: bold;
}

.add-account-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #007bff;
  color: white;
  border: none;
  font-size: 30px;
  line-height: 50px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
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
}
</style> 