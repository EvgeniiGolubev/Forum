<template>
  <main-navigation/>
  <router-view></router-view>
</template>

<script>
import MainNavigation from '@/components/MainNavigation.vue'
export default {
  name: 'App',
  components: {
    MainNavigation
  }
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
