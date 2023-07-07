<template>
  <errors-view v-bind:errors="errors"/>

  <main class="container">
    <div class="d-flex justify-content-between align-items-center my-2" role="group" v-if="!isProfileOwner">
      <button type="button" class="btn btn-outline-primary mx-2" @click="unsubscribe()" v-if="isSubscribed">Unsubscribe</button>
      <button type="button" class="btn btn-outline-primary" @click="subscribe" v-else>Subscribe</button>
    </div>

    <div class="card mb-3">
      <div class="row g-0">
        <div class="col-md-4" style="min-width: 300px">
          <div class="container mt-1 mb-1 ms-1" style="height: 300px; width: 300px; position: relative;"
               @mouseover="showUploadButton = true" @mouseout="showUploadButton = false">
            <div class="spinner-border text-primary avatar-spinner" role="status" v-show="spinnerVisible">
              <span class="visually-hidden">Loading...</span>
            </div>

            <img :src="getImagePath(profile.userPicture)" class="img-fluid rounded-start avatar"
                 alt="Profile image">

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
            <div class="collapse m-2" id="collapseNewName">
              <div class="card card-body">
                <form ref="form" @submit="changeProfile" class="needs-validation" novalidate>
                  <div class="form-floating">
                    <input type="text" class="form-control" id="newNameInput" v-model="profile.name" maxlength="255" required>
                    <label for="newNameInput">New name</label>
                    <div class="invalid-feedback">Name can not be empty.</div>
                  </div>

                  <button class="btn btn-primary mt-3">Save</button>
                </form>
              </div>
            </div>

            <div class="d-flex align-items-center me-3">
              <p class="card-text me-2 mt-3">{{ profile.description }}</p>
              <a data-bs-toggle="collapse" href="#collapseNewDescription" role="button" aria-expanded="false"
                 aria-controls="collapseNewDescription" v-if="isProfileOwner">
                <font-awesome-icon :icon="['fas', 'pen']" style="color: #3e6cf4; margin: 0"/>
              </a>
            </div>
            <div class="collapse m-2" id="collapseNewDescription">
              <div class="card card-body">
                <form ref="form" @submit="changeProfile" class="needs-validation" novalidate>
                  <div class="form-floating">
                    <textarea type="text" class="form-control" id="newDescriptionInput" rows="2" v-model="profile.description"
                              maxlength="600" required/>
                    <label for="newDescriptionInput">New description</label>
                    <div class="invalid-feedback">Description can not be empty.</div>
                  </div>

                  <button class="btn btn-primary mt-3">Save</button>
                </form>
              </div>
            </div>

            <div v-if="isProfileOwner" style="padding-left: 8px">
              <div class="row">
                <button type="button" style="width: 200px;" class="btn btn-outline-primary m-1"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapseNewEmail" aria-expanded="false" aria-controls="collapseNewEmail">
                  Change email
                </button>
                <div class="collapse m-2" id="collapseNewEmail">
                  <div class="card card-body">
                    <p>A confirmation email will be sent to the address provided. Until confirmation, you will not be
                      able to enter the site under your account!</p>
                    <form ref="form" @submit="changeEmail" class="needs-validation" novalidate>
                      <div class="form-floating">
                        <input type="email" class="form-control" id="floatingInput" placeholder="name@example.com"
                               v-model="newEmail" required>
                        <label for="floatingInput">New email address</label>
                        <div class="invalid-feedback">Please provide a valid email address.</div>
                      </div>
                      <div class="form-floating mt-3">
                        <input type="password" class="form-control" id="floatingPassword" placeholder="Password"
                               v-model="password" required minlength="1">
                        <label for="floatingPassword">Password</label>
                        <div class="invalid-feedback">Password must be at least 8 characters long.</div>
                      </div>
                      <button class="btn btn-primary mt-3">Change email</button>
                    </form>
                  </div>
                </div>
              </div>

              <div class="row">
                <button type="button" style="width: 200px;" class="btn btn-outline-primary m-1"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapseNewPassword" aria-expanded="false" aria-controls="collapseNewPassword">
                  Change password
                </button>
                <div class="collapse m-2" id="collapseNewPassword">
                  <div class="card card-body">
                    <form ref="form" @submit="changePassword" class="needs-validation" novalidate>
                      <div class="form-floating mt-3">
                        <input type="password" class="form-control" id="old-password" placeholder="Password"
                               v-model="oldPassword" required minlength="1">
                        <label for="password">Old password</label>
                        <div class="invalid-feedback">Password must be at least 8 characters long.</div>
                      </div>
                      <div class="form-floating mt-3">
                        <input type="password" class="form-control" id="password" placeholder="Password"
                               v-model="password" required minlength="1">
                        <label for="password">New password</label>
                        <div class="invalid-feedback">Password must be at least 8 characters long.</div>
                      </div>
                      <div class="form-floating mt-3">
                        <input type="password" class="form-control" id="confirm-password" placeholder="Confirm password"
                               v-model="confirmPassword" required :pattern="password">
                        <label for="confirm-password">Confirm new password</label>
                        <div class="invalid-feedback">Passwords must match.</div>
                      </div>
                      <button class="btn btn-primary mt-3">Change password</button>
                    </form>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>

    <user-profile-activity v-bind:user-profile="profile" v-bind:is-profile-owner="isProfileOwner" @errors="handleError"/>
  </main>
</template>

<script>
import {AXIOS} from "@/http-commons";
import ErrorsView from "@/components/ErrorsView.vue";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome"
import {library} from '@fortawesome/fontawesome-svg-core'
import {fas} from '@fortawesome/free-solid-svg-icons'
import {far} from '@fortawesome/free-regular-svg-icons'
import UserProfileActivity from "@/components/UserProfileActivity.vue";

library.add(fas, far)

export default {
  components: {UserProfileActivity, ErrorsView, FontAwesomeIcon},
  data() {
    return {
      errors: [],
      profile: null,
      oldPassword: '',
      password: '',
      confirmPassword: '',
      newEmail: '',
      spinnerVisible: false,
      isProfileOwner: false,
      showUploadButton: false,
      isSubscribed: false,
    }
  },
  methods: {
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

      const updatedProfile = {
        name: this.profile.name,
        description: this.profile.description
      }

      AXIOS.put(`/profile/${this.profile.id}`, updatedProfile)
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
      formData.append('image', this.profile.userPicture);

      const headers = {
        'Content-Type': 'multipart/form-data'
      }

      AXIOS.put(`/profile/${this.profile.id}/update-image`, formData, {headers: headers})
          .then(response => {
            this.profile = response.data
            this.spinnerVisible = false
            this.$store.dispatch('changePictureAction', response.data.userPicture)
          })
          .catch(error => {
            this.handleError(error)
          })
    },
    changeEmail(event) {
      event.preventDefault()

      const form = this.$refs.form;
      if (!form.checkValidity()) {
        return;
      }

      const newEmail = {
        email: this.newEmail,
        password: this.password
      }

      AXIOS.put(`/profile/${this.profile.id}/change-email`, newEmail)
          .then(response => {
            console.log(response)
          })
          .catch(error => {
            this.handleError(error)
          })

      this.newEmail = ''
      this.password = ''
    },
    changePassword(event) {
      event.preventDefault()

      const form = this.$refs.form;
      if (!form.checkValidity()) {
        return;
      }

      const newPassword = {
        oldPassword: this.oldPassword,
        newPassword: this.password,
        confirmPassword: this.confirmPassword
      }
      AXIOS.put(`/profile/${this.profile.id}/change-password`, newPassword)
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
    subscribe() {
      AXIOS.post(`/profile/${this.profile.id}/subscriptions`, {}, {params: {subscriptionStatus: true}})
          .catch(error => {
            this.handleError(error)
          })
    },
    unsubscribe() {
      AXIOS.post(`/profile/${this.profile.id}/subscriptions`, {}, {params: {subscriptionStatus: false}})
          .catch(error => {
            this.handleError(error)
          })
    },
    handleFileChange(event) {
      const maxFileSize = 5 * 1024 * 1024; // Максимальный размер файла (5 МБ)

      for (let file of event.target.files) {
        if (file.size > maxFileSize) {
          this.errors.push({message: 'The maximum photo size has been exceeded!'})
          return;
        } else {
          this.profile.userPicture = file;
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

      if (profileId === this.$store.getters.getId) {
        this.isProfileOwner = true
      } else {
        this.isProfileOwner = false

        const subscriptions = this.$store.getters.getUserSubscriptions
        const id = subscriptions.find(user => user.id === profileId)
        this.isSubscribed = !!id;
      }
    } else {
      profileId = this.$store.getters.getId
      this.getProfileById(profileId)
      this.isProfileOwner = true
    }
  },
}
</script>

<style scoped>
.avatar {
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