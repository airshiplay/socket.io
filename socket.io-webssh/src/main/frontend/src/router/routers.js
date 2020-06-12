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
        component: Main,
        children: [
            {
                path: '',
                name: 'equipment',
                meta: {title:'Direct Equipment',},
                component: () => import( /* webpackChunkName: "device-list" */  '@views/device/device-list.vue')
            }
        ]
    },
    {
        path: '/console/:id',
        name: 'console',
        meta: {title:'Direct Terminal',},
        component: () => import(/* webpackChunkName: "console" */ '@views/Console.vue')
    },
    {
        path: '/connect/:host',
        name: 'console',
        meta: {title:'Direct Terminal',},
        component: () => import(/* webpackChunkName: "console" */ '@views/Console.vue')
    },
    {
        path: '/rtty',
        component: Main,
        children: [
            {
                path: '',
                name: 'rtty',
                meta: {title:'Rtty Equipment',},
                component: () => import( /* webpackChunkName: "rtty-list" */  '@views/rtty/rtty-list.vue')
            }
        ]
    }, {
        path: '/rtty/console/:devid',
        name: 'rtty_console',
        meta: {title:'Rtty Terminal',},
        component: () => import(/* webpackChunkName: "rtty_console" */ '@views/rtty/Rtty.vue')
    },
    {
        path: '/user',
        component: Main,
        children: [
            {
                path: '',
                name: 'user',
                component: () => import( /* webpackChunkName: "user-list" */ '@views/user/user-list.vue')
            }
        ]
    },
    {
        path: '/enterprise',
        component: Main,
        children: [
            {
                path: '',
                name: 'enterprise',
                component: () => import( /* webpackChunkName: "enterprise-list" */ '@views/enterprise/enterprise-list.vue')
            }
        ]
    },
    {
        path: '/about',
        name: 'about',
        component: () => import( /* webpackChunkName: "about" */'@views/About.vue')
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