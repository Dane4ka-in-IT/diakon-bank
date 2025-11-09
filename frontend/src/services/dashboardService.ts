import { pulseApi } from './api';
import { useDashboardStore } from '@/stores/dashboard';
import type { DashboardData } from '@/stores/dashboard';

export const dashboardService = {
  async fetchDashboardData(): Promise<void> {
    const store = useDashboardStore();
    store.setLoading(true);
    store.setError(null);
    try {
      const response = await pulseApi.get<DashboardData>('/pulse/dashboard');
      store.setData(response.data);
    } catch (err: any) {
      console.error('Failed to fetch dashboard data:', err);
      store.setError(err.message || 'An unknown error occurred.');
    } finally {
      store.setLoading(false);
    }
  },
}; 