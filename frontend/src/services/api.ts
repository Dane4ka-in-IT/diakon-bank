import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

// We can externalize these to .env variables if needed, e.g., VITE_AUTH_API_URL
export const authApi = axios.create({
    baseURL: 'https://lt.shruc.dev/auth/api/v1'
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
    baseURL: 'https://lt.shruc.dev/bank/api/v1'
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
    baseURL: 'https://lt.shruc.dev/pulse/api/v1'
});

pulseApi.interceptors.request.use(config => {
    const authStore = useAuthStore();
    const user = authStore.user;
    if (user && user.id) {
        config.headers['X-User-Id'] = user.id;
    }
    // If pulse endpoints become secured, we'll need to add the auth token here too.
    return config;
}); 