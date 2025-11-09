<script setup lang="ts">
import MainLayout from '@/layouts/MainLayout.vue';
</script>

<template>
  <MainLayout>
    <router-view />
  </MainLayout>
</template>

<style>
/* You can move the contents of base.css and main.css here if you prefer a single style entry point */
@import '@/assets/base.css';

#app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
