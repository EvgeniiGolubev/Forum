<template>
  <main class="container">
    <div>
      <article-form v-bind:selected-article="null"  @form-submitted="redirect"/>
    </div>
  </main>
</template>

<script>
import ArticleForm from "@/components/ArticleForm.vue";

export default {
  components: {ArticleForm},
  methods: {
    redirect(articleId) {
      this.$router.push({name: 'ArticlePage', params: {id: articleId}});
    }
  }
}
</script>

<style>

</style>