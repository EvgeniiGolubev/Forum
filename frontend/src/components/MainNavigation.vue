<template>
  <header class="p-3 mb-3 border-bottom">
    <div class="container">
      <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
        <a href="/" class="d-flex align-items-center mb-2 mb-lg-0 link-body-emphasis text-decoration-none">
          <img src="/logo.png" class="bi me-2" width="40" height="40"/>
        </a>
        <ul class="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
          <li>
            <a href="/" class="nav-link px-2 link-secondary">Home</a>
          </li>
          <li>
            <a href="/" class="nav-link px-2 text-blue">Main</a>
          </li>
          <li>
            <a href="/profile" class="nav-link px-2 text-blue">Profile</a>
          </li>
          <li>
            <a href="/post-article" class="nav-link px-2 text-blue">Post article</a>
          </li>
          <li>
            <!--            HERE-->
            <router-link class="nav-link px-2 link-body-emphasis" to="/users" v-if="isVisible">Users</router-link>
          </li>
        </ul>
        <form class="col-12 col-lg-auto mb-3 mb-lg-0 me-lg-3" role="search">
          <input type="search" class="col-12 col-lg-auto mb-3 mb-lg-0 me-lg-3" placeholder="Search..." aria-label="Search"/>
        </form>
        <div class="dropdown text-end" v-if="isVisible">
          <a href="#" class="d-block link-dark text-decoration-none dropdown-toggle" @click="toggleDropdown">
            <img src="https://github.com/mdo.png" alt="user-pic" width="32" height="32" class="rounded-circle"/>
          </a>
          <ul class="dropdown-menu text-small show" data-popper-placement="bottom-start" v-show="showDropdown"
              style="position: absolute; inset: 0px auto auto 0px; margin: 0px; transform: translate(-62px, 34px);">
            <li>
              <a href="#" class="dropdown-item">Create new article</a>
            </li>
            <li>
              <a href="#" class="dropdown-item">Profile</a>
            </li>
            <li>
              <a href="#" class="dropdown-item">Some things</a>
            </li>
            <li>
              <hr class="dropdown-divider"/>
            </li>
            <li>
              <a href="/logout" class="dropdown-item" v-on:click="logout">Sign out</a>
            </li>
          </ul>
        </div>
        <div class="text-end" v-else>
          <router-link class="btn btn-outline-primary me-2" to="/login">Sign in</router-link>
          <router-link class="btn btn-primary" to="/register">Sign up</router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script>

</script>

<style>

</style>