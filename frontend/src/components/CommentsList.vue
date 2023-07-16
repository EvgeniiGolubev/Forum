<template>
  <div class="container">
    <div class="card my-3 p-3 bg-body rounded shadow-sm">
      <h5 class="border-bottom pb-2 mb-0 text-md-start">Comments</h5>
      <div class="d-flex justify-content-between align-items-center pt-3" v-for="comment in comments" :key="comment.id">
        <div class="d-flex">
          <img :src="getImagePath(comment.author.userPicture)" class="bd-placeholder-img flex-shrink-0 me-2 rounded" style="width: 50px; height: 50px;">
          <div class="pb-3 mb-0 lh-sm text-md-start">
            <strong class="d-block text-gray-dark">{{ comment.author.name }}</strong>
            <div class="comment-content">
              {{ comment.content }}
            </div>
          </div>
        </div>
        <div>
          <button class="btn btn-danger" @click.prevent="deleteComment(comment.id)" style="height: 35px;"
                  v-if="commentOwnerId === comment.author.id">
            X
          </button>
        </div>
      </div>
      <div class="d-flex text-body-secondary pt-3" v-if="articleId">
        <textarea class="form-control" rows="2" v-model="newComment" maxlength="600"></textarea>
        <input type="button" value="Submit comment" v-on:click="submitComment(articleId)"
               class="btn btn-outline-primary" style="height: 40px; margin-left: 25px;"/>
      </div>
    </div>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  props: ['comments', 'articleId'],
  emits: ['commentAdded', 'commentDeleted'],
  data() {
    return {
      newComment: '',
      commentOwnerId: null,
    }
  },
  methods: {
    submitComment(articleId) {
      AXIOS.post(`/comments/article/${articleId}`, { content: this.newComment })
          .then(response => {
            this.$emit('commentAdded', response.data);
          })
          .catch(error => {
            this.handleError(error)
          })

      this.newComment = ''
    },
    deleteComment(commentId) {
      AXIOS.delete(`/comments/${commentId}`)
          .then(response => {
            console.log(response)
            this.$emit('commentDeleted', commentId);
          })
          .catch(error => {
            this.handleError(error)
          })
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
    handleError(error) {
      this.spinnerVisible = false

      if (error) {
        console.log(error)
        if (!Array.isArray(error.response.data)) {
          this.errors.push(error.response.data)
        }
      }
    },
  },
  mounted() {
    this.commentOwnerId = this.$store.getters.getId
  }
}
</script>

<style scoped>
.comment-content {
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: pre-line;
}
</style>