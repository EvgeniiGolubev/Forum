<template>
  <errors-view v-bind:errors="errors"/>
  <alert-view v-bind:alert-message="alertMessage"/>

  <main class="form-signin">
    <div class="container">
      <form ref="form" @submit="submitForm" class="needs-validation" novalidate>
        <img class="mb-4" src="/img/logo.png" width="100" height="100"/>
        <h1 class="h3 mb-3 fw-normal">Sign up</h1>
        <div class="form-floating">
          <input type="email" class="form-control" id="email" placeholder="name@example.com" maxlength="255"
                 v-model="email" required>
          <label for="email">Email address</label>
          <div class="invalid-feedback" >Please provide a valid email address.</div>
        </div>
        <div class="form-floating mt-3">
          <input type="text" class="form-control" id="name" placeholder="Name" maxlength="255"
                 v-model="name" required>
          <label for="name">Name</label>
          <div class="invalid-feedback" >Please provide a valid name.</div>
        </div>
        <div class="form-floating mt-3">
          <input type="password" class="form-control" id="password" placeholder="Password" maxlength="255"
                 v-model="password" required minlength="1">
          <label for="password">Password</label>
          <div class="invalid-feedback" >Password must be at least 8 characters long.</div>
        </div>
        <div class="form-floating mt-3">
          <input type="password" class="form-control" id="confirm-password" maxlength="255"
                 placeholder="Confirm password" v-model="confirmPassword" required :pattern="password">
          <label for="confirm-password">Confirm password</label>
          <div class="invalid-feedback" >Passwords must match.</div>
        </div>
        <button class="btn btn-primary w-100 mt-3">Sign up</button>
      </form>
      <div class="mt-4">
        <a href="/oauth2/authorization/google" >Sign up with Google</a>
      </div>
    </div>
  </main>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ErrorsView from "@/components/ErrorsView.vue";
import AlertView from "@/components/AlertView.vue";

export default {
  components: {AlertView, ErrorsView},
  data() {
    return {
      email: '',
      name: '',
      password: '',
      confirmPassword: '',
      errors: [],
      alertMessage: null,
    }
  },
  methods: {
    submitForm() {
      const form = this.$refs.form;
      if (!form.checkValidity()) {
        return;
      }

      this.errors = []

      const user = {
        email: this.email,
        name: this.name,
        password: this.password,
        confirmPassword: this.confirmPassword,
      }

      AXIOS.post('/auth/register', user)
          .then(response => {
            console.log(response.data)
            this.alertMessage = 'An account confirmation message has been sent to your email.'

            setTimeout(() => {
              this.alertMessage = null
              this.$router.push('/main')
            }, 5000)
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
              this.errors.push(error.response.data)
            }
          })
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

<style>
.form-signin {
  max-width: 400px;
  margin: 0 auto;
}
</style>