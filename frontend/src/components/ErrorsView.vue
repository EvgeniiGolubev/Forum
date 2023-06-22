<template>
  <div class="alert-holder">
    <div class="alert alert-danger alert-dismissible fade show" style="min-height: 60px;" role="alert" v-for="(error, index) in errors" :key="index">
      {{ error.message }}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </div>
</template>

<script>
export default {
  props: ['errors'],
}
</script>

<style>

</style>