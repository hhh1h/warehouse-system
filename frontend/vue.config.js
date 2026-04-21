module.exports = {
  devServer: {
    host: '0.0.0.0',
    port: 8080,
    allowedHosts: 'all',
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''  // 去掉 /api 前缀，因为后端接口没有 /api
        }
      }
    }
  }
}
