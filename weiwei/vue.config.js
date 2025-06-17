const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  publicPath:'./',
  transpileDependencies: true,
  lintOnSave: false,
  devServer:{
    port: 80,
    historyApiFallback: true,
    allowedHosts: "all",
    client: {
      overlay: false,
    },
  },
  pages: {
    index: {
      entry: 'src/main.ts', // 入口文件
      title: '网上辅导答疑管理系统' // 设置标题
    }
  }
})
