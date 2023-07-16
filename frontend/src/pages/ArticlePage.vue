<template>
  <errors-view v-bind:errors="errors"/>

  <main class="container">
    <div v-if="visibleForm">
      <article-form v-bind:selected-article="article"  @form-submitted="reloadPage"/>
    </div>

    <div class="d-flex justify-content-between align-items-center my-2" role="group" v-if="isAuthor">
      <div>
        <button type="button" class="btn btn-outline-danger" @click="deleteArticle(article.id)">Delete article</button>
        <button type="button" class="btn btn-outline-primary mx-2" @click="editArticle()">Edit article</button>
      </div>
    </div>

    <div class="card text-center bg-card" v-if="article">
      <div class="card-header bg-card">
        <div id="carousel" class="carousel slide" v-if="showCarousel">
          <div class="carousel-inner">
            <div v-for="(imageLink, index) in article.imageLinks" :key="index" :class="['carousel-item', index === 0 ? 'active' : '']"
                 style="height: 500px;">
              <img :src="getImagePath(imageLink)" class="d-block w-100 image-container" alt="Article Image">
            </div>
          </div>
          <button class="carousel-control-prev" type="button" data-bs-target="#carousel" data-bs-slide="prev">
            <span class="carousel-control-prev-icon arrow" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
          </button>
          <button class="carousel-control-next" type="button" data-bs-target="#carousel" data-bs-slide="next">
            <span class="carousel-control-next-icon arrow" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
          </button>
        </div>
      </div>
      <div class="card-body">
        <h5 class="card-title">{{ article.title }}</h5>
        <p class="card-text text-md-start">{{ article.content }}</p>
        <a href="#" class="btn btn-link text-without-underline" v-on:click="openAuthorProfile(article.author.id)">{{ article.author.name }}</a>
      </div>
      <div class="card-footer text-body-secondary bg-card">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <input type="button" value="Comments" v-on:click="openComments(article.id)"
                   class="btn btn-outline-primary"/>
          </div>
          <div class="w-100" v-on:click="likeArticle(article.id)">
            <font-awesome-icon :icon="['fas', 'heart']" size="2xl" style="color: #3e6cf4;" class="heart" v-if="articleLiked"/>
            <font-awesome-icon :icon="['far', 'heart']" size="2xl" style="color: #3e6cf4;" class="heart" v-else/>
            <span>{{ articleLikes }}</span>
          </div>
          <small class="text-body-secondary">{{ formatDateTime(article.creationDate) }}</small>
        </div>
      </div>
    </div>
  </main>

  <comments-list v-bind:comments="comments" v-bind:article-id="article.id" @comment-added="newCommentAdded"
                 @comment-deleted="commentDeleted" v-if="commentsOpen"/>

</template>

<script>
import {AXIOS} from "@/http-commons";
import ArticleForm from "@/components/ArticleForm.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome"
import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'
import ErrorsView from "@/components/ErrorsView.vue";
import CommentsList from "@/components/CommentsList.vue";

library.add(fas, far)

export default {
  components: {CommentsList, ErrorsView, ArticleForm, FontAwesomeIcon},
  data() {
    return {
      errors: [],
      article: null,
      comments: [],
      visibleForm: false,
      showCarousel: true,
      articleLiked: false,
      commentsOpen: false,
      isAuthor: false,
      articleLikes: 24,
      articleId: 0,
    }
  },
  methods: {
    getArticle(articleId) {
      AXIOS.get(`/articles/${articleId}`)
          .then(response => {
            this.article = response.data
            this.isAuthor = this.$store.getters.getId === this.article.author.id
            this.showCarousel = this.article.imageLinks.length > 0
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    openComments(articleId) {
      AXIOS.get(`/comments/article/${articleId}`)
          .then(response => {
            this.comments = response.data
          })
          .catch(error => {
            this.handleError(error)
          })
      this.commentsOpen = !this.commentsOpen
    },
    likeArticle(articleId) {
      console.log(articleId)

      if (this.articleLiked) {
        this.articleLiked = false
        this.articleLikes--
      } else {
        this.articleLiked = true
        this.articleLikes++
      }
    },
    openAuthorProfile(authorId) {
      this.$router.push({name: 'UserProfile', params: {id: authorId}});
    },
    deleteArticle(id) {
      AXIOS.delete(`/articles/${id}`)
          .then(response => {
            console.log(response)
            this.$router.push("/")
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    editArticle() {
      this.visibleForm = !this.visibleForm
    },
    newCommentAdded(comment) {
      this.comments.push(comment)
    },
    commentDeleted(commentId) {
      const index = this.comments.findIndex(comment => comment.id === commentId);
      if (index !== -1) {
        this.comments.splice(index, 1);
      }
    },
    getImagePath(link) {
      if (link && typeof link === 'string') {
        if (link.startsWith('https://')) {
          return link
        } else {
          return `/img/${link}`
        }
      }

      return '/img/icon/paw.png';
    },
    formatDateTime(dateTimeString) {
      const options = { day: 'numeric', month: 'long', hour: 'numeric', minute: 'numeric' };
      const dateTime = new Date(dateTimeString);
      const date = dateTime.toLocaleDateString('en-US', options);
      return `${date}`;
    },
    handleError(error) {
      if (!Array.isArray(error.response.data)) {
        this.errors.push(error.response.data)
      }
    },
    reloadPage(id) {
      this.visibleForm = false
      this.articleId = id
      this.getArticle(id)
    }
  },
  mounted() {
    const articleId = this.$route.params.id;
    if (articleId) {
      this.articleId = articleId
      this.getArticle(articleId);
    }
  },
}
</script>

<style>
.image-container {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.arrow {
  background-color:#007bff;
}

.bg-card {
  background-color:white;
}

.heart {
  margin-right: 5px;
}

.text-without-underline {
  text-decoration: none;
}
</style>