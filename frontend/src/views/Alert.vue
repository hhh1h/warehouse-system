<template>
  <div class="alert-container">
    <div class="alert-header">
      <el-alert
        :title="`共有 ${unreadCount} 条未读预警`"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 20px"
      >
      </el-alert>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="loadAlerts">刷新</el-button>
        <el-button type="success" size="small" @click="checkInventory">检查库存</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="未读预警" name="unread">
        <el-table :data="alertList" style="width: 100%" v-loading="loading">
          <el-table-column prop="type" label="类型" width="100">
            <template slot-scope="scope">
              <el-tag :type="getAlertType(scope.row.type)" size="small">
                {{ getAlertTypeName(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="goodsName" label="商品名称"></el-table-column>
          <el-table-column prop="currentCount" label="当前库存" width="100"></el-table-column>
          <el-table-column prop="threshold" label="预警阈值" width="100"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="markAsRead(scope.row)">标记已读</el-button>
              <el-button type="text" size="small" @click="deleteAlert(scope.row.id)" style="color: #F56C6C">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="所有预警" name="all">
        <el-table :data="alertList" style="width: 100%" v-loading="loading">
          <el-table-column prop="type" label="类型" width="100">
            <template slot-scope="scope">
              <el-tag :type="getAlertType(scope.row.type)" size="small">
                {{ getAlertTypeName(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="goodsName" label="商品名称"></el-table-column>
          <el-table-column prop="currentCount" label="当前库存" width="100"></el-table-column>
          <el-table-column prop="threshold" label="预警阈值" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 'unread' ? 'warning' : 'info'" size="small">
                {{ scope.row.status === 'unread' ? '未读' : '已读' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button
                type="text"
                size="small"
                @click="markAsRead(scope.row)"
                v-if="scope.row.status === 'unread'"
              >
                标记已读
              </el-button>
              <el-button type="text" size="small" @click="deleteAlert(scope.row.id)" style="color: #F56C6C">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Alert',
  data() {
    return {
      activeTab: 'unread',
      alertList: [],
      unreadCount: 0,
      loading: false
    }
  },
  mounted() {
    this.$emit('update-title', '库存预警')
    this.loadAlerts()
    this.loadUnreadCount()
  },
  methods: {
    loadAlerts() {
      this.loading = true
      request.get('/alert', {
        params: { status: this.activeTab === 'unread' ? 'unread' : '' }
      }).then(res => {
        this.alertList = res.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    loadUnreadCount() {
      request.get('/alert/count').then(res => {
        this.unreadCount = res.data.unreadCount || 0
      })
    },
    handleTabClick() {
      this.loadAlerts()
    },
    markAsRead(row) {
      request.put(`/alert/${row.id}/read`).then(() => {
        this.$message.success('已标记为已读，可在"所有预警"中查看')
        this.loadAlerts()
        this.loadUnreadCount()
      })
    },
    deleteAlert(id) {
      this.$confirm('确认删除该预警吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/alert/${id}`).then(() => {
          this.$message.success('删除成功')
          this.loadAlerts()
          this.loadUnreadCount()
        })
      })
    },
    checkInventory() {
      request.post('/alert/check').then(res => {
        const alertCount = res.data.alertCount || 0
        if (alertCount > 0) {
          this.$message.success(`检查完成,发现 ${alertCount} 条新预警`)
        } else {
          this.$message.info('检查完成,暂无新预警')
        }
        this.loadAlerts()
        this.loadUnreadCount()
      })
    },
    getAlertType(type) {
      const typeMap = {
        'low_stock': 'danger',
        'overdue': 'warning',
        'excess': 'warning'
      }
      return typeMap[type] || 'info'
    },
    getAlertTypeName(type) {
      const nameMap = {
        'low_stock': '低库存',
        'overdue': '过期',
        'excess': '积压'
      }
      return nameMap[type] || type
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return d.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.alert-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
}

.alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}
</style>
