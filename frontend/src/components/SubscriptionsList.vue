<template>
  <div class="container">
    <div class="card my-3 p-3 bg-body rounded shadow-sm">
      <h5 class="border-bottom pb-2 mb-0 text-md-start">Subscriptions</h5>
      <div class="d-flex justify-content-between align-items-center pt-3" v-for="subscription in subscriptions" :key="subscription.id">
        <div class="d-flex align-items-center">
          <img :src="getImagePath(subscription.userPicture)" class="bd-placeholder-img flex-shrink-0 me-2 rounded" style="width: 50px; height: 50px;">
          <a href="#" class="btn btn-link text-without-underline" v-on:click="openProfile(subscription.id)">{{ subscription.name }}</a>
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
    openProfile(id) {
      this.$router.push({name: 'UserProfile', params: {id: id}});
    },
    unsubscribe(subscriptionId) {
      const id = this.$store.getters.getId
      AXIOS.post(`/profile/${id}/subscriptions/${subscriptionId}`, {}, {params: {subscriptionStatus: false}})
          .then(response => {
            console.log(response.data)
            window.location.reload()
          })
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