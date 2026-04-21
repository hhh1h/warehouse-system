import axios from 'axios'

const request = axios.create({
  // 使用相对路径，通过 vue.config.js 的 proxy 代理到后端
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    // 可在此添加 token 等
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      console.error('请求失败:', res.msg)
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  error => {
    console.error('响应错误:', error)
    return Promise.reject(error)
  }
)

export default request
