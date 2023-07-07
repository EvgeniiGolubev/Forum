<template>
  <div class="container">
    <div class="card my-3 p-3 bg-body rounded shadow-sm">
      <h5 class="border-bottom pb-2 mb-0 text-md-start">Subscriptions</h5>
      <div class="d-flex justify-content-between align-items-center pt-3" v-for="subscription in subscriptions" :key="subscription.id">
        <div class="d-flex align-items-center">
          <img :src="getImagePath(subscription.userPicture)" class="bd-placeholder-img flex-shrink-0 me-2 rounded" style="width: 50px; height: 50px;">
          <strong class="d-block text-gray-dark">{{ subscription.name }}</strong>
        </div>
        <div v-if="isProfileOwner">
          <button type="button" class="btn btn-outline-danger mx-2" @click="unsubscribe(subscription.id)">
            Unsubscribe
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  props: ['subscriptions', 'isProfileOwner'],
  methods: {
    unsubscribe(id) {
      AXIOS.post(`/profile/subscriptions/${id}`, {}, {params: {subscriptionStatus: false}})
          .catch(error => {
            this.$emit('errors', error);
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
    }
  }
}
</script>

<style scoped>

</style>