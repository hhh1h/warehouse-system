<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">仓库管理</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#1890ff"
      >
        <el-menu-item index="/home">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/goods">
          <i class="el-icon-goods"></i>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/storage">
          <i class="el-icon-house"></i>
          <span>仓库管理</span>
        </el-menu-item>
        <el-menu-item index="/record">
          <i class="el-icon-document"></i>
          <span>记录管理</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/alert">
          <i class="el-icon-bell"></i>
          <span>库存预警</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">{{ pageTitle }}</div>
        <div class="header-right">
          <span>{{ user.username }}</span>
          <el-button type="text" @click="logout">退出</el-button>
        </div>
      </el-header>

      <el-main class="main">
        <router-view @update-title="updateTitle"></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      pageTitle: '首页',
      user: {}
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    }
  },
  mounted() {
    const user = localStorage.getItem('user')
    if (user) {
      this.user = JSON.parse(user)
    }
  },
  methods: {
    updateTitle(title) {
      this.pageTitle = title
    },
    logout() {
      localStorage.removeItem('user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #001529;
  overflow: hidden;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  color: white;
  font-weight: bold;
  background-color: #002140;
}

.header {
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-title {
  font-size: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
