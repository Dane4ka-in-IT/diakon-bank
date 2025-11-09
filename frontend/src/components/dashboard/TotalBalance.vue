<template>
  <div class="total-balance-card">
    <h3>Общий баланс</h3>
    <div class="balance-amount">{{ formattedBalance }}</div>
    <div class="icon-wallet"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  balance: number;
}>();

const formattedBalance = computed(() => {
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    minimumFractionDigits: 2,
  }).format(props.balance || 0);
});
</script>

<style scoped>
.total-balance-card {
  background-color: #3a3a4a;
  border-radius: 15px;
  padding: 20px;
  color: white;
  min-width: 250px; /* Example width */
  height: 150px; /* Example height to match layout */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

h3 {
  margin: 0;
  font-weight: normal;
  font-size: 18px;
}

.icon-wallet {
  /* You would add your wallet icon here, possibly as a background image */
  width: 50px;
  height: 50px;
  background-color: #fff; /* Placeholder for icon */
  mask: url("data:image/svg+xml,...") no-repeat center; /* Example for SVG icon */
  -webkit-mask: url("data:image/svg+xml,...") no-repeat center;
}

.balance-amount {
  font-size: 28px;
  font-weight: bold;
  margin-top: 10px;
}
</style> 