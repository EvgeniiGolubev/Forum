import { createRouter, createWebHistory } from 'vue-router'
import LoginComponent from "@/components/LoginComponent.vue";
import MainComponent from "@/components/MainComponent.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', name: 'MainComponent', component: MainComponent },
        { path: '/login', name: 'LoginComponent', component: LoginComponent },
    ]
})

export default router