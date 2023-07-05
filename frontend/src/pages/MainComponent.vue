<template>
  <errors-view v-bind:errors="errors"/>

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
      articles: [
        {
          "id": 18,
          "title": "Now",
          "content": "dsfsdfsdf",
          "author": {
            "id": 1,
            "name": "Евгений Голубев",
            "userPicture": "https://lh3.googleusercontent.com/a/AAcHTtczi1vwo2wvg2kljQKuFXt6KVP4d-Yoq0XSyoNySA=s96-c"
          },
          "creationDate": "2023-07-01 01:08:25",
          "imageLinks": []
        },
        {
          "id": 17,
          "title": "aSZXDCFVGB",
          "content": "HGFDSA",
          "author": {
            "id": 1,
            "name": "Евгений Голубев",
            "userPicture": "https://lh3.googleusercontent.com/a/AAcHTtczi1vwo2wvg2kljQKuFXt6KVP4d-Yoq0XSyoNySA=s96-c"
          },
          "creationDate": "2023-06-30 23:23:12",
          "imageLinks": [
            "bc37dd7b-f9ec-4a23-a091-617722d229ff.jpg"
          ]
        },
        {
          "id": 16,
          "title": "ывапр",
          "content": "лорпа",
          "author": {
            "id": 1,
            "name": "Евгений Голубев",
            "userPicture": "https://lh3.googleusercontent.com/a/AAcHTtczi1vwo2wvg2kljQKuFXt6KVP4d-Yoq0XSyoNySA=s96-c"
          },
          "creationDate": "2023-06-29 22:39:28",
          "imageLinks": []
        },
        {
          "id": 15,
          "title": "asdfghjk",
          "content": "';lkjhgfds",
          "author": {
            "id": 1,
            "name": "Евгений Голубев",
            "userPicture": "https://lh3.googleusercontent.com/a/AAcHTtczi1vwo2wvg2kljQKuFXt6KVP4d-Yoq0XSyoNySA=s96-c"
          },
          "creationDate": "2023-06-29 01:00:28",
          "imageLinks": [
            "31d1dad5-c544-4d71-8190-7f01e9d19eb3.jpg"
          ]
        },
        {
          "id": 8,
          "title": "Woozle – головоломки",
          "content": "О компании\r\n\r\nНаша компания занимается разработкой и производством деревянных головоломок в городе Санкт-Петербурге.\r\nМы работаем с 2019 года, за это время нами было произведено более 10 тысяч головоломок и других изделий смежной тематики.\r\nВсе продукты разрабатываются нашими конструкторами и полностью производятся силами нашей компании.\r\n\r\nМы готовы предложить вам уникальное решение для интересного и запоминающегося корпоративного подарка - головоломки.\r\nНаш широкий ассортимент головоломок специально подобран, чтобы охватить различные события и праздники, такие как новый год, 23\r\nфевраля, дни рождения сотрудников, инженерные праздники и подарки для корпоративных клиентов.\r\n\r\nМы предлагаем уникальный корпоративный подарок с возможностью брендирования и выбором цвета, чтобы полностью соответствовать запросам заказчика. Наша головоломка не только является увлекательным развлечением, но также может использоваться в качестве упаковки для более ценного подарка, который помещается в ее внутренние отделения.\r\n\r\nМы также предлагаем возможность изготовления на заказ деревянных упаковок для подарков, которые могут быть специально разработаны под ваши требования. Наша команда способна создать корпоративные подарки с разнообразным наполнением, таким как напитки, сладости или сувенирная продукция.",
          "author": {
            "id": 3,
            "name": "Evgenii",
            "userPicture": "5eb09a98-a02c-493c-a304-36aa40871e97.jpg"
          },
          "creationDate": "2023-06-12 16:04:43",
          "imageLinks": [
            "ab63facb-5fa9-4abf-8b85-8a64bcc46aef.png",
            "824b2d2e-a67d-4193-8216-40d015c1fea7.png",
            "377164d4-8faf-4f31-8c57-9ead5f1bd78f.png",
            "52945c5d-ea59-466c-a3b8-aea9666ff785.png"
          ]
        }
      ],
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

      AXIOS.get("/articles", {params: params})
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
