<template>
  <errors-view v-bind:errors="errors"/>

  <h4 class="h4 m-4">Articles based on your subscriptions</h4>

  <pagination-component
      v-bind:show-search-panel="true"
      v-bind:currentPage="currentPage"
      v-bind:totalPages="totalPages"
      v-bind:sortOrder="sortOrder"
      v-bind:itemsPerPageOptions="[5, 10, 15, 20]"
      v-bind:string-search="stringSearch"
      @page-changed="handlePageChange"
      @sort-order-changed="handleSortOrderChange"
      @items-per-page-changed="handleItemsPerPageChange"
      @substring-search="substringSearch"
  />

  <articles-list v-bind:articles="articles"/>

  <pagination-component
      v-bind:show-search-panel="false"
      v-bind:currentPage="currentPage"
      v-bind:totalPages="totalPages"
      v-bind:sortOrder="sortOrder"
      v-bind:itemsPerPageOptions="[5, 10, 15, 20]"
      v-bind:string-search="stringSearch"
      @page-changed="handlePageChange"
      @sort-order-changed="handleSortOrderChange"
      @items-per-page-changed="handleItemsPerPageChange"
      @substring-search="substringSearch"
  />
</template>

<script>
import {AXIOS} from "@/http-commons";
import ArticlesList from "@/components/ArticlesList.vue";
import PaginationComponent from "@/components/PaginationComponent.vue";
import ErrorsView from "@/components/ErrorsView.vue";
export default {
  components: {
    ErrorsView,
    ArticlesList,
    PaginationComponent
  },
  data() {
    return {
      articles: [],
      errors: [],
      currentPage: 1,
      totalPages: 1,
      pageSize: 5,
      sortOrder: 'desc',
      stringSearch: '',
    }
  },
  methods: {
    getArticles() {
      const params = {
        stringSearch: this.stringSearch,
        pageSize: this.pageSize,
        page: this.currentPage,
        sortType: this.sortOrder
      }

      AXIOS.get("/articles/activity-feed", {params: params})
          .then(response => {
            this.articles = response.data.content
            this.totalPages = response.data.totalPages
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
              this.errors.push(error.response.data)
            }
          })
    },
    handlePageChange(pageNumber) {
      this.currentPage = pageNumber;
      this.getArticles()
    },
    handleSortOrderChange(sortOrder) {
      this.sortOrder = sortOrder;
      this.currentPage = 1;
      this.getArticles()
    },
    handleItemsPerPageChange(itemsPerPage) {
      this.pageSize = itemsPerPage;
      this.currentPage = 1;
      this.getArticles();
    },
    substringSearch(substring) {
      this.stringSearch = substring
      this.currentPage = 1;
      this.getArticles();
    }
  },
  mounted() {
    this.getArticles()
  },
}
</script>

<style>

</style>
