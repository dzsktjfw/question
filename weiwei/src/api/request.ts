import axios from 'axios'
import { start, close } from '@/utils/nprogress';
// 创建axios实例
const service = axios.create({
    // baseURL: '/', // api的base_Url
    baseURL: 'http://localhost:8080', // api的base_Url
    timeout: 50000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
    function (config) {
        start(); // 开始进度条
        // 在发送请求之前获取并添加token到请求头
        const token = localStorage.getItem('auth') || sessionStorage.getItem('auth')
        if (token) {
            // 通常JWT Token格式为 'Bearer token'
            config.headers['token'] = `${token}`
        }
        return config
    },
    function (error) {
        // 对请求错误做些什么
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    function (response) {
        close(); // 关闭进度条
        // 对响应数据做点什么
        return response
    },
    function (error) {
        // 对响应错误做点什么
        return Promise.reject(error)
    }
)

export default service
