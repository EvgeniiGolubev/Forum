<template>
  <errors-view v-bind:errors="errors"/>
  <main class="container">
    <div class="card mb-3">
      <div class="row g-0">
        <div class="col-md-4">
          <div class="container" style="height: 300px; width: 300px; position: relative" @mouseover="showUploadButton = true" @mouseout="showUploadButton = false">
            <div class="spinner-border text-primary avatar-spinner" role="status" v-show="spinnerVisible">
              <span class="visually-hidden">Loading...</span>
            </div>

            <img :src="getImagePath(this.profile.userPicture)" class="img-fluid rounded-start avatar" alt="Profile image">

            <div class="profile-avatar-upload" v-show="showUploadButton">
              <label for="file-input">
                <div class="card profile-avatar-upload-box">
                  <font-awesome-icon :icon="['fas', 'plus']" size="2xl" style="color: #3e6cf4;"/>
                </div>
              </label>
              <input id="file-input" class="form-control" type="file" @change="handleFileChange" style="display: none"/>
            </div>
          </div>
        </div>

        <div class="col-md-8">
          <div class="card-body text-md-start">
            <div class="d-flex">
              <h5 class="card-title" style="margin-right: 10px;">{{ profile.name }}</h5>
              <a data-bs-toggle="collapse" href="#collapseNewName" role="button" aria-expanded="false"
                 aria-controls="collapseNewName" v-if="isProfileOwner">
                <font-awesome-icon :icon="['fas', 'pen']" style="color: #3e6cf4;"/>
              </a>
            </div>
            <div class="collapse" id="collapseNewName">
              <div class="card card-body">
                <form ref="form" @submit="changeProfile" class="needs-validation" novalidate>
                  <div class="d-flex justify-content-between">
                    <div class="form-floating">
                      <input type="text" class="form-control" id="floatingInput" v-model="name" required>
                      <label for="floatingInput">New name</label>
                      <div class="invalid-feedback">Name can not be empty.</div>
                    </div>

                    <button class="btn btn-primary" style="max-height: 60px; width: 100px;">Save</button>
                  </div>
                </form>
              </div>
            </div>

            <p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional
              content. This content is a little bit longer.</p>
            <p class="card-text"><small class="text-body-secondary">Last updated 3 mins ago</small></p>
          </div>
        </div>
      </div>
    </div>
  </main>

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
        <td>
          <button type="button" @click="acceptSubscriber(subscriber.id)">Accept</button>
        </td>
        <td>
          <button type="button" @click="cancelSubscriber(subscriber.id)">Cancel</button>
        </td>
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
        <td>
          <button type="button" @click="unsubscribe(subscription.id)">Unsubscribe</button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ErrorsView from "@/components/ErrorsView.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome"
import {library} from '@fortawesome/fontawesome-svg-core'
import {fas} from '@fortawesome/free-solid-svg-icons'
import {far} from '@fortawesome/free-regular-svg-icons'

library.add(fas, far)

export default {
  components: {ErrorsView, FontAwesomeIcon},
  data() {
    return {
      subscribers: [
        {
          "id": 3,
          "name": "Evgenii",
          "userPicture": "5eb09a98-a02c-493c-a304-36aa40871e97.jpg"
        }
      ],
      subscriptions: [
        {
          "id": 3,
          "name": "Evgenii",
          "userPicture": "5eb09a98-a02c-493c-a304-36aa40871e97.jpg"
        }
      ],
      errors: [],
      name: '',
      image: null,
      profile: {
        "id": 1,
        "name": "Евгений Голубев",
        "userPicture": "https://lh3.googleusercontent.com/a/AAcHTtczi1vwo2wvg2kljQKuFXt6KVP4d-Yoq0XSyoNySA=s96-c"
      },
      oldPassword: '',
      password: '',
      confirmPassword: '',
      newEmail: '',
      spinnerVisible: false,
      isProfileOwner: false,
      showUploadButton: false,
    }
  },
  methods: {
    getSubscribers() {
      AXIOS.get("/profile/subscribers")
          .then(response => {
            this.subscribers = response.data
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    getSubscriptions() {
      AXIOS.get("/profile/subscriptions")
          .then(response => {
            this.subscriptions = response.data
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    acceptSubscriber(id) {
      AXIOS.post(`/profile/subscribers/${id}?subscriberStatus=${true}`)
          .catch(error => {
            this.handleError(error)
          })
    },
    cancelSubscriber(id) {
      AXIOS.post(`/profile/subscribers/${id}?subscriberStatus=${false}`)
          .catch(error => {
            this.handleError(error)
          })
    },
    unsubscribe(id) {
      AXIOS.post(`/profile/subscriptions/${id}?subscriptionStatus=${false}`)
          .catch(error => {
            this.handleError(error)
          })
    },
    getProfileById(profileId) {
      AXIOS.get(`/profile/${profileId}`)
          .then(response => {
            this.profile = response.data
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    changeProfile(event) {
      event.preventDefault()

      const form = this.$refs.form;
      if (!form.checkValidity()) {
        return;
      }

      const profile = {
        name: this.name,
      }

      AXIOS.put(`/profile`, profile)
          .then(response => {
            this.profile = response.data
            this.$store.dispatch('changeNameAction', response.data.name)
          })
          .catch(error => {
            this.handleError(error)
          })

    },
    changeImageProfile() {
      this.spinnerVisible = true

      const formData = new FormData();
      formData.append('image', this.image);

      const headers = {
        'Content-Type': 'multipart/form-data'
      }

      AXIOS.put(`/profile/change-image`, formData, {headers: headers})
          .then(response => {
            this.profile = response.data
            this.spinnerVisible = false
            this.$store.dispatch('changePictureAction', response.data.userPicture)
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    changeEmail() {
      const newEmail = {
        email: this.newEmail,
        password: this.password
      }

      AXIOS.put(`/profile/change-email`, newEmail)
          .then(response => {
            console.log(response)
          })
          .catch(error => {
            this.handleError(error)
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
            console.log(response)
          })
          .catch(error => {
            this.handleError(error)
          })

      this.oldPassword = ''
      this.password = ''
      this.confirmPassword = ''
    },
    subscribe(id) {
      AXIOS.post(`/profile/subscriptions/${id}`, {}, {params: {subscriptionStatus: true}})
          .catch(error => {
            this.handleError(error)
          })
    },
    handleFileChange(event) {
      const maxFileSize = 5 * 1024 * 1024; // Максимальный размер файла (5 МБ)

      for (let file of event.target.files) {
        if (file.size > maxFileSize) {
          this.errors.push({ message: 'The maximum photo size has been exceeded!' })
          return;
        } else {
          this.image = file;
          this.profile.userPicture = file
          this.changeImageProfile()
        }
      }
    },
    getImagePath(image) {
      if (!image) return '/img/icon/paw.png'

      if (typeof image === 'string') {
        if (image.startsWith('https://')) {
          return image
        } else {
          return `/img/${image}`
        }
      } else {
        return URL.createObjectURL(image)
      }
    },
    handleError(error) {
      this.spinnerVisible = false

      if (error) {
        if (!Array.isArray(error.response.data)) {
          this.errors.push(error.response.data)
        }
      }
    },
  },
  mounted() {
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
      form.addEventListener('submit', event => {
        if (!form.checkValidity()) {
          event.preventDefault();
          event.stopPropagation();
        }

        form.classList.add('was-validated');
      }, false);
    });

    let profileId = this.$route.params.id

    if (profileId) {
      this.getProfileById(profileId)
      this.isProfileOwner = false
    } else {
      profileId = this.$store.getters.getId
      this.getProfileById(profileId)
      this.isProfileOwner = true
    }

    this.getSubscribers()
    this.getSubscriptions()
  },
}
</script>

<style scoped>
.avatar{
  object-fit: contain;
  height: 300px;
}

.profile-avatar-upload {
  position: absolute;
  top: 0;
  left: 0;
  width: 300px;
  height: 300px;
}

.profile-avatar-upload-box {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.15);
}

.avatar-spinner {
  position: absolute;
  right: 110px;
  bottom: 110px;
  width: 80px;
  height: 80px;
}
</style>