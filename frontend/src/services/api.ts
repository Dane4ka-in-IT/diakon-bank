import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
export const authApi = axios.create({
    baseURL: import.meta.env.VITE_AUTH_API_BASE_URL
});
authApi.interceptors.request.use(config => {
    const authStore = useAuthStore();
    const token = authStore.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
export const bankApi = axios.create({
    baseURL: import.meta.env.VITE_BANK_API_BASE_URL
});
bankApi.interceptors.request.use(config => {
    const authStore = useAuthStore();
    const token = authStore.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
export const pulseApi = axios.create({
    baseURL: import.meta.env.VITE_PULSE_API_BASE_URL
});
pulseApi.interceptors.request.use(config => {
    const authStore = useAuthStore();
    const user = authStore.user;
    if (user && user.id) {
        config.headers['X-User-Id'] = user.id;
    }
    return config;
});