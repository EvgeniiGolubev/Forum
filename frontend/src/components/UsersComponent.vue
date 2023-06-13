<template>

  <div>
    Errors:
    <ul>
      <li v-for="(error, index) in errors" :key="index">{{ error.message }}</li>
    </ul>
  </div>

  <h3>Filter users</h3>
  <div>
    <input type="number" v-model="userId" placeholder="User id"/>
    <input type="text" v-model="userEmail" placeholder="User email"/>
    <input type="text" v-model="userName" placeholder="User name"/>

    <label for="status-select">User status:</label>
    <select id="status-select" v-model="userStatus">
      <option value="ACTIVE">ACTIVE</option>
      <option value="BANNED">BANNED</option>
    </select>

    <label for="role-select">User role:</label>
    <select id="role-select" v-model="userRole">
      <option value="ADMIN">ADMIN</option>
      <option value="MODERATOR">MODERATOR</option>
      <option value="USER">USER</option>
    </select>

    <input type="text" v-model="userLocale" placeholder="User locale"/>

    <label for="provider-select">User provider:</label>
    <select id="provider-select" v-model="userProvider">
      <option value="LOCAL">LOCAL</option>
      <option value="GOOGLE">GOOGLE</option>
    </select>

    <button type="button" @click="searchUsers">Submit</button>
  </div>

  <br>

  <h3>Change pagination</h3>
  <div>
    <input type="number" v-model="currentPage" placeholder="Current page"/>
    <input type="number" v-model="pageSize" placeholder="Page size"/>
    <label for="sort-select">Выберите сортировку:</label>
    <select id="sort-select" v-model="sortType">
      <option value="asc">По возрастанию</option>
      <option value="desc">По убыванию</option>
    </select>
    <button type="button" @click="handlePageChange">Submit</button>
  </div>

  <br>

  <h3>Update user</h3>
  <div>
    <input type="text" v-model="userEmail" placeholder="User email"/>
    <input type="text" v-model="userName" placeholder="User name"/>
    <button type="button" @click="update">Update</button>
  </div>

  <br>

  <h2>Users:</h2>
  <table>
    <thead>
    <tr>
      <th>Id</th>
      <th>Email</th>
      <th>Name</th>
      <th>Status</th>
      <th>Roles</th>
      <th>Locale</th>
      <th>Provider</th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
      <th></th>
    </tr>
    </thead>
    <tbody>
      <tr v-for="(user, index) in users" :key="index">
        <td>{{ user.id }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.name }}</td>
        <td>{{ user.status }}</td>
        <td>
          <div v-for="(role, index) in user.roles" :key="index">
            {{ role }}
          </div>
        </td>
        <td>{{ user.locale }}</td>
        <td>{{ user.provider }}</td>
        <td>
          <button type="button" @click="updateUser(user)">Edit</button>
        </td>
        <td>
          <button type="button" @click="deleteUser(user.id)">Delete</button>
        </td>
        <td>
          <button type="button" @click="changePassword(user.id)">Change user password</button>
        </td>
        <td>
          <label for="status-select">User status:</label>
          <select id="status-select" v-model="userStatus">
            <option value="ACTIVE">ACTIVE</option>
            <option value="BANNED">BANNED</option>
          </select>
          <button type="button" @click="changeStatus(user.id)">Change user status</button>
        </td>
        <td>
          <button type="button" @click="makeModerator(user.id, user.roles)">Make user moderator</button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  data() {
    return {
      users: [],
      errors: [],
      userId: '',
      userEmail: '',
      userName: '',
      userStatus: '',
      userRole: '',
      userLocale: '',
      userProvider: '',
      currentPage: 0,
      pageSize: 5,
      sortType: 'desc',
      selectedUser: null,
    }
  },
  watch: {
    selectedUser(updateUser) {
      this.userId = updateUser.id
      this.userEmail = updateUser.email
      this.userName = updateUser.name
    }
  },
  methods: {
    findUsers() {
      AXIOS.get(`/users`, {
        params: {
          id: this.userId,
          email: this.userEmail,
          name: this.userName,
          status: this.userStatus,
          role: this.userRole,
          locale: this.userLocale,
          provider: this.userProvider,
          pageSize: this.pageSize,
          page: this.currentPage,
          sortType: this.sortType
        }})
          .then(response => {
            this.users = response.data.content
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    update() {
      let user = {
        email: this.userEmail,
        name: this.userName
      }

      AXIOS.put(`/users/${this.userId}`, user)
          .then(response => {
            let index = this.users.findIndex(item => item.id === response.data.id)
            this.users.splice(index, 1, response.data)
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })

      this.userEmail = ''
      this.userName = ''
    },
    deleteUser(id) {
      AXIOS.delete(`/users/${id}`)
          .then(response => {
            alert(response.data.message)
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    changePassword(id) {
      AXIOS.put(`/users/change-password/${id}`)
          .then(response => {
            alert(response.data.message)
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    updateUser(user) {
      this.selectedUser = user
    },
    changeStatus(id) {
      let isBanned = false
      if(this.userStatus === 'BANNED') {
        isBanned = true
      }

      AXIOS.put(`/users/change-status/${id}`, null, {params: { isBanned: isBanned }})
          .then(response => {
            alert(response.data.message)
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    makeModerator(id, roles) {
      let isModer = true
      if (roles.includes('MODERATOR')) {
        isModer = false
      }

      AXIOS.put(`/users/change-moderator-role/${id}`, null,{params: { isModer: isModer }})
          .then(response => {
            alert(response.data.message)
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    searchUsers() {
      this.findUsers()
    },
    handlePageChange() {
      this.findUsers()
    }
  },
  mounted() {
    this.findUsers()
  }
}
</script>

<style scoped>

</style>