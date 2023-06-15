<template>
  <div>
    <input type="text" v-model="title" placeholder="title"/>
    <textarea v-model="content" placeholder="content"/>
    <input type="file" @change="handleFileChange">
    <input type="button" :value="buttonLabel" @click="save"/>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  props: ['article'],
  data() {
    return {
      id: '',
      title: '',
      content: '',
      images: [],
    }
  },
  watch: {
    article(newArt) {
      this.id = newArt.id
      this.title = newArt.title
      this.content = newArt.content
      this.images = newArt.images
    }
  },
  computed: {
    buttonLabel() {
      return this.id ? 'Update' : 'Save';
    }
  },
  methods: {
    save() {
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
              let index = this.articles.findIndex(item => item.id === response.data.id)
              this.articles.splice(index, 1, response.data);
            })
            .catch(error => {
              if (Array.isArray(error.response.data)) {
                this.errors = error.response.data
              } else {
                this.errors.push(error.response.data)
              }
            })
      } else {
        AXIOS.post(`/articles`, formData, { headers: headers })
            .then(response => {
              this.articles.push(response.data)
            })
            .catch(error => {
              if (Array.isArray(error.response.data)) {
                this.errors = error.response.data
              } else {
                this.errors.push(error.response.data)
              }
            })
      }

      this.id = ''
      this.title = ''
      this.content = ''
      this.images = []
      this.$emit('form-submitted');
    },
    handleFileChange(event) {
      // Обработчик события изменения файла
      this.images.push(event.target.files[0]);
    },
  }
}
</script>

<style>

</style>