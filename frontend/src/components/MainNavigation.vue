<template>
  <header class="p-3 mb-3 border-bottom">
    <div class="container">
      <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
        <a href="/" class="d-flex align-items-center mb-2 mb-lg-0 link-body-emphasis text-decoration-none">
          <img src="/img/logo.png" class="bi me-2" width="40" height="40"/>
        </a>
        <ul class="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
          <li>
            <router-link class="nav-link px-2 link-secondary" to="/">Home</router-link>
          </li>
          <li>
            <router-link class="nav-link px-2 text-blue" to="/">Main</router-link>
          </li>
          <li>
            <router-link class="nav-link px-2 text-blue" to="/my-profile" v-if="isVisible">Profile</router-link>
          </li>
          <li>
            <router-link class="nav-link px-2 text-blue" to="/create-article" v-if="isVisible">Create new article</router-link>
          </li>
          <li>
            <router-link class="nav-link px-2 text-blue" to="/activity-feed" v-if="isVisible">Activity Feed</router-link>
          </li>
          <li>
            <router-link class="nav-link px-2 text-blue" to="/users" v-if="isAdminOrModerator">Users</router-link>
          </li>
        </ul>
        <div class="d-flex flex-wrap dropdown text-end" v-if="isVisible">
          <div class="name mx-3">
            <span>{{ name }}</span>
          </div>
          <div class="dropdown text-end">
            <a href="#" class="d-block link-body-emphasis text-decoration-none dropdown-toggle" data-bs-toggle="dropdown"
               aria-expanded="false">
              <img :src="userPicture()" alt="user-pic" width="42" height="42" class="rounded-circle"/>
            </a>
            <ul class="dropdown-menu text-small">
              <li>
                <router-link class="dropdown-item" to="/my-profile">Profile</router-link>
              </li>
              <li>
                <router-link class="dropdown-item" to="/create-article">Creat new article</router-link>
              </li>
              <li>
                <router-link class="dropdown-item" to="/activity-feed">Activity Feed</router-link>
              </li>
              <li>
                <hr class="dropdown-divider"/>
              </li>
              <li>
                <a href="/logout" class="dropdown-item" v-on:click="logout">Sign out</a>
              </li>
            </ul>
          </div>
        </div>
        <div class="text-end" v-else>
          <router-link class="btn btn-outline-primary me-2" to="/login">Sign in</router-link>
          <router-link class="btn btn-primary" to="/registration">Sign up</router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  computed: {
    isVisible() {
      return this.$store.getters.isAuthenticated
    },
    isAdminOrModerator() {
      return this.$store.getters.isAdmin || this.$store.getters.isModerator
    },
    name() {
      let name = this.$store.getters.getName
      if (name) {
        return name
      }

      return 'User name'
    },
  },
  methods: {
    logout() {
      this.$store.dispatch('logoutAction')
    },
    userPicture() {
      const picture = this.$store.getters.getPicture

      if (picture !== null) {
        if (picture.startsWith('https://')) {
          return picture
        } else {
          return `/img/${picture}`
        }
      } else {
        return `/img/icon/cat.png`
      }
    }
  }
}
</script>

<style>
.name {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>