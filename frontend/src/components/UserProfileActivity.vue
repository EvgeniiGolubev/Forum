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
      subscribers: [
        {
          "id": 3,
          "name": "Evgenii",
          "userPicture": "5eb09a98-a02c-493c-a304-36aa40871e97.jpg"
        }
      ],
      subscriptions: [
        {
          "id": 3,
          "name": "Evgenii",
          "userPicture": "5eb09a98-a02c-493c-a304-36aa40871e97.jpg"
        }
      ],
      articles: [
        {
          "id": 18,
          "title": "Now",
          "content": "dsfsdfsdf",
          "author": {
            "id": 1,
            "name": "Евгений Голубев",
            "userPicture": "https://lh3.googleusercontent.com/a/AAcHTtczi1vwo2wvg2kljQKuFXt6KVP4d-Yoq0XSyoNySA=s96-c"
          },
          "creationDate": "2023-07-01 01:08:25",
          "imageLinks": []
        }
      ],
      comments: [{
        id: 1,
        author: {
          name: 'Biba',
          userPicture: "377164d4-8faf-4f31-8c57-9ead5f1bd78f.png",
        },
        content: 'blabalblablablalbalblalblablalbalblalb'
      },
        {
          id: 1,
          author: {
            name: 'Biba',
            userPicture: "377164d4-8faf-4f31-8c57-9ead5f1bd78f.png",
          },
          content: 'blabalblablablalbalblalblablalbalblalb'
        }],
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
      AXIOS.get(`/profile/subscribers/${this.userProfile.id}`)
          .then(response => {
            this.subscribers = response.data
          })
          .catch(error => {
            this.$emit('errors', error);
          })
    },
    getSubscriptions() {
      AXIOS.get(`/profile/subscriptions/${this.userProfile.id}`)
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