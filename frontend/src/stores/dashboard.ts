import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

// Updated interface to match the real API response
export interface BankAccount {
  id: number;
  accountNumber: string;
  accountType: string;
  balance: number;
  bankName: string;
  currency: string;
  nickname: string;
}

// Updated interface to match the real API response
export interface BankTransaction {
  id: number;
  transactionInformation: string;
  bookingDateTime: string;
  amount: number;
  currency: string;
  creditDebitIndicator: 'Credit' | 'Debit';
}

export const useDashboardStore = defineStore('dashboard', () => {
  const accounts = ref<BankAccount[]>([]);
  const transactions = ref<BankTransaction[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const totalBalance = computed(() => {
    return accounts.value.reduce((total, acc) => total + acc.balance, 0);
  });

  const totalIncome = computed(() => {
    return transactions.value
      .filter(t => t.transactionInformation.toLowerCase().includes('зарплата') || t.transactionInformation.toLowerCase().includes('доход'))
      .reduce((sum, t) => sum + t.amount, 0);
  });

  const totalExpenses = computed(() => {
    return transactions.value
      .filter(t => !t.transactionInformation.toLowerCase().includes('зарплата') && !t.transactionInformation.toLowerCase().includes('доход'))
      .reduce((sum, t) => sum + t.amount, 0);
  });

  return {
    accounts,
    transactions,
    loading,
    error,
    totalBalance,
    totalIncome,
    totalExpenses,
  };
}); 