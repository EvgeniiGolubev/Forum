<template>
  <div>
    Response message:
    {{ responseMessages }}
  </div>

  <div>
    Errors:
    <ul>
      <li v-for="(error, index) in errors" :key="index">{{ error.message }}</li>
    </ul>
  </div>

  <br>

  <div>
    <input type="text" v-model="title" placeholder="title"/>
    <textarea v-model="content" placeholder="content"/>
    <input type="file" @change="handleFileChange">
    <input type="button" :value="buttonLabel" @click="save"/>
  </div>

  <br>

  <h3>Change pagination</h3>
  <div>
    <input type="number" v-model="currentPage" placeholder="Current page"/>
    <input type="number" v-model="pageSize" placeholder="Page size"/>
    <label for="sort-select">Выберите сортировку:</label>
    <select id="sort-select" v-model="sortType">
      <option value="asc">По возрастанию</option>
      <option value="desc">По убыванию</option>
    </select>
    <button type="button" @click="handlePageChange">Submit</button>
  </div>

  <br>

  <table>
    <thead>
    <tr>
      <th>Id</th>
      <th>Title</th>
      <th>Content</th>
      <th>Image</th>
      <th>Author</th>
      <th>Date</th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="(article, index) in articles" :key="index">
      <td>{{ article.id }}</td>
      <td>{{ article.title }}</td>
      <td>{{ article.content }}</td>
      <td>
        <div v-for="(imageLink, index) in article.imageLinks" :key="index">
          <img :src="getImagePath(imageLink)" alt="Article Image">
        </div>
      </td>
      <td>{{ article.author.name }}</td>
      <td>{{ article.creationDate }}</td>
      <td>
        <button type="button" @click="subscribe(article.author.id)">Subscribe</button>
      </td>
      <td>
        <button type="button" @click="deleteArticle(article.id)">Delete</button>
      </td>
      <td>
        <button type="button" @click="editArticle(article)">Edit</button>
      </td>
    </tr>
    </tbody>
  </table>
</template>

<script>
import {AXIOS} from "@/http-commons";
export default {
  data() {
    return {
      responseMessages: '',
      articles: [],
      errors: [],
      id: '',
      title: '',
      content: '',
      images: [],
      selectedArticle: null,
      currentPage: 0,
      pageSize: 5,
      sortType: 'desc'
    }
  },
  watch: {
    selectedArticle(newArt) {
      this.id = newArt.id
      this.title = newArt.title
      this.content = newArt.content
    }
  },
  computed: {
    buttonLabel() {
      return this.id ? 'Update' : 'Save';
    }
  },
  methods: {
    getArticles() {
      const params = {
        pageSize: this.pageSize,
        page: this.currentPage,
        sortType: this.sortType
      }

      AXIOS.get("/articles", {params: params})
          .then(response => {
            this.articles = response.data.content
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    deleteArticle(id) {
      AXIOS.delete(`/articles/${id}`)
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    editArticle(article) {
      this.selectedArticle = article
    },
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
    },
    subscribe(id) {
      AXIOS.post(`/profile/subscriptions/${id}?subscriptionStatus=${true}`)
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    handleFileChange(event) {
      // Обработчик события изменения файла
      this.images.push(event.target.files[0]);
    },
    handlePageChange() {
      this.getArticles()
    },
    getImagePath(link) {
      if (link) {
        if (link.startsWith('https://')) {
          return link
        } else {
          return `/img/${link}`
        }
      }

      return '/img/placeholder.jpg';
    }
  },
  mounted() {
    this.getArticles()
  },
}
</script>

<style scoped>

</style>
