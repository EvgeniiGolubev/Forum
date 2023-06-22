<template>
  <errors-view v-bind:errors="errors"/>

  <main class="form-signin">
    <div class="container">
      <form ref="form" @submit.prevent="submitForm" class="needs-validation" novalidate>
        <img class="mb-4" src="/img/logo.png" width="100" height="100"/>
        <h1 class="h3 mb-3 fw-normal">Sign in</h1>
        <div class="form-floating">
          <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com" v-model="email" required>
          <label for="floatingInput">Email address</label>
          <div class="invalid-feedback" >Please provide a valid email address.</div>
        </div>
        <div class="form-floating mt-3">
          <input type="password" class="form-control" id="floatingPassword" placeholder="Password" v-model="password" required minlength="1">
          <label for="floatingPassword">Password</label>
          <div class="invalid-feedback" >Password must be at least 8 characters long.</div>
        </div>
        <button class="btn btn-primary w-100 mt-3">Sign-in</button>
      </form>
      <div class="mt-2">
        <button type="button" class="btn btn-link" v-on:click="toRegistration()">Registration</button>
      </div>
      <div class="mt-2">
        <a href="/oauth2/authorization/google" >Sign in with Google</a>
      </div>
    </div>
  </main>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ErrorsView from "@/components/ErrorsView.vue";

export default {
  components: { ErrorsView },
  data() {
    return {
      email: '',
      password: '',
      errors: [],
    }
  },
  methods: {
    submitForm() {
      this.errors = []

      const user = {
        email: this.email,
        password: this.password
      }
      AXIOS.post('/auth/login', user)
          .then(response => {
            this.$store.dispatch('loginAction', {
              authenticate: true,
              roles: response.data.roles,
              name: response.data.name,
              picture: response.data.userPicture,
              id: response.data.id
            })

            this.$router.push('/profile')
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
              this.errors.push(error.response.data)
            }
          })
    },
    toRegistration() {
      this.$router.push('/registration')
    }
  },
  mounted() {
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
      form.addEventListener('submit', event => {
        if (!form.checkValidity()) {
          event.preventDefault();
          event.stopPropagation();
        }

        form.classList.add('was-validated');
      }, false);
    });
  },
}
</script>

<style scoped>
.form-signin {
  max-width: 400px;
  margin: 0 auto;
}
</style>