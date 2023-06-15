<template>
  <div class="alert-holder">
    <div class="alert alert-danger" role="alert" v-for="(error, index) in errors" :key="index" v-bind:class="{ 'show': errors.length }">
      {{ error.message }}
    </div>
  </div>
  <pagination-component
      v-bind:currentPage="currentPage"
      v-bind:totalPages="totalPages"
      v-bind:sortOrder="sortOrder"
      v-bind:itemsPerPageOptions="[5, 10, 15, 20]"
      @page-changed="handlePageChange"
      @sort-order-changed="handleSortOrderChange"
      @items-per-page-changed="handleItemsPerPageChange"
  />

  <articles-list v-bind:articles="articles"/>

  <pagination-component
      v-bind:currentPage="currentPage"
      v-bind:totalPages="totalPages"
      v-bind:sortOrder="sortOrder"
      v-bind:itemsPerPageOptions="[5, 10, 15, 20]"
      @page-changed="handlePageChange"
      @sort-order-changed="handleSortOrderChange"
      @items-per-page-changed="handleItemsPerPageChange"
  />
</template>

<script>
import {AXIOS} from "@/http-commons";
import ArticlesList from "@/components/ArticlesList.vue";
import PaginationComponent from "@/components/PaginationComponent.vue";
export default {
  components: {
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
      sortOrder: 'desc'
    }
  },
  methods: {
    getArticles() {
      const params = {
        pageSize: this.pageSize,
        page: this.currentPage,
        sortType: this.sortOrder
      }

      AXIOS.get("/articles", {params: params})
          .then(response => {
            this.articles = response.data.content
            this.totalPages = response.data.totalPages
          })
          .catch(error => {
            if (!Array.isArray(error.response.data)) {
              this.errors.push(error.response.data)
            }
            setTimeout(() => {
              this.errors = [];
            }, 5000)
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
    }

  },
  mounted() {
    this.getArticles()
  },
}
</script>

<style>
.alert-holder {
  max-width: 800px;
  margin: 0 auto;
}

.alert {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.alert.show {
  opacity: 1;
}
</style>
