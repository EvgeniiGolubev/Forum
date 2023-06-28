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
    AXIOS.get("/auth/oauth2-success")
        .then(response => {
          this.$store.dispatch('loginAction', {
            authenticate: true,
            roles: response.data.roles,
            name: response.data.name,
            picture: response.data.userPicture,
            id: response.data.id
          })

          this.alertMessage = 'You successfully sign in'
          setTimeout(() => {
            this.alertMessage = null
            this.$router.push('/profile')
          }, 1000)
        })
        .catch(error => {
          if (!Array.isArray(error.response.data)) {
            this.errors.push(error.response.data)
          }
        });
  }
}
</script>

<style>

</style>