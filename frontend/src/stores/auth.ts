import { defineStore } from 'pinia';
import { ref } from 'vue';

// A mock user object, replace with your actual user structure
export interface User {
  id: number;
  username: string;
  // Add other user properties here
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'));
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') || 'null'));

  function setToken(newToken: string) {
    token.value = newToken;
    localStorage.setItem('token', newToken);
  }

  function setUser(newUser: User) {
    user.value = newUser;
    localStorage.setItem('user', JSON.stringify(newUser));
  }

  function logout() {
    token.value = null;
    user.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    // You might want to redirect to the login page here
  }

  return {
    token,
    user,
    setToken,
    setUser,
    logout,
  };
}); 