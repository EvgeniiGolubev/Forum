<template>
  <div class="alert-holder">
    <div class="alert alert-success alert-dismissible fade show" style="min-height: 60px;" role="alert">
      {{ alertMessage }}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  </div>
</template>

<script>
export default {
  props: ['alertMessage'],
}
</script>

<style>

</style>