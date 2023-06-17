<template>
  <div class="alert-holder">
    <div class="alert alert-danger" role="alert" v-for="(error, index) in errors" :key="index" v-bind:class="{ 'show': errors.length }">
      {{ error.message }}
    </div>
  </div>

  <div class="card text-center bg-card">
    <form ref="form" @submit.prevent="submitForm" class="needs-validation m-3" novalidate>
      <div class="form-floating">
        <input type="text" class="form-control" id="floatingInput" v-model="title" required>
        <label for="floatingInput">Title</label>
        <div class="invalid-feedback" >Title can not be empty.</div>
      </div>
      <div class="form-floating mt-3">
        <textarea class="form-control" rows="3" id="content" v-model="content" required style="height: 300px;"></textarea>
        <label for="content">Content</label>
        <div class="invalid-feedback">Content can not be empty.</div>
      </div>
      <div class="mt-3">
        <input class="form-control" type="file" multiple @change="handleFileChange">
      </div>
      <div class="d-flex justify-content-between mt-3">
        <button class="btn btn-primary" style="width: 100px;">{{ buttonLabel }}</button>
      </div>
    </form>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  props: ['selectedArticle'],
  data() {
    return {
      id: '',
      title: '',
      content: '',
      images: [],
      errors: [],
    }
  },
  mounted() {
    if (this.selectedArticle) {
      this.id = this.selectedArticle.id
      this.title = this.selectedArticle.title
      this.content = this.selectedArticle.content
      if (this.selectedArticle.images) {
        this.images = this.selectedArticle.images
      }
    }

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
  computed: {
    buttonLabel() {
      return this.id ? 'Update' : 'Save';
    }
  },
  methods: {
    submitForm() {
      const formData = new FormData();
      formData.append('title', this.title);
      formData.append('content', this.content);
      for (let i = 0; i < this.images.length; i++) {
        formData.append('images', this.images[i]);
      }

      const headers = {
        'Content-Type': 'multipart/form-data'
      }

      if (this.id) {
        AXIOS.put(`/articles/${this.id}`, formData, { headers: headers })
            .then(response => {
              console.log(response.data)
            })
            .catch(error => {
              this.handleError(error)
            })
      } else {
        AXIOS.post(`/articles`, formData, { headers: headers })
            .then(response => {
              console.log(response.data)
            })
            .catch(error => {
              this.handleError(error)
            })
      }

      if (this.errors.length === 0) {
        this.id = ''
        this.title = ''
        this.content = ''
        this.images = []
        this.$emit('form-submitted');
      }
    },
    handleFileChange(event) {
      for (let file of event.target.files) {
        this.images.push(file)
      }
      // this.images.push(event.target.files[0]);
    },
    handleError(error) {
      if (!Array.isArray(error.response.data)) {
        this.errors.push(error.response.data)
      }

      setTimeout(() => {
        this.errors = [];
      }, 5000)
    }
  }
}
</script>

<style>

</style>