<template>
  <div class="storage-container">
    <div class="form-container">
      <el-form :inline="true" :model="form">
        <el-form-item label="仓库名称">
          <el-input v-model="form.name" placeholder="请输入仓库名称"></el-input>
        </el-form-item>
        <el-form-item label="仓库地址">
          <el-input v-model="form.place" placeholder="请输入仓库地址"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="请输入备注"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">添加</el-button>
          <el-button type="warning" @click="update">更新</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="search-container">
      <el-input v-model="search" placeholder="搜索仓库" style="width: 200px" @keyup.enter.native="load"></el-input>
      <el-button type="primary" @click="load">搜索</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="仓库名称" width="200">
        <template slot-scope="scope">
          <div class="storage-name">
            <div class="main-name">{{ scope.row.name }}</div>
            <div class="sub-name">{{ scope.row.place }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="place" label="仓库地址"></el-table-column>
      <el-table-column prop="remark" label="备注"></el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" @click="handleDelete(scope.row.id)" style="color: #F56C6C">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[5, 10, 20]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      style="margin-top: 20px"
    ></el-pagination>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Storage',
  data() {
    return {
      form: {},
      search: '',
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  mounted() {
    this.$emit('update-title', '仓库管理')
    this.load()
  },
  methods: {
    load() {
      request.get('/storage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          search: this.search
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      // 验证仓库名称是否为空
      if (!this.form.name) {
        this.$message.warning('请输入仓库名称')
        return
      }

      // 检查仓库名称是否重复
      const isDuplicate = this.tableData.some(item =>
        item.name === this.form.name && item.id !== this.form.id
      )

      if (isDuplicate) {
        this.$message.warning('仓库名称已存在,请使用不同的名称')
        return
      }

      request.post('/storage', this.form).then(() => {
        this.$message.success('添加成功')
        this.load()
        this.reset()
      })
    },
    update() {
      request.put('/storage', this.form).then(() => {
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
      this.$confirm('确认删除该仓库吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/storage/${id}`).then(() => {
          this.$message.success('删除成功')
          this.load()
        })
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.load()
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.load()
    }
  }
}
</script>

<style scoped>
.storage-container {
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

.storage-name {
  display: flex;
  flex-direction: column;
}

.main-name {
  font-weight: bold;
  color: #303133;
}

.sub-name {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}
</style>
