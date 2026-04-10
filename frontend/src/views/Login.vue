<template>
  <div class="login-container">
    <div class="login-box">
      <h2>仓库管理系统</h2>
      <el-form :model="user" :rules="rules" ref="userForm" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="user.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="user.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" @keyup.enter.native="login"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="login">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Login',
  data() {
    return {
      user: {
        username: 'admin',
        password: '123456'
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    login() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          request.post('/user/login', this.user).then(res => {
            localStorage.setItem('user', JSON.stringify(res.data))
            this.$message.success('登录成功')
            this.$router.push('/')
          }).catch(() => {
            this.$message.error('登录失败')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 400px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
