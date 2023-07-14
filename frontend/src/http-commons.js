import axios from "axios";

export const AXIOS = axios.create({
    baseURL: `/api`,
    headers: {
        'Access-Control-Allow-Origin': ['http://localhost:8080'],
        'Access-Control-Allow-Methods': 'GET,POST,DELETE,PUT,OPTIONS',
        'Access-Control-Allow-Headers': '*',
        'Access-Control-Allow-Credentials': true
    },
})

// AXIOS.interceptors.response.use(
//     response => response,
//     error => {
//         console.log(error)
//         if (error.response && error.response.status === 401) {
//             this.$store.dispatch('logoutAction')
//             this.$router.push('/login')
//         }
//
//         if (error.response && error.response.status === 404) {
//             this.$router.push('/')
//         }
//
//         return Promise.reject(error)
//     }
// )