<template>
  <div>
    <div>
      {{ responseMessages }}
    </div>

    <div>
      <ul>
        <li v-for="(error, index) in errors" :key="index">{{ error.message }}</li>
      </ul>
    </div>

    <form @submit.prevent="register">
      <div>
        <label for="email">Email</label>
        <input type="text" id="email" v-model="email"/>
      </div>
      <div>
        <label for="name">Username</label>
        <input type="text" id="name" v-model="name"/>
      </div>
      <div>
        <label for="password">Password</label>
        <input type="password" id="password" v-model="password"/>
      </div>
      <div>
        <label for="confirm">Confirm the password</label>
        <input type="password" id="confirm" v-model="confirmPassword"/>
      </div>
      <button type="submit">Sign up</button>
    </form>

    <br/>
    <a href="/oauth2/authorization/google">Sign up with Google</a>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  data() {
    return {
      email: '',
      name: '',
      password: '',
      confirmPassword: '',
      errors: [],
      responseMessages: '',
    }
  },
  methods: {
    register() {
      this.errors = []
      this.responseMessages = ''

      const user = {
        email: this.email,
        name: this.name,
        password: this.password,
        confirmPassword: this.confirmPassword,
      }

      AXIOS.post('/auth/register', user)
          .then(response => {
            alert(response.data.name)
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })

    }
  }
}
</script>

<style scoped>

</style>