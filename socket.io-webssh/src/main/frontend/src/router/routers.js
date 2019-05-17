import Main from '@/components/main'

export default [
    {
        path: '/',
        name: '_home',
        redirect: '/home',
        component: Main,
        children: [
            {
                path: '/home',
                name: 'home',
                component: () => import('@views/Home.vue')
            }
        ]
    }, {
        path: '/login',
        name: 'login',
        meta: {
            title: 'Login - 登录',
            hideInMenu: true
        },
        component: () => import('@views/login/login.vue')
    },
    {
        path: '/about',
        name: 'about',
        component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
    },
    {
        path: '/console/:id',
        name: 'console',
        component: () => import(/* webpackChunkName: "about" */ '../views/Console.vue')
    },

    {
        path: '/rtty/console/:devid',
        name: 'rtty_console',
        component: () => import(/* webpackChunkName: "about" */ '../views/rtty/Rtty.vue')
    },
    {
        path: '/401',
        name: 'error_401',
        meta: {
            hideInMenu: true
        },
        component: () => import('@views/error-page/401.vue')
    },
    {
        path: '/500',
        name: 'error_500',
        meta: {
            hideInMenu: true
        },
        component: () => import('@views/error-page/500.vue')
    },
    {
        path: '*',
        name: 'error_404',
        meta: {
            hideInMenu: true
        },
        component: () => import('@views/error-page/404.vue')
    }
]