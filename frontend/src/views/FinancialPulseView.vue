<template>
  <div class="financial-pulse-container">
    <div class="financial-pulse-card">
    <div class="header-section">
      <button class="back-button" @click="goBack">← Назад</button>
      <h2>Финансовый пульс</h2>
    </div>
    <div class="period-selector">
      <label for="from-date">Период</label>
      <div class="date-inputs">
        <span>От</span>
        <input type="text" id="from-date" placeholder="ДД" maxlength="2" />
        <input type="text" placeholder="ММ" maxlength="2" />
        <input type="text" placeholder="ГГГГ" maxlength="4" />
        <span>до</span>
        <input type="text" id="to-date" placeholder="ДД" maxlength="2" />
        <input type="text" placeholder="ММ" maxlength="2" />
        <input type="text" placeholder="ГГГГ" maxlength="4" />
      </div>
      <button class="calculate-btn">Рассчитать</button>
    </div>
    <div class="charts-container">
      <div class="chart-wrapper">
        <h3>Расходы</h3>
        <div class="chart-canvas-wrapper">
          <Doughnut :data="expensesData" :options="chartOptions" />
        </div>
      </div>
      <div class="chart-wrapper">
        <h3>Доходы</h3>
        <div class="chart-canvas-wrapper">
          <Doughnut :data="incomeData" :options="chartOptions" />
        </div>
      </div>
    </div>
    <div class="summary-boxes">
      <div class="summary-box">
        <h4>Расходы</h4>
        <p>{{ formatCurrency(computedExpenses) }}</p>
      </div>
      <div class="summary-box">
        <h4>Доходы</h4>
        <p>{{ formatCurrency(computedIncome) }}</p>
      </div>
      <div class="summary-box">
        <h4>Сбережения</h4>
        <p>{{ formatCurrency(computedIncome - computedExpenses) }}</p>
      </div>
    </div>
    <div class="actions">
      <button class="leaks-btn">Поиск финансовых утечек 👑</button>
    </div>
  </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { Doughnut } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement, CategoryScale } from 'chart.js';
import { useDashboardStore } from '@/stores/dashboard';
ChartJS.register(Title, Tooltip, Legend, ArcElement, CategoryScale);
const dashboardStore = useDashboardStore();
const router = useRouter();
const goBack = () => {
  router.push({ name: 'dashboard' });
};
const isSalary = (transaction: any): boolean => {
  const category = (transaction.transactionInformation || '').toLowerCase();
  return category.includes('зарплата') || category.includes('salary') || category.includes('доход');
};
const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', minimumFractionDigits: 0 }).format(value);
};
const processChartData = (transactions: any[], filterPredicate: (t: any) => boolean) => {
    const filtered = transactions.filter(filterPredicate);
    const categories = filtered.reduce((acc, t) => {
        const category = t.transactionInformation || 'Прочее';
        const amount = Math.abs(t.amount);
        acc[category] = (acc[category] || 0) + amount;
        return acc;
    }, {} as Record<string, number>);
    const sortedCategories = Object.entries(categories).sort(([, a], [, b]) => b - a);
    const topN = 10;
    const mainCategories = sortedCategories.slice(0, topN);
    const otherCategories = sortedCategories.slice(topN);
    const chartData = Object.fromEntries(mainCategories);
    if (otherCategories.length > 0) {
        chartData['Другое'] = otherCategories.reduce((acc, [, amount]) => acc + amount, 0);
    }
    return {
        labels: Object.keys(chartData),
        datasets: [
            {
                backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#FFD700', '#ADFF2F', '#FF69B4', '#1E90FF', '#9932CC', '#F4A460', '#808080'],
                data: Object.values(chartData),
            },
        ],
    };
};
const expensesData = computed(() => {
    return processChartData(dashboardStore.transactions, t => !isSalary(t));
});
const incomeData = computed(() => {
    return processChartData(dashboardStore.transactions, t => isSalary(t));
});
const computedExpenses = computed(() => {
    return dashboardStore.transactions
        .filter(t => !isSalary(t))
        .reduce((sum, t) => sum + Math.abs(t.amount), 0);
});
const computedIncome = computed(() => {
    return dashboardStore.transactions
        .filter(t => isSalary(t))
        .reduce((sum, t) => sum + Math.abs(t.amount), 0);
});
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right' as const,
      labels: {
        color: '#ffffff',
      },
    },
    title: {
      display: true,
      color: '#ffffff',
    },
  },
};
</script>
<style scoped>
.financial-pulse-container {
    width: 100%;
}
.financial-pulse-card {
  background-color: #2c2f48;
  color: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  max-width: 1200px;
  margin: 2rem auto;
}
.financial-pulse-card h2 {
    font-size: 2rem;
    margin-bottom: 1.5rem;
}
.header-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.back-button {
  background-color: #4a4e69;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}
.back-button:hover {
  background-color: #5a5e79;
}
.financial-pulse-card h2 {
    font-size: 2rem;
    margin: 0;
}
.period-selector {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  color: white;
}
.date-inputs {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: white;
}
.date-inputs input {
    width: 50px;
    padding: 0.5rem;
    border-radius: 4px;
    border: 1px solid #555;
    background-color: #333;
    color: white;
    text-align: center;
}
.date-inputs input::placeholder {
    color: #888;
}
.calculate-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}
.charts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}
.chart-wrapper {
  background-color: #3a3a4a;
  padding: 1.5rem;
  border-radius: 12px;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.chart-canvas-wrapper {
  position: relative;
  height: 300px;
}
.summary-boxes {
  display: flex;
  justify-content: space-around;
  gap: 1rem;
  margin-bottom: 2rem;
}
.summary-box {
  background-color: #333;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
  flex: 1;
}
.summary-box p {
    font-size: 1.25rem;
    font-weight: bold;
    color: #4caf50;
}
.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.leaks-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
}
</style>