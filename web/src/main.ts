import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/antd.css'
import * as Icons from '@ant-design/icons-vue'
import axios from "axios";


const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app')

const icons: any = Icons
for (const i in icons) {
    app.component(i, icons[i])
}
// 输出环境
// console.log('环境：', process.env.NODE_ENV)
// console.log('服务器：', process.env.VUE_APP_SERVER)

// 设置axios的baseURL
axios.defaults.baseURL = process.env.VUE_APP_SERVER
// 添加请求拦截器
axios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    console.log('请求参数: config',config)
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    console.log("返回结果: response",response)
    return response;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    console.log("返回错误: error",error)
    return Promise.reject(error);
});