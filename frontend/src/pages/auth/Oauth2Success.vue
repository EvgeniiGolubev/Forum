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
    AXIOS.get("/auth/oauth2-success")
        .then(response => {
          this.$store.dispatch('loginAction', {
            authenticate: true,
            roles: response.data.roles,
            name: response.data.name,
            picture: response.data.userPicture,
          })

          this.alertMessage = 'You successfully sign in'
          setTimeout(() => {
            this.alertMessage = null
            this.$router.push('/profile')
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