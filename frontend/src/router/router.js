import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from "@/pages/auth/LoginPage.vue";
import RegisterPage from "@/pages/auth/RegisterPage.vue";
import ConfirmEmail from "@/pages/auth/ConfirmEmail.vue";
import Oauth2Success from "@/pages/auth/Oauth2Success.vue";
import MainComponent from "@/pages/MainComponent.vue";
import ProfileComponent from "@/pages/ProfileComponent.vue";
import UsersComponent from "@/pages/UsersComponent.vue";
import ArticlePage from "@/pages/ArticlePage.vue";
import CreateArticlePage from "@/pages/CreateArticlePage.vue";
import ActivityFeed from "@/pages/ActivityFeed.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'MainComponent', component: MainComponent },
        { path: '/login', name: 'LoginPage', component: LoginPage },
        { path: '/registration', name: 'RegisterPage', component: RegisterPage },
        { path: '/confirm-email', name: 'ConfirmEmail', component: ConfirmEmail },
        { path: '/registration/oauth2-success', name: 'Oauth2Success', component: Oauth2Success },
        { path: '/profile/:id?', name: 'ProfileComponent', component: ProfileComponent },
        { path: '/users', name: 'UsersComponent', component: UsersComponent },
        { path: '/article/:id', name: 'ArticlePage', component: ArticlePage },
        { path: '/create-article', name: 'CreateArticlePage', component: CreateArticlePage },
        { path: '/activity-feed', name: 'ActivityFeed', component: ActivityFeed },
    ]
})

export default router