import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const authApi = axios.create({
  baseURL: 'https://lt.shruc.dev/auth/api/v1',
});

const bankApi = axios.create({
  baseURL: 'https://lt.shruc.dev/bank/api/v1',
});

const pulseApi = axios.create({
  baseURL: 'https://lt.shruc.dev/pulse/api/v1',
});

bankApi.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  const token = authStore.token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

pulseApi.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  const userId = authStore.user?.id; // Assuming user object has an id
  if (userId) {
    config.headers['X-User-Id'] = userId;
  }
  return config;
});


export { authApi, bankApi, pulseApi }; 