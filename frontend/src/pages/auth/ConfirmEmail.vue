<template>
  <h1>Success register</h1>
  <router-link to="/login" style="margin: 5px">To login</router-link>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  mounted() {
    // Получение кода подтверждения из URL
    const code = this.$route.query.code;

    // Отправка кода на бэкенд
    this.confirmEmail(code);
  },
  methods: {
    confirmEmail(code) {
      // API-запрос для подтверждения почты
      // Используйте вашу библиотеку для сетевых запросов (например, axios)
      // для отправки кода на бэкенд
      AXIOS.post('/auth/confirm-email', { code: code })
          .then(response => {
            alert(response.data)
          })
          .catch(error => {
            alert(error.response.data)
          });
    }
  }
}
</script>

<style scoped>

</style>