<template>
  <div class="alert-holder">
    <div class="alert alert-danger" role="alert" v-for="(error, index) in errors" :key="index" v-bind:class="{ 'show': errors.length }">
      {{ error.message }}
    </div>
    <div class="alert alert-success" role="alert" v-if="alertMessage" v-bind:class="{ 'show': alertMessage }">
      {{ alertMessage }}
    </div>
  </div>

  <main class="form-signin">
    <div class="container">
      <form ref="form" @submit.prevent="submitForm" class="needs-validation" novalidate>
        <img class="mb-4" src="/logo.png" width="100" height="100"/>
        <h1 class="h3 mb-3 fw-normal">Sign up</h1>
        <div class="form-floating">
          <input type="email" class="form-control" id="email" placeholder="name@example.com" v-model="email" required>
          <label for="email">Email address</label>
          <div class="invalid-feedback" >Please provide a valid email address.</div>
        </div>
        <div class="form-floating mt-3">
          <input type="text" class="form-control" id="name" placeholder="Name" v-model="name" required>
          <label for="name">Name</label>
          <div class="invalid-feedback" >Please provide a valid name.</div>
        </div>
        <div class="form-floating mt-3">
          <input type="password" class="form-control" id="password" placeholder="Password" v-model="password" required minlength="1">
          <label for="password">Password</label>
          <div class="invalid-feedback" >Password must be at least 8 characters long.</div>
        </div>
        <div class="form-floating mt-3">
          <input type="password" class="form-control" id="confirm-password" placeholder="Confirm password" v-model="confirmPassword" required :pattern="password">
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

export default {
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
            console.log(response.data.message)
            this.alertMessage = 'An account confirmation message has been sent to your email.'
            setTimeout(() => {
              this.alertMessage = null
              this.$router.push('/')
            }, 5000)
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
              this.errors.push(error.response.data)
            }

            setTimeout(() => {
              this.errors = [];
            }, 5000)
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