<template>
  <div v-if="dashboardStore.loading" class="loading-spinner">Loading...</div>
  <div v-else-if="dashboardStore.error" class="error-message">{{ dashboardStore.error }}</div>
  <div v-else class="dashboard-grid">
    <div class="card total-balance">
      <TotalBalance :balance="dashboardStore.totalBalance" />
    </div>
    <div class="top-right-cluster">
      <TransactionButtons 
        :income="dashboardStore.totalIncome" 
        :expenses="dashboardStore.totalExpenses" 
      />
      <FinancialPulse />
    </div>
    <div class="card my-accounts">
      <MyAccounts :accounts="dashboardStore.accounts" />
    </div>
    <div class="card recent-transactions">
      <RecentTransactions :transactions="dashboardStore.transactions" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useDashboardStore } from '@/stores/dashboard';
import { bankService } from '@/services/bankService';
import TotalBalance from '@/components/dashboard/TotalBalance.vue';
import TransactionButtons from '@/components/dashboard/TransactionButtons.vue';
import MyAccounts from '@/components/dashboard/MyAccounts.vue';
import FinancialPulse from '@/components/dashboard/FinancialPulse.vue';
import RecentTransactions from '@/components/dashboard/RecentTransactions.vue';

const dashboardStore = useDashboardStore();

onMounted(() => {
  bankService.fetchBankData();
});
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(350px, 1.5fr) minmax(300px, 1fr);
  grid-template-rows: auto 1fr;
  gap: 30px;
  width: 100%;
  align-items: flex-start;
}

.card {
  background-color: #2c2f48;
  border-radius: 12px;
  padding: 24px;
  color: #e0e0e0;
}

.total-balance {
  grid-column: 1 / 2;
  grid-row: 1 / 2;
}

.top-right-cluster {
  grid-column: 2 / 3;
  grid-row: 1 / 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.my-accounts {
  grid-column: 1 / 2;
  grid-row: 2 / 3;
}

.recent-transactions {
  grid-column: 2 / 3;
  grid-row: 2 / 3;
}

.loading-spinner, .error-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  font-size: 2em;
}
</style> 