<template>
  <div class="transaction-buttons">
    <button class="transaction-button spend">
      <span class="arrow">→</span> {{ formattedExpenses }}
    </button>
    <button class="transaction-button receive">
      <span class="arrow">→</span> {{ formattedIncome }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  income: number;
  expenses: number;
}>();

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
  }).format(value || 0);
};

const formattedIncome = computed(() => `+ ${formatCurrency(props.income)}`);
const formattedExpenses = computed(() => `- ${formatCurrency(props.expenses)}`);
</script>

<style scoped>
.transaction-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
  flex-grow: 1;
}

.transaction-button {
  background-color: #3a3a4a;
  border: 1px solid #555;
  border-radius: 10px;
  color: white;
  padding: 15px 20px;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 15px;
  text-align: left;
  width: 100%;
}

.transaction-button:hover {
  background-color: #4a4a5a;
}

.arrow {
  display: inline-block;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  font-weight: bold;
}

.spend .arrow {
  background-color: #ff4d4d;
  transform: rotate(-45deg);
}

.receive .arrow {
  background-color: #4dff4d;
  transform: rotate(135deg); /* Pointing down-left, as in the image */
}
</style> 