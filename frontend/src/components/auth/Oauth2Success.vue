<template>
  <h1>Oauth2 success</h1>
  <router-link to="/login" style="margin: 5px">To login</router-link>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  mounted() {
    AXIOS.get("/auth/oauth2-success").then(response => {
      alert(response.data)
    })
  }
}
</script>

<style scoped>

</style>