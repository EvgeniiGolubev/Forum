import {createStore} from 'vuex'

const state = {
    authenticate: localStorage.getItem('user-authenticate') || '',
    role: localStorage.getItem('user-role') || '',
    name: localStorage.getItem('user-name') || '',
    picture: localStorage.getItem('user-picture') || '',
}

const getters = {
    isAuthenticated: state => {
        return state.authenticate;
    },
    isAdmin: state => {
        return state.role === 'admin'
    },
    isModerator: state => {
        return state.role === 'admin' || state.role === 'moderator'
    },
    getName: state => {
        return state.name
    },
    getPicture: state => {
        return state.picture
    },
}

const mutations = {
    loginMutation: (state, user) => {
        localStorage.setItem("user-authenticate", user.authenticate)
        localStorage.setItem("user-name", user.name)
        localStorage.setItem("user-picture", user.picture)
        state.authenticate = user.authenticate
        state.name = user.name
        state.picture = user.picture

        let isUser = false
        let isModerator = false
        let isAdmin = false

        for (let i = 0; i < user.roles.length; i++) {
            if (user.roles[i] === 'USER') {
                isUser = true
            } else if (user.roles[i] === 'MODERATOR') {
                isModerator = true
            } else if (user.roles[i] === 'ADMIN') {
                isAdmin = true
            }
        }

        if (isUser) {
            localStorage.setItem('user-role', 'USER')
            state.role = 'USER'
        }

        if (isModerator) {
            localStorage.setItem('user-role', 'MODERATOR')
            state.role = 'MODERATOR'
        }

        if (isAdmin) {
            localStorage.setItem('user-role', 'ADMIN')
            state.role = 'ADMIN'
        }
    },
    logoutMutation: () => {
        state.authenticate = false
        state.role = ''
        store.name =''
        store.picture = ''
        localStorage.removeItem("user-authenticate")
        localStorage.removeItem("user-role")
        localStorage.removeItem("user-name")
        localStorage.removeItem("user-picture")
    }
}

const actions = {
    loginAction: (context, user) => {
        context.commit('loginMutation', user)
    },
    logoutAction: (context) => {
        context.commit('logoutMutation')
    }
}

const store = createStore({
    state: state,
    getters: getters,
    mutations: mutations,
    actions: actions
})


export default store