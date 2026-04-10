<template>
  <div class="record-container">
    <div class="search-container">
      <el-input v-model="searchGoods" placeholder="搜索商品" style="width: 200px"></el-input>
      <el-input v-model="searchUser" placeholder="搜索用户" style="width: 200px"></el-input>
      <el-button type="primary" @click="load">搜索</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="goods" label="商品名称"></el-table-column>
      <el-table-column prop="user" label="操作用户"></el-table-column>
      <el-table-column prop="count" label="数量">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.count > 0 ? '#67C23A' : '#F56C6C' }">
            {{ scope.row.count > 0 ? '+' : '' }}{{ scope.row.count }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="time" label="操作时间">
        <template slot-scope="scope">
          {{ formatTime(scope.row.time) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
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
  name: 'Record',
  data() {
    return {
      searchGoods: '',
      searchUser: '',
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0
    }
  },
  mounted() {
    this.$emit('update-title', '记录管理')
    this.load()
  },
  methods: {
    load() {
      request.get('/record', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          goods: this.searchGoods,
          user: this.searchUser
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    handleDelete(id) {
      this.$confirm('确认删除该记录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/record/${id}`).then(() => {
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
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.record-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.search-container {
  display: flex;
  gap: 10px;
}
</style>
