<template>
  <errors-view v-bind:errors="errors"/>

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
      <div class="container mt-3" v-if="imageLinks">
        <div class="row align-items-center">
          <div class="card" style="width: 12rem; padding: 20px; margin: 5px;" v-for="(image, index) in imageLinks" :key="index">
            <img :src="getImagePath(image)" class="bd-placeholder-img flex-shrink-0 me-2 rounded image-container" style="width: 150px; height: 150px;">
            <button class="btn btn-danger" style="position: absolute; left: 0; top: 0;" @click.prevent="deleteImage(image)">X</button>
          </div>
        </div>
      </div>
      <div class="d-flex justify-content-between mt-3">
        <button class="btn btn-primary" style="width: 100px;">{{ buttonLabel }}</button>
      </div>
    </form>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ErrorsView from "@/components/ErrorsView.vue";

export default {
  components: { ErrorsView },
  props: ['selectedArticle'],
  data() {
    return {
      id: '',
      title: '',
      content: '',
      imageLinks: [],
      errors: [],
    }
  },
  computed: {
    buttonLabel() {
      return this.id ? 'Update' : 'Save';
    }
  },
  methods: {
    submitForm() {
      this.errors = []

      const formData = new FormData();
      formData.append('title', this.title);
      formData.append('content', this.content);
      for (let i = 0; i < this.imageLinks.length; i++) {
        formData.append('images', this.imageLinks[i]);
      }

      const headers = {
        'Content-Type': 'multipart/form-data'
      }

      if (this.id) {
        AXIOS.put(`/articles/${this.id}`, formData, { headers: headers })
            .then(response => {
              console.log(response.data)
              this.$emit('form-submitted');
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
    },
    handleFileChange(event) {
      for (let file of event.target.files) {
        this.imageLinks.push(file)
      }
    },
    deleteImage(link) {
      AXIOS.delete(`/articles/delete-image/${this.id}`, {params: {imageLink: link}})
          .then(response => {
            const index = this.imageLinks.indexOf(link);
            if (index > -1) {
              this.imageLinks.splice(index, 1);
            }

            console.log(response)
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    handleError(error) {
      if (!Array.isArray(error.response.data)) {
        this.errors.push(error.response.data.message)
      }
    },
    getImagePath(link) {
      if (!link) return '/img/icon/paw.png'

      if (typeof link === 'string') {
        if (link.startsWith('https://')) {
          return link
        } else {
          return `/img/${link}`
        }
      } else {
        return URL.createObjectURL(link)
      }
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

    if (this.selectedArticle) {
      this.id = this.selectedArticle.id
      this.title = this.selectedArticle.title
      this.content = this.selectedArticle.content
      if (this.selectedArticle.imageLinks) {
        this.imageLinks = this.selectedArticle.imageLinks
      }
    }
  },
}
</script>

<style>

</style>