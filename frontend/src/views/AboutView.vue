<template>
  <div class="about-page">
    <div class="hero">
      <h1>О проекте</h1>
      <p>Будущее ваших финансов уже здесь!</p>
    </div>
    <div class="content-grid">
      <div class="info-card">
        <h2>Проблема: Финансовый хаос</h2>
        <p>В современном мире у многих из нас есть счета, карты и вклады в нескольких разных банках. Чтобы понять общую картину своих финансов, приходится переключаться между десятками приложений, вручную сводить балансы и пытаться удержать все в голове. Это неудобно, отнимает время и мешает принимать взвешенные финансовые решения.</p>
      </div>
      <div class="video-card">
        <div class="video-placeholder">
          <!-- In a real app, this would be an iframe or a video player component -->
          <div class="play-button"></div>
        </div>
      </div>
      <div class="info-card">
        <h2>Как это работает?</h2>
        <p>Мы используем самые современные и безопасные открытые банковские API (Open Banking). Это официальная и защищенная технология, которая позволяет нашему приложению по вашему разрешению подключаться к счетам и агрегировать данные в режиме реального времени. "Diakon-bank" также предложит мощные, но простые инструменты для:</p>
        <ul>
          <li>Анализа расходов: Узнайте, куда на самом деле уходят ваши деньги.</li>
          <li>Бюджетирования: Устанавливайте лимиты и достигайте финансовых целей.</li>
          <li>Планирования: Получайте персональные рекомендации и стройте свое финансовое будущее.</li>
        </ul>
      </div>
      <div class="info-card solution-card">
        <h2>Наше решение</h2>
        <p>"Diakon-bank" – это инновационное приложение, которое навсегда решит эту проблему. Наша цель – объединить все ваши счета из различных банков в одном удобном, безопасном и интуитивно понятном интерфейсе. Представьте: все ваши средства, история транзакций и глубокая финансовая аналитика доступны в одном месте, буквально на кончиках ваших пальцев.</p>
        <p class="quote">"Мы создаем не просто приложение, а инструмент для финансовой свободы каждого."</p>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
</script>
<style scoped>
.about-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  color: #e0e0e0;
}
.main-layout:not(.dark-theme) .about-page {
  color: #333;
}
.main-layout:not(.dark-theme) .about-page .hero h1,
.main-layout:not(.dark-theme) .about-page .info-card h2,
.main-layout:not(.dark-theme) .about-page .quote {
  color: #0056b3;
}
.main-layout:not(.dark-theme) .about-page .info-card {
  background-color: #ffffff;
  border-color: #dee2e6;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
}
.hero {
  text-align: center;
  margin-bottom: 40px;
}
.hero h1 {
  font-size: 3em;
  color: #00aaff;
}
.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}
.info-card {
  background-color: rgba(0, 50, 100, 0.3);
  border: 1px solid #00aaff;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 0 15px rgba(0, 170, 255, 0.2);
}
.info-card h2 {
  color: #00aaff;
  margin-top: 0;
}
.info-card ul {
  padding-left: 20px;
}
.solution-card {
  grid-column: span 1;
}
.quote {
  font-style: italic;
  color: #00caff;
  border-left: 3px solid #00caff;
  padding-left: 15px;
  margin-top: 20px;
}
.video-card {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #000;
  border-radius: 15px;
  min-height: 300px;
}
.video-placeholder {
  width: 80%;
  height: 80%;
  background-color: #111;
  position: relative;
  cursor: pointer;
}
.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 0;
  height: 0;
  border-top: 20px solid transparent;
  border-bottom: 20px solid transparent;
  border-left: 30px solid white;
}
</style>