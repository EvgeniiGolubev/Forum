<template>
  <errors-view v-bind:errors="errors"/>

  <main class="container">
    <div class="d-flex justify-content-between align-items-center my-2" role="group">
      <button type="button" class="btn btn-outline-primary mx-2" @click="unsubscribe" v-if="isSubscribed">Unsubscribe</button>
      <button type="button" class="btn btn-outline-primary" @click="subscribe" v-else>Subscribe</button>
    </div>

    <div class="card mb-3" v-if="profile">
      <div class="row g-0">
        <div class="col-md-4" style="min-width: 300px">
          <div class="container mt-1 mb-1 ms-1" style="height: 300px; width: 300px; position: relative;">
            <img :src="getImagePath(profile.userPicture)" class="img-fluid rounded-start avatar"
                 alt="Profile image">
          </div>
        </div>

        <div class="col-md-8">
          <div class="card-body text-md-start">
            <div class="d-flex">
              <p>Name:</p>
              <h5 class="card-title me-3">{{ profile.name }}</h5>
            </div>

            <div class="d-flex me-3">
              <p>Description:</p>
              <p class="card-text me-2 mt-3">{{ profile.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <user-profile-activity v-bind:user-profile="profile" v-bind:is-profile-owner="false" @errors="handleError" v-if="profile"/>
  </main>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ErrorsView from "@/components/ErrorsView.vue";
import UserProfileActivity from "@/components/UserProfileActivity.vue";

export default {
  components: {UserProfileActivity, ErrorsView},
  data() {
    return {
      errors: [],
      profileId: -1,
      id: -1,
      profile: null,
      isSubscribed: false,
    }
  },
  methods: {
    getProfileById() {
      AXIOS.get(`/profile/${this.profileId}`)
          .then(response => {
            this.profile = response.data
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    checkSubscriptions() {
      AXIOS.get(`/profile/${this.id}/subscriptions`)
          .then(response => {
            for (let user of response.data) {
              if (user.id === this.profileId) {
                this.isSubscribed = true
                break;
              }
            }
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    subscribe() {
        AXIOS.post(`/profile/${this.id}/subscriptions/${this.profileId}`, {}, {params: {subscriptionStatus: true}})
            .then(response => {
              console.log(response)
              window.location.reload()
            })
            .catch(error => {
              this.handleError(error)
            })

    },
    unsubscribe() {
        AXIOS.post(`/profile/${this.id}/subscriptions/${this.profileId}`, {}, {params: {subscriptionStatus: false}})
            .then(response => {
              console.log(response)
              window.location.reload()
            })
            .catch(error => {
              this.handleError(error)
            })

    },
    getImagePath(image) {
      if (!image) return '/img/icon/paw.png'

      if (typeof image === 'string') {
        if (image.startsWith('https://')) {
          return image
        } else {
          return `/img/${image}`
        }
      } else {
        return URL.createObjectURL(image)
      }
    },
    handleError(error) {
      if (error) {
        if (!Array.isArray(error.response.data)) {
          this.errors.push(error.response.data)
        }
      }
    },
  },
  mounted() {
    this.profileId = Number(this.$route.params.id)
    this.id = this.$store.getters.getId

    if (this.profileId) {
      if (this.profileId === this.id) {
        this.$router.push('/my-profile')
      } else {
        this.getProfileById()
        this.checkSubscriptions()
      }
    } else {
      this.$router.push('/my-profile')
    }
  },
}
</script>

<style scoped>
.avatar {
  object-fit: contain;
  height: 300px;
}
</style>