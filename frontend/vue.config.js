module.exports = {
  devServer: {
    host: '0.0.0.0',  // 允许外部访问
    port: 8080,
    allowedHosts: 'all'  // 允许所有主机访问(替代disableHostCheck)
  }
}
