<template>
  <div class="container">
    <div class="card my-3 p-3 bg-body rounded shadow-sm">
      <h5 class="border-bottom pb-2 mb-0 text-md-start">Comments</h5>
      <div class="d-flex text-body-secondary pt-3" v-for="comment in comments" :key="comment.id">
        <img :src="getImagePath(comment.author.userPicture)" class="bd-placeholder-img flex-shrink-0 me-2 rounded" style="width: 50px; height: 50px;">
        <p class="pb-3 mb-0 lh-sm border-bottom text-md-start">
          <strong class="d-block text-gray-dark">{{ comment.author.name }}</strong>
          {{ comment.content }}
        </p>
      </div>
      <hr>
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
  emits: ['newComment'],
  data() {
    return {
      newComment: '',
    }
  },
  methods: {
    submitComment(articleId) {
      AXIOS.post(`/comments/${articleId}`, {newComment: this.newComment})
          .then(response => {
            this.$emit('newComment', response.data);
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
  }
}
</script>

<style scoped>

</style>