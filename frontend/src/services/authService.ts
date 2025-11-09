import { authApi } from './api';
import { useAuthStore } from '@/stores/auth';
import type { User } from '@/stores/auth';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  sub: string; // Subject, typically the username
  userId: number;
  // Add other claims like roles if they exist in your token
}

export const authService = {
  async register(username: string, password: string): Promise<any> {
    const response = await authApi.post('/auth/register', { username, password });
    return response.data;
  },

  async login(username: string, password: string): Promise<void> {
    const response = await authApi.post<{ token: string }>('/auth/login', { username, password });
    const { token } = response.data;
    const authStore = useAuthStore();
    authStore.setToken(token);

    // Decode the token to get user info
    const decodedToken: DecodedToken = jwtDecode(token);

    const user: User = {
        id: decodedToken.userId,
        username: decodedToken.sub,
    };
    authStore.setUser(user);
  },

  logout() {
    const authStore = useAuthStore();
    authStore.logout();
  }
}; 