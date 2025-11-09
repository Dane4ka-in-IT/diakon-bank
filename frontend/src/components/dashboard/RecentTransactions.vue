<template>
  <div class="recent-transactions-card">
    <h3 class="card-title">Последние операции</h3>
    <ul class="transactions-list" :class="{ 'is-expanded': isExpanded }">
      <li v-for="transaction in visibleTransactions" :key="transaction.id" class="transaction-item">
        <div class="transaction-icon">{{ getIconForTransaction(transaction.transactionInformation) }}</div>
        <span class="transaction-description">{{ transaction.transactionInformation }}</span>
        <span class="transaction-amount">{{ formatCurrency(transaction.amount, transaction.currency) }}</span>
      </li>
    </ul>
    <button v-if="transactions.length > 5" @click="toggleExpanded" class="show-more-btn">
      {{ isExpanded ? 'Show less' : 'Show more' }}
    </button>
    <div v-else class="no-transactions">
      <p>No recent transactions.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import type { BankTransaction } from '@/stores/dashboard';

const props = defineProps<{
  transactions: BankTransaction[];
}>();

const isExpanded = ref(false);

const visibleTransactions = computed(() => {
  if (isExpanded.value) {
    return props.transactions;
  }
  return props.transactions.slice(0, 5);
});

const toggleExpanded = () => {
  isExpanded.value = !isExpanded.value;
};

const formatCurrency = (value: number, currency: string) => {
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: currency || 'RUB',
  }).format(value || 0);
};

const getIconForTransaction = (description: string | null) => {
  const lowerCaseDesc = description?.toLowerCase() || '';
  if (lowerCaseDesc.includes('магнит') || lowerCaseDesc.includes('продукты')) return '🛒';
  if (lowerCaseDesc.includes('транспорт')) return '🚌';
  if (lowerCaseDesc.includes('wildberries') || lowerCaseDesc.includes('одежда')) return '🛍️';
  if (lowerCaseDesc.includes('cofix') || lowerCaseDesc.includes('кофе')) return '☕';
  if (lowerCaseDesc.includes('перевод')) return '💸';
  return '💳'; // Default icon
};
</script>

<style scoped>
.recent-transactions-card {
  background-color: #3a3a4a;
  border-radius: 15px;
  padding: 20px;
  color: white;
}

.card-title {
  margin: 0 0 15px 0;
  font-weight: normal;
  border-bottom: 1px solid #555;
  padding-bottom: 10px;
}

.transactions-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px; /* Adds space between items */
}

.transaction-item {
  display: flex;
  align-items: center;
  padding: 12px 5px; /* More vertical padding */
  border-bottom: 1px solid #555;
}

.transaction-icon {
  font-size: 24px;
  width: 50px; /* A bit more space for the icon */
  text-align: center;
  margin-right: 15px;
}

.transaction-description {
  flex-grow: 1;
}

.transaction-amount {
  font-weight: bold;
}

.no-transactions {
  padding: 20px 0;
  text-align: center;
  color: #888;
}

.transactions-list.is-expanded {
  max-height: 400px; /* Or any height you prefer */
  overflow-y: auto;
}

.show-more-btn {
  background: none;
  border: 1px solid #007bff;
  color: #00aaff;
  width: 100%;
  padding: 10px;
  margin-top: 15px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
}

.show-more-btn:hover {
  background-color: #007bff;
  color: white;
}

/* Custom scrollbar styles */
.transactions-list::-webkit-scrollbar {
  width: 8px;
}

.transactions-list::-webkit-scrollbar-track {
  background: #2c2c3a;
  border-radius: 10px;
}

.transactions-list::-webkit-scrollbar-thumb {
  background: #555;
  border-radius: 10px;
}

.transactions-list::-webkit-scrollbar-thumb:hover {
  background: #777;
}
</style> 