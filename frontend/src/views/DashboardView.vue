<template>
  <div v-if="dashboardStore.loading" class="loading-spinner">Loading...</div>
  <div v-else-if="dashboardStore.error" class="error-message">{{ dashboardStore.error }}</div>
  <div v-else class="dashboard">
    <div class="main-content">
      <div class="balance-section">
        <TotalBalance :balance="dashboardStore.totalBalance" />
        <TransactionButtons 
          :income="dashboardStore.totalIncome" 
          :expenses="dashboardStore.totalExpenses" 
        />
      </div>
      <MyAccounts :accounts="dashboardStore.accounts" />
    </div>
    <div class="sidebar">
      <FinancialPulse />
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
.dashboard {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  padding: 20px;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.balance-section {
    display: flex;
    gap: 20px;
    align-items: flex-start;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.loading-spinner, .error-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  font-size: 2em;
}
</style> 