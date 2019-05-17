const path = require('path')

const resolve = dir => {
    return path.join(__dirname, dir)
}
const debug = process.env.NODE_ENV !== 'production'


module.exports = {
    publicPath: debug ? '/' : '/',
    outputDir: '../../../target/classes/static', // 构建输出目录
    assetsDir: 'assets', // 静态资源目录 (js, css, img, fonts)
    lintOnSave: false, // 是否开启eslint保存检测，有效值：ture | false | 'error'
    chainWebpack: config => {
        config.resolve.alias
            .set('@', resolve('src'))
            .set('@api', resolve('src/api'))
            .set('@assets', resolve('src/assets'))
            .set('@comp', resolve('src/components'))
            .set('@views', resolve('src/views'))
            .set('@layout', resolve('src/layout'))
            .set('@static', resolve('src/static'))
    },
    runtimeCompiler: true, // 运行时版本是否需要编译
    transpileDependencies: [], // 默认babel-loader忽略mode_modules，这里可增加例外的依赖包名
    productionSourceMap: false, // 是否在构建生产包时生成 sourceMap 文件，false将提高构建速度
    css: { // 配置高于chainWebpack中关于css loader的配置
        // modules: true, // 是否开启支持‘foo.module.css’样式
        // extract: true, // 是否使用css分离插件 ExtractTextPlugin，采用独立样式文件载入，不采用<style>方式内联至html文件中
        sourceMap: false, // 是否在构建样式地图，false将提高构建速度
        loaderOptions: { // css预设器配置项
            sass: {
                data: ''//`@import "@/assets/scss/mixin.scss";`
            }
        }
    },
    parallel: require('os').cpus().length > 1, // 构建时开启多进程处理babel编译
    pluginOptions: { // 第三方插件配置
    },
    pwa: { // 单页插件相关配置 https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-pwa
    },
    devServer: {
        open: true,
        host: '0.0.0.0',
        port: 8080,
        https: false,
        hotOnly: false,
        // proxy: 'http://127.0.0.1:8081',
        proxy: {
            '/api': {
                target: 'http://127.0.0.1:8080',
                ws: true,
                changOrigin: true,
                pathRewrite: {
                    '^/api/': '/api/',
                }
            },
            '/socket.io': {
                target: 'http://127.0.0.1:8080',
                ws: true,
                changOrigin: true,
                pathRewrite: {
                    '^/socket.io/': '/socket.io/',
                }
            },
            '/ws': {
                target: 'http://127.0.0.1:8080',
                ws: true,
                changOrigin: true,
                pathRewrite: {
                    '^/ws': '/ws',
                }
            }
        },
        overlay: {
            warnings: true,
            errors: true
        },
        before: app => {
        }
    }
}
