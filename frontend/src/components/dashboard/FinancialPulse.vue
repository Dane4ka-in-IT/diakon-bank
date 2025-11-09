<template>
  <button class="financial-pulse-button">
    Финансовый пульс
  </button>
</template>

<script setup lang="ts">
// This button would likely navigate to a dedicated analytics page or open a modal
</script>

<style scoped>
.financial-pulse-button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 10px;
  padding: 20px;
  width: 100%;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  text-align: center;
  box-shadow: 0 4px 10px rgba(0, 123, 255, 0.4);
  transition: all 0.2s ease-in-out;
}

.financial-pulse-button:hover {
  background-color: #0056b3;
  box-shadow: 0 6px 15px rgba(0, 123, 255, 0.5);
  transform: translateY(-2px);
}
</style> 