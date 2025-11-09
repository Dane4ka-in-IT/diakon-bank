<template>
  <div class="tariffs-page">
    <h1 class="page-title">Тарифы</h1>
    <div class="tariffs-container">
      <div class="tariff-card">
        <h2 class="tariff-name">Бесплатный тариф (Free)</h2>
        <p class="tariff-subtitle">Полный контроль и безопасность</p>
        <div class="features">
          <h3>Функционал:</h3>
          <ul>
            <li>Подключение до 2-х банков.</li>
            <li>Полный доступ к "Единому Дашборду".</li>
            <li>Полный "Паспорт Надежности" (с рейтингом и детальным анализом).</li>
            <li>Умные уведомления ("Сторож Кошелька").</li>
          </ul>
        </div>
        <button class="tariff-button">Остаться на Free</button>
      </div>

      <div class="tariff-card premium">
        <div class="crown-icon">👑</div>
        <h2 class="tariff-name">Платный тариф (Premium)</h2>
        <p class="tariff-subtitle premium-subtitle">Финансовый гений</p>
        <div class="features">
          <h3>Функционал:</h3>
          <ul>
            <li>Для новых пользователей бесплатный пробный период в 1 месяц.</li>
            <li>Подключение неограниченного количества банков.</li>
            <li>Полный доступ к "Финансовому Пульсу" (вся глубокая аналитика, графики, поиск утечек).</li>
          </ul>
        </div>
        <button class="tariff-button premium-button">289 руб/мес</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// No script logic needed for this static page
</script>

<style scoped>
.tariffs-page {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  color: #e0e0e0;
}

.page-title {
  text-align: center;
  font-size: 2.5em;
  margin-bottom: 40px;
}

.tariffs-container {
  display: flex;
  justify-content: center;
  gap: 40px;
  flex-wrap: wrap;
}

.tariff-card {
  background-color: rgba(45, 45, 60, 0.6);
  border: 2px solid #00aaff;
  border-radius: 15px;
  padding: 30px;
  width: 400px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 0 20px rgba(0, 170, 255, 0.2);
  position: relative;
}

.tariff-name {
  color: #fff;
  font-size: 1.5em;
  margin: 0;
}

.tariff-subtitle {
  color: #aaa;
  margin: 5px 0 25px 0;
}

.premium-subtitle {
  color: #ffd700;
}

.features {
  margin-bottom: 30px;
  flex-grow: 1;
}

.features h3 {
  font-weight: bold;
  margin-bottom: 15px;
}

.features ul {
  list-style: disc;
  padding-left: 20px;
}

.features li {
  margin-bottom: 10px;
}

.tariff-button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px;
  font-size: 1em;
  cursor: pointer;
  transition: background-color 0.2s;
}

.tariff-button:hover {
  background-color: #0056b3;
}

.premium {
  border-color: #ffd700;
  box-shadow: 0 0 25px rgba(255, 215, 0, 0.3);
}

.premium-button {
  background-color: #ffd700;
  color: #333;
  font-weight: bold;
}

.premium-button:hover {
  background-color: #e0c200;
}

.crown-icon {
  position: absolute;
  top: -20px;
  right: 20px;
  font-size: 3em;
  transform: rotate(15deg);
}
</style> 