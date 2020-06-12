import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import i18n from './locale'
import './plugins/ant-design-vue.js'
import './plugins/vue-ls.js'
Vue.config.productionTip = false

new Vue({
    router,
    i18n,
    store,
    render: h => h(App)
}).$mount('#app')
