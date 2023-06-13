<template>
  <a href="/logout" style="margin: 5px">Logout</a>
  <router-link to="/login" style="margin: 5px">Login</router-link>
  <router-link to="/" style="margin: 5px">Main</router-link>
  <router-link to="/registration" style="margin: 5px">Registration</router-link>
  <router-link to="/profile" style="margin: 5px">Profile</router-link>
  <router-link to="/users" style="margin: 5px">Users</router-link>
  <router-view></router-view>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
