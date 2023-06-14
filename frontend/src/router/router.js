import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from "@/pages/auth/LoginPage.vue";
import RegisterComponent from "@/pages/auth/RegisterComponent.vue";
import ConfirmEmail from "@/pages/auth/ConfirmEmail.vue";
import Oauth2Success from "@/pages/auth/Oauth2Success.vue";
import MainComponent from "@/pages/MainComponent.vue";
import ProfileComponent from "@/pages/ProfileComponent.vue";
import UsersComponent from "@/pages/UsersComponent.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'MainComponent', component: MainComponent },
        { path: '/login', name: 'LoginPage', component: LoginPage },
        { path: '/registration', name: 'RegisterComponent', component: RegisterComponent },
        { path: '/confirm-email', name: 'ConfirmEmail', component: ConfirmEmail },
        { path: '/registration/oauth2-success', name: 'Oauth2Success', component: Oauth2Success },
        { path: '/profile', name: 'ProfileComponent', component: ProfileComponent },
        { path: '/users', name: 'UsersComponent', component: UsersComponent },
    ]
})

export default router