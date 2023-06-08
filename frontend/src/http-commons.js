import axios from "axios";

export const AXIOS = axios.create({
    baseURL: `/api`,
    headers: {
        'Access-Control-Allow-Origin': ['http://localhost:8080'],
        'Access-Control-Allow-Methods': 'GET,POST,DELETE,PUT,OPTIONS',
        'Access-Control-Allow-Headers': '*',
        'Access-Control-Allow-Credentials': true
    }
})