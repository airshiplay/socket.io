import Vue from 'vue'
import VueI18n from 'vue-i18n'
Vue.use(VueI18n)

const navLang = navigator.language
const localLang = (navLang === 'zh-CN' || navLang === 'en-US') ? navLang : false
let lang = localLang || 'zh-CN'

Vue.config.lang = lang

// vue-i18n 6.x+写法
Vue.locale = () => {}
const messages = {
    // 'zh-CN': Object.assign(zhCnLocale, customZhCn),
    // 'zh-TW': Object.assign(zhTwLocale, customZhTw),
    // 'en-US': Object.assign(enUsLocale, customEnUs)
}
const i18n = new VueI18n({
    locale: lang,
    messages
})

export default i18n
