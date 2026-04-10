import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:9090',
  timeout: 5000
})

request.interceptors.request.use(
  config => {
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
      return Promise.reject(new Error(res.msg || 'Error'))
    }
    return res
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

export default request
