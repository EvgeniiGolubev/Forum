<template>
  <div class="container">
    <div class="card my-3 p-3 bg-body rounded shadow-sm">
      <h5 class="border-bottom pb-2 mb-0 text-md-start">Subscribers</h5>
      <div class="d-flex justify-content-between align-items-center pt-3" v-for="subscriber in subscribers" :key="subscriber.id">
        <div class="d-flex align-items-center">
          <img :src="getImagePath(subscriber.userPicture)" class="bd-placeholder-img flex-shrink-0 me-2 rounded" style="width: 50px; height: 50px;">
          <strong class="d-block text-gray-dark">{{ subscriber.name }}</strong>
        </div>
        <div v-if="isProfileOwner">
          <button type="button" class="btn btn-outline-danger mx-2" @click="changeSubscriberStatus(subscriber.id, false)"
                  v-if="isSubscribed(subscriber.id)">
            Unsubscribe
          </button>
          <button type="button" class="btn btn-outline-primary" @click="changeSubscriberStatus(subscriber.id, true)" v-else>
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
  props: ['subscribers', 'isProfileOwner'],
  methods: {
    changeSubscriberStatus(id, status) {
      AXIOS.post(`/profile/subscribers/${id}?subscriberStatus=${status}`)
          .catch(error => {
            this.$emit('errors', error);
          })
    },
    isSubscribed(id) {
      const subscriptions = this.$store.getters.getUserSubscriptions
      return subscriptions.some(subscriber => subscriber.id === id)
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

</style>