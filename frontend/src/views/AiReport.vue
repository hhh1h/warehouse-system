<template>
  <div class="ai-report">
    <el-card>
      <div slot="header" class="clearfix">
        <span>📊 AI 智能报表</span>
        <el-button style="float: right;" type="primary" @click="generateReport" :loading="loading">
          🔄 刷新报表
        </el-button>
      </div>

      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading"></i>
        <p>正在生成报表...</p>
      </div>

      <div v-else-if="reportData" class="report-content">
        <!-- 概览卡片 -->
        <el-row :gutter="20" class="overview-cards">
          <el-col :span="6">
            <div class="stat-card goods">
              <div class="stat-icon">📦</div>
              <div class="stat-info">
                <div class="stat-value">{{ reportData.totalGoods }}</div>
                <div class="stat-label">商品种类</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card stock">
              <div class="stat-icon">🏭</div>
              <div class="stat-info">
                <div class="stat-value">{{ reportData.totalStock }}</div>
                <div class="stat-label">总库存量</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card storage">
              <div class="stat-icon">📍</div>
              <div class="stat-info">
                <div class="stat-value">{{ reportData.totalStorage }}</div>
                <div class="stat-label">仓库数量</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card records">
              <div class="stat-icon">📝</div>
              <div class="stat-info">
                <div class="stat-value">{{ reportData.totalRecords }}</div>
                <div class="stat-label">记录总数</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 库存状态分布 -->
        <el-card class="section-card">
          <div slot="header">
            <span>📊 库存状态分布</span>
          </div>
          <div class="distribution-chart">
            <div
              v-for="item in reportData.stockDistribution"
              :key="item.category"
              class="distribution-item"
            >
              <div class="distribution-label">
                <span :class="['status-dot', getStatusClass(item.category)]"></span>
                {{ item.category }}
              </div>
              <div class="distribution-bar">
                <div
                  :class="['distribution-fill', getStatusClass(item.category)]"
                  :style="{ width: getBarWidth(item.count) + '%' }"
                ></div>
              </div>
              <div class="distribution-value">{{ item.count }}种</div>
            </div>
          </div>
        </el-card>

        <!-- 仓库库存统计 -->
        <el-card class="section-card">
          <div slot="header">
            <span>🏭 各仓库库存统计</span>
          </div>
          <el-table :data="reportData.storageStats" border>
            <el-table-column prop="name" label="仓库名称"></el-table-column>
            <el-table-column prop="goodsCount" label="商品种类">
              <template slot-scope="scope">
                <el-tag type="info">{{ scope.row.goodsCount }}种</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="totalStock" label="总库存">
              <template slot-scope="scope">
                <strong>{{ scope.row.totalStock }}</strong> 件
              </template>
            </el-table-column>
            <el-table-column label="占比">
              <template slot-scope="scope">
                {{ getStoragePercent(scope.row.totalStock) }}%
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 低库存预警 -->
        <el-card class="section-card" :class="{ 'has-warning': reportData.lowStockGoods && reportData.lowStockGoods.length > 0 }">
          <div slot="header">
            <span>⚠️ 低库存商品 ({{ reportData.lowStockGoods ? reportData.lowStockGoods.length : 0 }})</span>
          </div>
          <div v-if="reportData.lowStockGoods && reportData.lowStockGoods.length > 0">
            <el-table :data="reportData.lowStockGoods" border size="small">
              <el-table-column prop="name" label="商品名称"></el-table-column>
              <el-table-column prop="count" label="当前库存">
                <template slot-scope="scope">
                  <el-tag type="danger">{{ scope.row.count }}件</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="barcode" label="条码">
                <template slot-scope="scope">
                  {{ scope.row.barcode || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="建议">
                <template slot-scope="scope">
                  <span v-if="scope.row.count === 0" class="urgent">⚠️ 立即补货</span>
                  <span v-else-if="scope.row.count < 5" class="warning">⚡ 尽快补货</span>
                  <span v-else class="normal">📋 计划补货</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-else class="no-warning">
            <i class="el-icon-success" style="color: #67C23A; font-size: 48px;"></i>
            <p>🎉 所有商品库存充足，无需预警</p>
          </div>
        </el-card>

        <!-- AI 智能建议 -->
        <el-card class="section-card ai-suggestions">
          <div slot="header">
            <span>💡 AI 智能建议</span>
          </div>
          <div class="suggestions-list">
            <div class="suggestion-item" v-if="reportData.lowStockGoods && reportData.lowStockGoods.length > 0">
              <i class="el-icon-warning" style="color: #E6A23C;"></i>
              <div>
                <strong>库存预警</strong>
                <p>发现 {{ reportData.lowStockGoods.length }} 种商品库存不足，建议尽快进行采购补货。</p>
              </div>
            </div>
            <div class="suggestion-item" v-if="reportData.totalStock < 100">
              <i class="el-icon-info" style="color: #409EFF;"></i>
              <div>
                <strong>库存偏低</strong>
                <p>当前总库存量偏低，建议关注库存消耗速度，适当增加储备。</p>
              </div>
            </div>
            <div class="suggestion-item">
              <i class="el-icon-s-management" style="color: #67C23A;"></i>
              <div>
                <strong>定期盘点</strong>
                <p>建议每周进行一次库存盘点，确保数据准确性。</p>
              </div>
            </div>
            <div class="suggestion-item">
              <i class="el-icon-document" style="color: #909399;"></i>
              <div>
                <strong>数据导出</strong>
                <p>可在商品管理页面导出Excel报表进行存档分析。</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AiReport',
  data() {
    return {
      loading: false,
      reportData: null
    }
  },
  mounted() {
    this.generateReport()
  },
  methods: {
    async generateReport() {
      this.loading = true
      try {
        const response = await axios.get('/api/ai/report')
        if (response.data.code === 200) {
          this.reportData = response.data.data
        }
      } catch (error) {
        this.$message.error('获取报表失败')
      } finally {
        this.loading = false
      }
    },

    getStatusClass(category) {
      if (category.includes('充足')) return 'status-sufficient'
      if (category.includes('正常')) return 'status-normal'
      if (category.includes('预警')) return 'status-warning'
      if (category.includes('缺货')) return 'status-danger'
      return ''
    },

    getBarWidth(count) {
      if (!this.reportData) return 0
      const max = Math.max(...this.reportData.stockDistribution.map(s => s.count))
      return max > 0 ? (count / max) * 100 : 0
    },

    getStoragePercent(stock) {
      if (!this.reportData || this.reportData.totalStock === 0) return 0
      return Math.round((stock / this.reportData.totalStock) * 100)
    }
  }
}
</script>

<style scoped>
.ai-report {
  padding: 20px;
}

.loading-container {
  text-align: center;
  padding: 60px;
  color: #909399;
}

.loading-container i {
  font-size: 48px;
  margin-bottom: 20px;
}

.overview-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  color: white;
}

.stat-card.goods { background: linear-gradient(135deg, #409EFF, #66b1ff); }
.stat-card.stock { background: linear-gradient(135deg, #67C23A, #85ce61); }
.stat-card.storage { background: linear-gradient(135deg, #E6A23C, #ebb563); }
.stat-card.records { background: linear-gradient(135deg, #909399, #a6a9ad); }

.stat-icon {
  font-size: 40px;
  margin-right: 15px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.section-card {
  margin-bottom: 20px;
}

.distribution-chart {
  padding: 10px 0;
}

.distribution-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.distribution-label {
  width: 100px;
  display: flex;
  align-items: center;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
}

.status-sufficient { background: #67C23A; }
.status-normal { background: #409EFF; }
.status-warning { background: #E6A23C; }
.status-danger { background: #F56C6C; }

.distribution-bar {
  flex: 1;
  height: 20px;
  background: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
  margin: 0 15px;
}

.distribution-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
}

.distribution-value {
  width: 60px;
  text-align: right;
  color: #606266;
}

.no-warning {
  text-align: center;
  padding: 40px;
  color: #67C23A;
}

.no-warning p {
  margin-top: 15px;
  font-size: 16px;
}

.urgent { color: #F56C6C; font-weight: bold; }
.warning { color: #E6A23C; }
.normal { color: #909399; }

.suggestions-list {
  padding: 10px 0;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.suggestion-item i {
  font-size: 24px;
  margin-right: 15px;
  margin-top: 2px;
}

.suggestion-item strong {
  display: block;
  margin-bottom: 5px;
}

.suggestion-item p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}
</style>
