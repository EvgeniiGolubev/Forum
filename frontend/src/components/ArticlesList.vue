<template>
  <div class="album py-5">
    <div class="container">
      <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">

        <div class="col" v-for="article in articles" :key="article.id" >
          <div class="card shadow-sm" style="padding-top: 10px;">
            <img :src="getImagePath(article.imageLinks[0])" class="bd-placeholder-img card-img-top" style="object-fit: contain; max-height: 300px; height: 300px;">
            <div class="card-body">
              <h4 class="card-title text-md-start">{{ article.title }}</h4>
              <p class="card-text text-md-start">{{ truncateText(article.content) }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <button type="button" class="btn btn-link text-without-underline" v-on:click="openAuthorProfile(article.author.id)">
                  {{ article.author.name }}
                </button>
              </div>
              <hr>
              <div class="d-flex justify-content-between align-items-center">
                <div class="d-flex justify-content-between align-items-center">
                  <input type="button" value="Read" v-on:click="openArticle(article.id)"
                         class="btn btn-outline-primary"/>
                  <div>
                    <font-awesome-icon :icon="['fas', 'heart']" size="2xl" style="color: #3e6cf4;" class="heart"/>
                    <span>{{ article.likes }}</span>
                  </div>
                </div>
                <small class="text-body-secondary">{{ formatDateTime(article.creationDate) }}</small>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome"
import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'

library.add(fas, far)

export default {
  components: {FontAwesomeIcon},
  props: ['articles'],
  methods: {
    openArticle(articleId) {
      if (!this.$store.getters.isAuthenticated) {
        this.$router.push('/login')
      } else {
        this.$router.push({ name: 'ArticlePage', params: { id: articleId } });
      }
    },
    openAuthorProfile(authorId) {
      if (!this.$store.getters.isAuthenticated) {
        this.$router.push('/login')
      } else {
        this.$router.push({name: 'UserProfile', params: {id: authorId}});
      }
    },
    getImagePath(link) {
      if (link) {
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
    truncateText(content) {
      if (content.length <= 100) {
        return content
      } else {
        return content.slice(0, 100) + '...'
      }
    }
  }
}
</script>

<style scoped>
.heart {
  margin-left: 15px;
  margin-right: 10px;
}


.text-without-underline {
  text-decoration: none;
}
</style>