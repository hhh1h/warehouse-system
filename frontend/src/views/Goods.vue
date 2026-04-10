<template>
  <div class="goods-container">
    <div class="form-container">
      <el-form :inline="true" :model="form" :rules="rules" ref="formRef">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称"></el-input>
        </el-form-item>
        <el-form-item label="商品条码">
          <el-input v-model="form.barcode" placeholder="请输入商品条码(选填)">
            <el-button slot="append" icon="el-icon-scanner" @click="showQRCodeDialog" :disabled="!form.barcode">生成二维码</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="仓库" prop="storage">
          <el-select v-model="form.storage" placeholder="请选择仓库">
            <el-option
              v-for="item in storageList"
              :key="item.id"
              :label="getStorageLabel(item)"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="库存" prop="type">
          <el-input v-model="form.type" type="number" placeholder="请输入库存数量"></el-input>
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
      <el-input v-model="search" placeholder="搜索商品名称" style="width: 200px" @keyup.enter.native="load"></el-input>
      <el-input v-model="barcodeSearch" placeholder="扫描/输入条码" style="width: 200px" @keyup.enter.native="searchByBarcode">
        <el-button slot="append" icon="el-icon-search" @click="searchByBarcode">查询</el-button>
      </el-input>
      <el-button type="primary" @click="load">搜索</el-button>
      <el-button type="danger" @click="handleBatchDelete" :disabled="multipleSelection.length === 0">批量删除</el-button>
      <el-button type="success" @click="downloadTemplate">下载模板</el-button>
      <el-button type="warning" @click="exportExcel">导出Excel</el-button>
    </div>

    <el-table
      :data="tableData"
      style="width: 100%; margin-top: 20px"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="商品名称"></el-table-column>
      <el-table-column prop="remark" label="仓库"></el-table-column>
      <el-table-column prop="type" label="库存"></el-table-column>
      <el-table-column prop="barcode" label="条码"></el-table-column>
      <el-table-column label="操作" width="250">
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

    <!-- 二维码对话框 -->
    <el-dialog
      title="商品二维码"
      :visible.sync="qrCodeVisible"
      width="400px"
      center
    >
      <div class="qrcode-container">
        <div ref="qrcode" class="qrcode"></div>
        <div class="qrcode-info">
          <p><strong>商品名称:</strong> {{ currentGoods.name }}</p>
          <p><strong>商品条码:</strong> {{ currentGoods.barcode }}</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="qrCodeVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- Excel导入对话框 -->
    <el-dialog
      title="批量导入商品"
      :visible.sync="importDialogVisible"
      width="500px"
    >
      <el-upload
        class="upload-demo"
        drag
        action="/api/excel/import/goods"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        accept=".xlsx,.xls"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处,或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传 xlsx/xls 文件,且不超过 5MB</div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="importDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- Excel导入按钮 -->
    <div class="excel-actions">
      <el-button type="success" icon="el-icon-download" @click="importDialogVisible = true">批量导入</el-button>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import QRCode from 'qrcode'
import * as XLSX from 'xlsx'

export default {
  name: 'Goods',
  data() {
    return {
      form: {},
      search: '',
      barcodeSearch: '',
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      multipleSelection: [],
      storageList: [],
      qrCodeVisible: false,
      currentGoods: {},
      importDialogVisible: false,
      rules: {
        name: [
          { required: true, message: '请输入商品名称', trigger: 'blur' }
        ],
        storage: [
          { required: true, message: '请选择仓库', trigger: 'change' }
        ],
        type: [
          { required: true, message: '请输入库存数量', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.$emit('update-title', '商品管理')
    this.load()
    this.loadStorage()
  },
  methods: {
    loadStorage() {
      request.get('/storage/list').then(res => {
        this.storageList = res.data
      })
    },
    load() {
      request.get('/goods', {
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
    searchByBarcode() {
      if (!this.barcodeSearch) {
        this.$message.warning('请输入条码')
        return
      }
      request.get(`/goods/barcode/${this.barcodeSearch}`).then(res => {
        this.tableData = [res.data]
        this.total = 1
        this.$message.success('查询成功')
      }).catch(err => {
        this.$message.error(err.response?.data?.msg || '未找到该条码商品')
      })
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          request.post('/goods', this.form).then(() => {
            this.$message.success('添加成功')
            this.load()
            this.reset()
          })
        } else {
          this.$message.warning('请填写完整的商品信息')
          return false
        }
      })
    },
    update() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          request.put('/goods', this.form).then(() => {
            this.$message.success('更新成功')
            this.load()
            this.reset()
          })
        } else {
          this.$message.warning('请填写完整的商品信息')
          return false
        }
      })
    },
    reset() {
      this.form = {}
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
    },
    handleDelete(id) {
      this.$confirm('确认删除该商品吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/goods/${id}`).then(() => {
          this.$message.success('删除成功')
          this.load()
        })
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleBatchDelete() {
      const ids = this.multipleSelection.map(item => item.id)
      this.$confirm(`确认删除选中的 ${ids.length} 个商品吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete('/goods/del/batch', { data: ids }).then(() => {
          this.$message.success('批量删除成功')
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
    showQRCodeDialog(row) {
      if (typeof row === 'object') {
        this.currentGoods = row
      } else {
        this.currentGoods = this.form
      }

      if (!this.currentGoods.barcode) {
        this.$message.warning('请先设置商品条码')
        return
      }

      this.qrCodeVisible = true
      this.$nextTick(() => {
        const qrcodeContainer = this.$refs.qrcode
        qrcodeContainer.innerHTML = ''
        QRCode.toCanvas(this.currentGoods.barcode, { width: 200 }, (error, canvas) => {
          if (error) {
            console.error(error)
            this.$message.error('生成二维码失败')
          } else {
            qrcodeContainer.appendChild(canvas)
          }
        })
      })
    },
    downloadTemplate() {
      window.location.href = '/api/excel/template/goods'
    },
    exportExcel() {
      window.location.href = '/api/excel/export/goods'
      this.$message.success('正在导出Excel文件...')
    },
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
                      file.type === 'application/vnd.ms-excel'
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isExcel) {
        this.$message.error('只能上传Excel文件!')
        return false
      }
      if (!isLt5M) {
        this.$message.error('上传文件大小不能超过 5MB!')
        return false
      }
      return true
    },
    handleImportSuccess(response) {
      if (response.code === 200) {
        this.$message.success('导入成功!')
        this.importDialogVisible = false
        this.load()
      } else {
        this.$message.error(response.msg || '导入失败')
      }
    },
    handleImportError(err) {
      this.$message.error('上传失败,请重试')
    },
    getStorageLabel(item) {
      if (item.place && item.place !== item.name) {
        return `${item.name} (${item.place})`
      }
      return item.name
    }
  }
}
</script>

<style scoped>
.goods-container {
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

.qrcode-container {
  text-align: center;
  padding: 20px;
}

.qrcode {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.qrcode-info {
  text-align: left;
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.qrcode-info p {
  margin: 8px 0;
  color: #333;
}

.excel-actions {
  position: fixed;
  right: 40px;
  bottom: 100px;
  z-index: 999;
}
</style>
