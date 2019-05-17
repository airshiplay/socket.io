import Vue from 'vue'
import config from '@/defaultSettings'
import Storage from 'vue-ls'
Vue.use(Storage, config.storageOptions)