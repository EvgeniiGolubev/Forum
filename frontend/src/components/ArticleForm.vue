<template>
  <errors-view v-bind:errors="errors"/>

  <div class="card text-center bg-card">
    <form ref="form" @submit="submitForm" class="needs-validation m-3" novalidate>
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
        <div class="container">
          <p class="text-md-start">Maximum photo size 5Mb</p>
        </div>
      </div>
      <div class="container mt-3" v-if="imageLinks">
        <div class="row align-items-center">
          <div class="card image-holder" v-for="(image, index) in imageLinks" :key="index">
            <img :src="getImagePath(image)" class="bd-placeholder-img flex-shrink-0 me-2 rounded image-container img-preview">
            <button class="btn btn-danger btn-delete" @click.prevent="deleteImage(image)">X</button>
          </div>
          <div class="card image-holder" v-for="(image, index) in newImages" :key="index">
            <div class="spinner-border text-primary spinner" role="status" v-if="spinnerVisible">
              <span class="visually-hidden">Loading...</span>
            </div>
            <img :src="getImagePath(image)" class="bd-placeholder-img flex-shrink-0 me-2 rounded image-container img-preview">
            <button class="btn btn-danger btn-delete" @click.prevent="deleteImage(image)">X</button>
          </div>
          <div class="file-upload"  v-if="imageLinks.length < 6">
            <label for="file-input">
              <div class="card upload-box">
                <font-awesome-icon :icon="['fas', 'plus']" size="2xl" style="color: #3e6cf4;"/>
              </div>
            </label>
            <input id="file-input" class="form-control" type="file" @change="handleFileChange" style="display: none"/>
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
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'

library.add(fas, far)

export default {
  components: {FontAwesomeIcon, ErrorsView },
  emits: ['formSubmitted'],
  props: ['selectedArticle'],
  data() {
    return {
      id: '',
      title: '',
      content: '',
      imageLinks: [],
      newImages: [],
      errors: [],
      spinnerVisible: false,
    }
  },
  computed: {
    buttonLabel() {
      return this.id ? 'Update' : 'Save';
    }
  },
  methods: {
    submitForm(event) {
      event.preventDefault()

      const form = this.$refs.form;
      if (!form.checkValidity()) {
        return;
      }

      this.spinnerVisible = true

      const formData = new FormData();
      formData.append('title', this.title);
      formData.append('content', this.content);

      for (let i = 0; i < this.imageLinks.length; i++) {
        formData.append('imageLinks', this.imageLinks[i]);
      }

      for (let i = 0; i < this.newImages.length; i++) {
        formData.append('newImages', this.newImages[i]);
      }

      const headers = {
        'Content-Type': 'multipart/form-data'
      }

      if (this.id) {
        AXIOS.put(`/articles/${this.id}`, formData, { headers: headers })
            .then(response => {
              this.spinnerVisible = false
              this.$emit('formSubmitted', response.data.id);
            })
            .catch(error => {
              this.handleError(error)
            })
      } else {
        AXIOS.post(`/articles`, formData, { headers: headers })
            .then(response => {
              this.spinnerVisible = false
              this.$emit('formSubmitted', response.data.id);
            })
            .catch(error => {
              this.handleError(error)
            })
      }
    },
    handleFileChange(event) {
      const maxFileSize = 5 * 1024 * 1024; // Максимальный размер файла (5 МБ)

      for (let file of event.target.files) {
        if (file.size > maxFileSize) {
          this.errors.push({ message: 'The maximum photo size has been exceeded!' })
          return;
        } else {
          this.newImages.push(file);
        }
      }
    },
    deleteImage(image) {
      if (!image) return

      if (typeof image === 'string') {
        const index = this.imageLinks.indexOf(image);
        if (index > -1) {
          this.imageLinks.splice(index, 1);
        }
      } else {
        const index = this.newImages.indexOf(image);
        if (index > -1) {
          this.newImages.splice(index, 1);
        }
      }

    },
    handleError(error) {
      this.spinnerVisible = false

      if (error) {
        if (!Array.isArray(error.response.data)) {
          this.errors.push(error.response.data)
        }
      }
    },
    getImagePath(image) {
      if (!image) return '/img/icon/paw.png'

      if (typeof image === 'string') {
        if (image.startsWith('https://')) {
          return image
        } else {
          return `/img/${image}`
        }
      } else {
        return URL.createObjectURL(image)
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
.file-upload {
  position: relative;
  width: 150px;
  height: 150px;
}

.upload-box,
.image-preview {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.spinner {
  position: absolute;
  right: 35px;
  bottom: 35px;
  width: 80px;
  height: 80px;
}

.image-holder {
  width: 150px;
  height: 150px;
  margin: 5px;
}

.img-preview {
  width: 150px;
  height: 150px;
}

.btn-delete {
  position: absolute;
  left: 0;
  top: 0;
}
</style>