<template>
  <div class="container">
    <div class="card my-3 p-3 bg-body rounded shadow-sm">
      <h5 class="border-bottom pb-2 mb-0 text-md-start">Subscribers</h5>
      <div class="d-flex justify-content-between align-items-center pt-3" v-for="subscriber in subscribers" :key="subscriber.id">
        <div class="d-flex align-items-center">
          <img :src="getImagePath(subscriber.userPicture)" class="bd-placeholder-img flex-shrink-0 me-2 rounded" style="width: 50px; height: 50px;">
          <a href="#" class="btn btn-link text-without-underline" v-on:click="openProfile(subscriber.id)">{{ subscriber.name }}</a>
        </div>
        <div v-if="isProfileOwner">
          <button type="button" class="btn btn-outline-primary" @click="changeSubscriberStatus(subscriber.id)" v-if="!isSubscribed(subscriber.id)">
            Subscribe
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  props: ['subscribers', 'isProfileOwner', 'subscriptions'],
  methods: {
    openProfile(id) {
      this.$router.push({name: 'UserProfile', params: {id: id}});
    },
    changeSubscriberStatus(subscriberId) {
      const id = this.$store.getters.getId
      AXIOS.post(`/profile/${id}/subscribers/${subscriberId}?subscriberStatus=${true}`)
          .then(response => {
            console.log(response.data)
            window.location.reload()
          })
          .catch(error => {
            this.$emit('errors', error);
          })
    },
    isSubscribed(id) {
      for (let user of this.subscriptions) {
        if (user.id === id) {
          return true
        }
      }

      return false
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
    }
  }
}
</script>

<style scoped>
.text-without-underline {
  text-decoration: none;
}
</style>