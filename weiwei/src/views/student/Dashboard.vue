<template>
  <!-- 主布局容器 -->
  <div class="dashboard-container">
    <!-- 顶部Header区域 -->
    <el-header class="header" height="70px">
      <div class="header-content">
        <div class="system-title">
          <h1>网上辅导答疑管理系统学生端</h1>
        </div>
        <div class="user-area">
          <el-icon class="user-icon"><User /></el-icon>
          <span class="username">{{ userName }}</span>
          <el-button 
            class="logout-btn" 
            type="text" 
            @click="handleLogout"
          >
            退出
          </el-button>
        </div>
      </div>
    </el-header>

    <!-- 主体区域 -->
    <div class="main-layout">
      <!-- 左侧Aside区域 -->
      <el-aside class="aside" width="200px">
        <el-menu
          :default-active="activeMenu"
          class="dashboard-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="commonQuestions">
            <el-icon><Document /></el-icon>
            <span>常见问题库</span>
          </el-menu-item>
          
          <el-menu-item index="askQuestion">
            <el-icon><EditPen /></el-icon>
            <span>我要提问</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧Main内容区域 -->
      <el-main class="main-content">
        <component :is="currentComponent" />
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Document, EditPen } from '@element-plus/icons-vue'

// 动态导入组件
import CommonQuestions from './CommonQuestions.vue'
import AskQuestion from './AskQuestion.vue'

const router = useRouter()
const activeMenu = ref('commonQuestions') // 默认激活菜单

// 用户名 - 从登录信息中获取
const userName = ref('')
onMounted(() => {
  
  // 从存储中获取用户名
  userName.value =JSON.parse(sessionStorage.getItem('token'))?.username||JSON.parse(localStorage.getItem('token'))?.username || '学生用户'
})

// 当前显示组件
const currentComponent = computed(() => {
  switch(activeMenu.value) {
    case 'commonQuestions':
      return CommonQuestions
    case 'askQuestion':
      return AskQuestion
    default:
      return CommonQuestions
  }
})

// 菜单选择处理
const handleMenuSelect = (index) => {
  activeMenu.value = index
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除登录信息
    localStorage.removeItem('token')
    sessionStorage.removeItem('token')
    localStorage.removeItem('username')
    
    // 跳转到登录页
    router.replace('/')
    ElMessage.success('已安全退出')
  }).catch(() => {
    // 用户取消操作
  })
}
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background-color: #1e4fa1; /* 系统主色调 */
  color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.system-title h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-icon {
  font-size: 18px;
}

.username {
  font-weight: 500;
  margin-right: 15px;
}

.logout-btn {
  color: #ffffff;
  padding: 8px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.aside {
  background-color: #ffffff;
  border-right: 1px solid #e6e6e6;
  overflow-y: auto;
  height: 100%;
  padding-top: 10px;
}

.dashboard-menu {
  border-right: none;
}

.dashboard-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 5px 10px;
  border-radius: 5px;
}

.dashboard-menu .el-menu-item:hover {
  background-color: #f0f7ff;
}

.dashboard-menu .el-menu-item.is-active {
  background-color: #1e4fa11a; /* 主色调的浅色 */
  color: #1e4fa1;
  font-weight: 500;
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto;
}
</style>