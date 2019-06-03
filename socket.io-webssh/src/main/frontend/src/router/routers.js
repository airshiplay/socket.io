import Main from '@/components/main'

export default [
    {
        path: '/login',
        name: 'login',
        meta: {
            title: 'Login - 登录',
            hideInMenu: true
        },
        component: () => import('@views/login/login.vue')
    },
    {
        path: '/',
        name: '_home',
        redirect: '/equipment',
    },
    {
        path: '/equipment',
        name: '_equipment',
        component: Main,
        children: [
            {
                path: '',
                name: 'equipment',
                component: () => import( /* webpackChunkName: "device" */  '@views/device/device-list.vue')
            }
        ]
    },
    {
        path: '/user',
        name: '_user',
        component: Main,
        children: [
            {
                path: '',
                name: 'user',
                component: () => import( /* webpackChunkName: "user" */ '@views/user/user-list.vue')
            }
        ]
    },
    {
        path: '/enterprise',
        name: '_enterprise',
        component: Main,
        children: [
            {
                path: '',
                name: 'enterprise',
                component: () => import( /* webpackChunkName: "enterprise" */ '@views/enterprise/enterprise-list.vue')
            }
        ]
    },
    {
        path: '/about',
        name: 'about',
        component: () => import(/* webpackChunkName: "about" */ '@views/About.vue')
    },
    {
        path: '/console/:id',
        name: 'console',
        component: () => import(/* webpackChunkName: "console" */ '@views/Console.vue')
    },

    {
        path: '/rtty/console/:devid',
        name: 'rtty_console',
        component: () => import(/* webpackChunkName: "rtty_console" */ '@views/rtty/Rtty.vue')
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