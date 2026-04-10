<template>
  <div class="user-container">
    <div class="form-container">
      <el-form :inline="true" :model="form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin"></el-option>
            <el-option label="普通用户" value="user"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">添加</el-button>
          <el-button type="warning" @click="update">更新</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="search-container">
      <el-input v-model="search" placeholder="搜索用户" style="width: 200px" @keyup.enter.native="load"></el-input>
      <el-button type="primary" @click="load">搜索</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="role" label="角色">
        <template slot-scope="scope">
          <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'primary'">
            {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" @click="handleDelete(scope.row.id)" style="color: #F56C6C">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'User',
  data() {
    return {
      form: {},
      search: '',
      tableData: []
    }
  },
  mounted() {
    this.$emit('update-title', '用户管理')
    this.load()
  },
  methods: {
    load() {
      request.get('/user', {
        params: {
          search: this.search
        }
      }).then(res => {
        this.tableData = res.data
      })
    },
    save() {
      request.post('/user', this.form).then(() => {
        this.$message.success('添加成功')
        this.load()
        this.reset()
      })
    },
    update() {
      request.put('/user', this.form).then(() => {
        this.$message.success('更新成功')
        this.load()
        this.reset()
      })
    },
    reset() {
      this.form = {}
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
    },
    handleDelete(id) {
      this.$confirm('确认删除该用户吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/user/${id}`).then(() => {
          this.$message.success('删除成功')
          this.load()
        })
      })
    }
  }
}
</script>

<style scoped>
.user-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.form-container {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #EBEEF5;
}

.search-container {
  display: flex;
  gap: 10px;
}
</style>
