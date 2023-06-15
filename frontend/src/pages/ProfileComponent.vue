<template>
  <div>
    Response message:
    {{ responseMessages }}
  </div>

  <div>
    Errors:
    <ul>
      <li v-for="(error, index) in errors" :key="index">{{ error.message }}</li>
    </ul>
  </div>

  <div>
    <h3>User profile</h3>
    <p>Profile id: {{ profile.id }}</p>
    <p>profile name: {{ profile.name }}</p>
    <img :src="getImagePath(this.profile.userPicture)" alt="Profile image">
  </div>

  <div>
    <h3>Change user name and picture</h3>
    <input type="text" v-model="name" placeholder="name"/>
    <input type="file" @change="handleFileChange">
    <input type="button" value="Change" @click="changeProfile"/>
  </div>

  <div>
    <h3>Change user email</h3>
    <input type="text" v-model="newEmail" placeholder="New email"/>
    <input type="text" v-model="password" placeholder="New password"/>
    <input type="button" value="Change" @click="changeEmail"/>
  </div>

  <div>
    <h3>Change user password</h3>
    <input type="text" v-model="oldPassword" placeholder="Old password"/>
    <input type="text" v-model="password" placeholder="New password"/>
    <input type="password" v-model="confirmPassword" placeholder="Confirm password"/>
    <input type="button" value="Change" @click="changePassword"/>
  </div>

  <div>
    <h1>Subscribers</h1>
    <table>
      <thead>
      <tr>
        <th>Id</th>
        <th>Name</th>
        <th></th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(subscriber, index) in subscribers " :key="index">
        <td>{{ subscriber.id }}</td>
        <td>{{ subscriber.name }}</td>
        <td><button type="button" @click="acceptSubscriber(subscriber.id)">Accept</button></td>
        <td><button type="button" @click="cancelSubscriber(subscriber.id)">Cancel</button></td>
      </tr>
      </tbody>
    </table>
  </div>
  <br>
  <div>
    <h1>Subscriptions</h1>
    <table>
      <thead>
      <tr>
        <th>Id</th>
        <th>Name</th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(subscription, index) in subscriptions" :key="index">
        <td>{{ subscription.id }}</td>
        <td>{{ subscription.name }}</td>
        <td><button type="button" @click="unsubscribe(subscription.id)">Unsubscribe</button></td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";

export default {
  data() {
    return {
      subscribers: [],
      subscriptions: [],
      responseMessages: '',
      errors: [],
      name: '',
      image: null,
      profile: {id: 0, name: "user"},
      oldPassword: '',
      password: '',
      confirmPassword: '',
      newEmail: '',
    }
  },
  methods: {
    getSubscribers() {
      AXIOS.get("/profile/subscribers")
          .then(response => {
            this.subscribers = response.data
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    getSubscriptions() {
      AXIOS.get("/profile/subscriptions")
          .then(response => {
            this.subscriptions = response.data
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    acceptSubscriber(id) {
      AXIOS.post(`/profile/subscribers/${id}?subscriberStatus=${true}`)
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    cancelSubscriber(id) {
      AXIOS.post(`/profile/subscribers/${id}?subscriberStatus=${false}`)
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    unsubscribe(id) {
      AXIOS.post(`/profile/subscriptions/${id}?subscriptionStatus=${false}`)
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    getProfile() {
      AXIOS.get(`/profile`)
          .then(response => {
            this.profile = response.data
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })
    },
    getProfileById(profileId) {
      console.log(profileId)
    },
    changeProfile() {
      const formData = new FormData();
      formData.append('name', this.name);
      formData.append('image', this.image);

      const headers = {
        'Content-Type': 'multipart/form-data'
      }

      AXIOS.put(`/profile`, formData, { headers: headers })
          .then(response => {
            this.profile = response.data
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })

    },
    changeEmail() {
      const newEmail = {
        email: this.newEmail,
        password: this.password
      }

      AXIOS.put(`/profile/change-email`, newEmail)
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })

      this.newEmail = ''
      this.password = ''
    },
    changePassword() {
      const newPassword = {
        oldPassword: this.oldPassword,
        newPassword: this.password,
        confirmPassword: this.confirmPassword
      }
      AXIOS.put(`/profile/change-password`, newPassword)
          .then(response => {
            this.responseMessages = response.data.message
          })
          .catch(error => {
            if (Array.isArray(error.response.data)) {
              this.errors = error.response.data
            } else {
              this.errors.push(error.response.data)
            }
          })

      this.oldPassword = ''
      this.password = ''
      this.confirmPassword = ''
    },
    handleFileChange(event) {
      // Обработчик события изменения файла
      this.image = event.target.files[0];
    },
    getImagePath(link) {
      if (link) {
        if (link.startsWith('https://')) {
          return link
        } else {
          return `/img/${link}`
        }
      }

      return '/img/placeholder.jpg';
    },
  },
  mounted() {
    const profileId = this.$route.query.id;
    this.getProfileById(profileId);

    this.getSubscribers()
    this.getSubscriptions()
    this.getProfile()
  },
}
</script>

<style scoped>

</style>