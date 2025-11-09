<script setup lang="ts">
import MainLayout from '@/layouts/MainLayout.vue';
</script>
<template>
  <MainLayout>
    <router-view />
  </MainLayout>
</template>
<style>
@import '@/assets/base.css';
#app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>