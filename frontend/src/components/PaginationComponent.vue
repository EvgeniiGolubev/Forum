<template>
  <nav>
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
    <div class="btn-group" role="group">
      <button type="button" class="btn btn-outline-primary" @click="changeSortOrder('asc')">New ones first</button>
      <button type="button" class="btn btn-outline-primary" @click="changeSortOrder('desc')">Old ones first</button>
    </div>
  </nav>
</template>

<script>
export default {
  props: ['currentPage', 'totalPages', 'sortOrder', 'itemsPerPageOptions'],
  data() {
    return {
      selectedItemsPerPage: this.itemsPerPageOptions[0] // Изначально выбирается первое значение из списка
    };
  },
  methods: {
    changePage(pageNumber) {
      if (pageNumber >= 1 && pageNumber <= this.totalPages) {
        this.$emit('page-changed', pageNumber);
      }
    },
    changeSortOrder(sortOrder) {
      this.$emit('sort-order-changed', sortOrder);
    },
    changeItemsPerPage() {
      this.$emit('items-per-page-changed', this.selectedItemsPerPage);
    }
  }
}
</script>

<style>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
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