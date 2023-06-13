import { createRouter, createWebHistory } from 'vue-router'
import LoginComponent from "@/components/auth/LoginComponent.vue";
import RegisterComponent from "@/components/auth/RegisterComponent.vue";
import ConfirmEmail from "@/components/auth/ConfirmEmail.vue";
import Oauth2Success from "@/components/auth/Oauth2Success.vue";
import MainComponent from "@/components/MainComponent.vue";
import ProfileComponent from "@/components/ProfileComponent.vue";
import UsersComponent from "@/components/UsersComponent.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'MainComponent', component: MainComponent },
        { path: '/login', name: 'LoginComponent', component: LoginComponent },
        { path: '/registration', name: 'RegisterComponent', component: RegisterComponent },
        { path: '/confirm-email', name: 'ConfirmEmail', component: ConfirmEmail },
        { path: '/registration/oauth2-success', name: 'Oauth2Success', component: Oauth2Success },
        { path: '/profile', name: 'ProfileComponent', component: ProfileComponent },
        { path: '/users', name: 'UsersComponent', component: UsersComponent },
    ]
})

export default router