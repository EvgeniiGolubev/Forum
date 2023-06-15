<template>
  <h1>Create or update article</h1>
  <article-form v-bind:article="article" v-if="visibleForm" @form-submitted="visibleForm = false"/>

  <button type="button" @click="subscribe(article.author.id)">Subscribe</button>
  <button type="button" @click="deleteArticle(article.id)">Delete</button>
  <button type="button" @click="editArticle()">Edit</button>

  <div>{{ article.id }}</div>
  <div>{{ article.title }}</div>
  <div>{{ article.content }}</div>
  <div>
    <div v-for="(imageLink, index) in article.imageLinks" :key="index">
      <img :src="getImagePath(imageLink)" alt="Article Image">
    </div>
  </div>
  <div>{{ article.author.name }}</div>
  <div>{{ article.creationDate }}</div>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ArticleForm from "@/components/ArticleForm.vue";

export default {
  components: {ArticleForm},
  data() {
    return {
      article: {
        id: 1,
        author: {
          name: "ppp"
        }
      },
      visibleForm: false,
    }
  },
  mounted() {
    const articleId = this.$route.query.id;
    this.getArticle(articleId);
  },
  methods: {
    getArticle(articleId) {
      console.log(articleId)
    },
    subscribe(id) {
      AXIOS.post(`/profile/subscriptions/${id}`, {}, {params: {subscriptionStatus: true}})
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
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
    editArticle() {
      this.visibleForm = true
    },
    getImagePath(link) {
      if (link) {
        if (link.startsWith('https://')) {
          return link
        } else {
          return `/img/${link}`
        }
      }

      return 'https://via.placeholder.com/150';
    }
  }
}
</script>

<style>

</style>