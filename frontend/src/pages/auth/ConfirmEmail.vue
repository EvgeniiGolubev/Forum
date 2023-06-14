<template>
  <div class="alert-holder">
    <div class="alert alert-danger" role="alert" v-for="(error, index) in errors" :key="index" v-bind:class="{ 'show': errors.length }">
      {{ error.message }}
    </div>
    <div class="alert alert-success" role="alert" v-if="alertMessage" v-bind:class="{ 'show': alertMessage }">
      {{ alertMessage }}
    </div>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  data() {
    return {
      errors: [],
      alertMessage: null,
    }
  },
  mounted() {
    const code = this.$route.query.code;
    this.confirmEmail(code);
  },
  methods: {
    confirmEmail(code) {
      AXIOS.post('/auth/confirm-email', { code: code })
          .then(response => {
            console.log(response.data.message)
            this.alertMessage = 'Your mail has been successfully confirmed'
            setTimeout(() => {
              this.alertMessage = null
              this.$router.push('/login')
            }, 5000)
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
              this.errors.push(error.response.data)
            }

            setTimeout(() => {
              this.errors = [];
            }, 5000)
          });
    }
  }
}
</script>

<style>
.alert-holder {
  max-width: 800px;
  margin: 0 auto;
}

.alert {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.alert.show {
  opacity: 1;
}
</style>