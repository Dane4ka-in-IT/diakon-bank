import { bankApi } from './api';
import { useDashboardStore } from '@/stores/dashboard';
import type { BankAccount, BankTransaction } from '@/stores/dashboard';

export const bankService = {
  async fetchBankData(): Promise<void> {
    const store = useDashboardStore();
    store.loading = true;
    store.error = null;
    try {
      // Fetch accounts and transactions in parallel
      const [accountRes, transactionRes] = await Promise.all([
        // Both APIs return a flat array
        bankApi.get<BankAccount[]>('/bank/accounts'),
        bankApi.get<BankTransaction[]>('/bank/transactions'),
      ]);

      store.accounts = accountRes.data;
      store.transactions = transactionRes.data;

    } catch (err: any) {
      console.error('Failed to fetch bank data:', err);
      store.error = err.message || 'An unknown error occurred while fetching bank data.';
    } finally {
      store.loading = false;
    }
  },
}; 