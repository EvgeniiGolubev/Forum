<template>
  <div class="container d-flex flex-wrap justify-content-between align-items-center">
    <div class="d-flex justify-content-between align-items-center" v-if="showSearchPanel">
      <div class="input-group me-3">
        <input type="text" class="form-control" placeholder="Search..." v-model="substring">
        <button class="btn btn-outline-primary" type="button" @click="search">
          <font-awesome-icon :icon="['fas', 'magnifying-glass']" />
        </button>
      </div>
      <select class="form-select" style="max-width: 180px" v-model="selectedSortOrder" @change="changeSortOrder">
        <option value="asc">Old ones first</option>
        <option value="desc">New ones first</option>
      </select>
    </div>
    <div v-else></div>
    <div>
      <ul class="pagination">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" @click="changePage(currentPage - 1)">Previous</a>
        </li>

        <li v-for="pageNumber in totalPages" :key="pageNumber" class="page-item" :class="{ active: pageNumber === currentPage }">
          <a class="page-link" @click="changePage(pageNumber)">{{ pageNumber }}</a>
        </li>

        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" @click="changePage(currentPage + 1)">Next</a>
        </li>
        <li class="page-item mx-3">
          <select class="form-select select" v-model="selectedItemsPerPage" @change="changeItemsPerPage">
            <option v-for="option in itemsPerPageOptions" :key="option" :value="option">{{ option }}</option>
          </select>
        </li>
      </ul>
    </div>
  </div>


</template>

<script>
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome"
import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'

library.add(fas, far)

export default {
  components: {FontAwesomeIcon},
  props: ['showSearchPanel', 'currentPage', 'totalPages', 'sortOrder', 'itemsPerPageOptions', 'stringSearch'],
  data() {
    return {
      selectedItemsPerPage: this.itemsPerPageOptions[0],
      selectedSortOrder: 'desc',
      substring: '',
    };
  },
  created() {
    this.selectedSortOrder = this.sortOrder
    this.substring = this.stringSearch
  },
  methods: {
    changePage(pageNumber) {
      if (pageNumber >= 1 && pageNumber <= this.totalPages) {
        this.$emit('page-changed', pageNumber);
      }
    },
    changeSortOrder() {
      this.$emit('sort-order-changed', this.selectedSortOrder);
    },
    changeItemsPerPage() {
      this.$emit('items-per-page-changed', this.selectedItemsPerPage);
    },
    search() {
      this.$emit('substring-search', this.substring);
    }
  }
}
</script>

<style>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.page-item {
  cursor: pointer;
}

.page-item.disabled .page-link,
.page-item.disabled .page-link:hover {
  color: #6c757d;
  pointer-events: none;
  background-color: transparent;
  border-color: #dee2e6;
}

.page-item.active .page-link {
  z-index: 3;
  color: #fff;
  background-color: #007bff;
  border-color: #007bff;
}

.select {
  width: 80px;
}
</style>