<template>
  <errors-view v-bind:errors="errors"/>
  <alert-view v-bind:alert-message="alertMessage"/>
</template>

<script>
import {AXIOS} from "@/http-commons";
import AlertView from "@/components/AlertView.vue";
import ErrorsView from "@/components/ErrorsView.vue";

export default {
  components: {ErrorsView, AlertView},
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
            console.log(response.data)
            this.alertMessage = 'Your mail has been successfully confirmed'
            setTimeout(() => {
              this.alertMessage = null
              this.$router.push('/login')
            }, 1000)
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
              this.errors.push(error.response.data)
            }
          });
    }
  }
}
</script>

<style>

</style>