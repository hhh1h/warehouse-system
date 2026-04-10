<template>
  <div class="home-container">
    <div class="stats-cards">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-goods stat-icon"></i>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalGoods || 0 }}</div>
            <div class="stat-label">商品总数</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-house stat-icon"></i>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalStorage || 0 }}</div>
            <div class="stat-label">仓库数量</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-box stat-icon"></i>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalCount || 0 }}</div>
            <div class="stat-label">库存总量</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <i class="el-icon-user stat-icon"></i>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            <div class="stat-label">用户数量</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Home',
  data() {
    return {
      stats: {
        totalGoods: 0,
        totalStorage: 0,
        totalCount: 0,
        totalUsers: 0
      }
    }
  },
  mounted() {
    this.$emit('update-title', '首页')
    this.loadStats()
  },
  methods: {
    loadStats() {
      request.get('/goods/stats').then(res => {
        this.stats.totalGoods = res.data.totalGoods
        this.stats.totalStorage = res.data.totalStorage
        this.stats.totalCount = res.data.totalCount
      })
      request.get('/user/stats').then(res => {
        this.stats.totalUsers = res.data.totalUsers
      })
    }
  }
}
</script>

<style scoped>
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  cursor: pointer;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  font-size: 48px;
  color: #409EFF;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}
</style>
