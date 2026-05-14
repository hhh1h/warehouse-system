import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: 'goods',
        name: 'Goods',
        component: () => import('../views/Goods.vue')
      },
      {
        path: 'storage',
        name: 'Storage',
        component: () => import('../views/Storage.vue')
      },
      {
        path: 'record',
        name: 'Record',
        component: () => import('../views/Record.vue')
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/User.vue')
      },
      {
        path: 'alert',
        name: 'Alert',
        component: () => import('../views/Alert.vue')
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 如果访问登录页且已登录，直接跳转首页
  if (to.path === '/login') {
    if (token) {
      next({ path: '/home', replace: true })
    } else {
      next()
    }
    return
  }
  
  // 其他页面需要登录
  if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
