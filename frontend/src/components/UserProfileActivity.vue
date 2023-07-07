<template>
  <div class="card text-center">
    <div class="card-header">
      <ul class="nav nav-tabs card-header-tabs">
        <li class="nav-item">
          <a class="nav-link" :class="{ active: isActive('articles') }" aria-current="true" href="#" @click.prevent="setActive('articles')">
            Articles
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: isActive('comments') }" href="#" @click.prevent="setActive('comments')">
            Comments
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: isActive('subscribers') }" href="#" @click.prevent="setActive('subscribers')">
            Subscribers
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" :class="{ active: isActive('subscriptions') }" href="#" @click.prevent="setActive('subscriptions')">
            Subscriptions
          </a>
        </li>
      </ul>
    </div>

    <div class="card-body" v-if="visibleSections.articles">
      <articles-list v-bind:articles="articles"/>
    </div>

    <div class="card-body" v-if="visibleSections.comments">
      <comments-list v-bind:comments="comments"/>
    </div>

    <div class="card-body" v-if="visibleSections.subscribers">
      <subscribers-list v-bind:subscribers="subscribers" v-bind:is-profile-owner="isProfileOwner"/>
    </div>

    <div class="card-body" v-if="visibleSections.subscriptions">
      <subscriptions-list v-bind:subscriptions="subscriptions" v-bind:is-profile-owner="isProfileOwner"/>
    </div>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ArticlesList from "@/components/ArticlesList.vue";
import CommentsList from "@/components/CommentsList.vue";
import SubscribersList from "@/components/SubscribersList.vue";
import SubscriptionsList from "@/components/SubscriptionsList.vue";

export default {
  components: {SubscriptionsList, SubscribersList, CommentsList, ArticlesList},
  props: ['userProfile', 'isProfileOwner'],
  emits: ['errors'],
  data() {
    return {
      activeClass: 'articles',
      visibleSections: {
        articles: true,
        comments: false,
        subscribers: false,
        subscriptions: false,
      },
      subscribers: [],
      subscriptions: [],
      articles: [],
      comments: [],
    }
  },
  methods: {
    isActive(className) {
      return this.activeClass === className;
    },
    setActive(className) {
      this.activeClass = className;
      this.visibleSections = Object.keys(this.visibleSections).reduce((acc, section) => {
        acc[section] = section === className;
        return acc;
      }, {});
    },
    getSubscribers() {
      AXIOS.get(`/profile/${this.userProfile.id}/subscribers`)
          .then(response => {
            this.subscribers = response.data
          })
          .catch(error => {
            this.$emit('errors', error);
          })
    },
    getSubscriptions() {
      AXIOS.get(`/profile/${this.userProfile.id}/subscriptions`)
          .then(response => {
            this.subscriptions = response.data
          })
          .catch(error => {
            this.$emit('errors', error);
          })
    },
    getArticles() {
      AXIOS.get(`/articles/author/${this.userProfile.id}`)
          .then(response => {
            this.articles = response.data
          })
          .catch(error => {
            this.$emit('errors', error);
          })
    },
    getComments() {
      AXIOS.get(`/comments/author/${this.userProfile.id}`)
          .then(response => {
            this.comments = response.data
          })
          .catch(error => {
            this.$emit('errors', error);
          })
    },
  },
  mounted() {
    this.getSubscribers()
    this.getSubscriptions()
    this.getArticles()
    this.getComments()
  }
}
</script>

<style >

</style>