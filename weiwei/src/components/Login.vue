<template>
  <div class="login-container" :style="{ backgroundImage: `url(${backgroundImage})` }">
    <el-card class="login-form">
      <!-- 系统名称 -->
      <div class="system-title">
        <h1>在线辅导答疑管理系统</h1>
      </div>

      <!-- 角色切换 -->
      <el-tabs v-model="activeRole" stretch>
        <el-tab-pane label="学生登录" name="student" />
        <el-tab-pane label="教师登录" name="teacher" />
      </el-tabs>

      <!-- 登录表单 -->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            :prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
          <!-- 密码错误提示 -->
          <div v-if="passwordError" class="error-message">
            <el-icon><Warning /></el-icon>
            用户名或密码错误，请重新输入
          </div>
        </el-form-item>

        <!-- 操作区域 -->
        <div class="action-area">
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
          <el-link type="primary" @click="handleForgotPassword">
            忘记密码？
          </el-link>
        </div>

        <el-button 
          type="primary" 
          size="large" 
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登录
        </el-button>
      </el-form>

      <!-- 注册引导 -->
      <div class="register-guide">
        <span>没有账号？</span>
        <el-link type="primary" @click="goToRegister">
          {{ activeRole === 'student' ? '注册学生账号' : '注册教师账号' }}
        </el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import backgroundImage from '@/assets/login-bg.jpg' // 背景图路径
import service from '@/api/request'
import * as jwt_decode from 'jwt-decode';

const activeRole = ref('student')
const router = useRouter()
const loading = ref(false)
const passwordError = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  get role() {
    return activeRole.value
  },
  remember: false
})

// 表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
})

// 处理忘记密码
const handleForgotPassword = () => {
  ElMessageBox.alert('请联系老师帮忙重置密码', '密码重置', {
    confirmButtonText: '确定',
    customClass: 'forgot-password-modal'
  })
}

// 处理登录提交
const handleLogin = async () => {
  loading.value = true
  
  try {
    
    
    // 调用后端登录接口
    const response = await service.post('/login', {
      role: loginForm.role,
      username: loginForm.username,
      password: loginForm.password
    })
    
    // 处理登录响应
    if (response.data && response.data.code === 1) {
      // 保存token到存储
      saveToken(response.data.data)
      ElMessage.success('登录成功')
      if(activeRole.value==='student'){
          router.push('/dashboard') // 跳转到主页
      }else{
          router.push('menu')
      }
    } else {
      // 处理登录失败
      const errorMsg = response.data?.msg || '用户名或密码错误'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    // 错误处理
    handleLoginError(error)
  } finally {
    loading.value = false
  }
}

// 保存Token到存储
const saveToken = (token) => {
  console.log('保存Token:', token)
  const item=jwt_decode.jwtDecode(token)
  console.log('解码后的Token:', item)
  if (loginForm.remember) {
    localStorage.setItem('token', JSON.stringify(item)) // 长期存储
    localStorage.setItem('auth',token)
  } else {
    sessionStorage.setItem('token', JSON.stringify(item) ) // 会话存储
    sessionStorage.setItem('auth',token)
  }
  
  // 同时存储用户角色
  const storage = loginForm.remember ? localStorage : sessionStorage
  storage.setItem('userRole', loginForm.role)
  storage.setItem('username', loginForm.username)
}

// 错误处理
const handleLoginError = (error) => {
  let errorMessage = '登录请求失败，请检查网络连接'
  
  if (error.response) {
    // 处理HTTP错误状态码
    switch (error.response.status) {
      case 401:
        errorMessage = '身份验证失败，请检查凭证'
        break
      case 403:
        errorMessage = '访问被拒绝，请联系管理员'
        break
      case 500:
        errorMessage = '服务器内部错误，请稍后再试'
        break
      default:
        errorMessage = error.response.data?.message || `请求失败: ${error.response.status}`
    }
  } else if (error.message) {
    errorMessage = error.message
  }
  
  ElMessage.error(errorMessage)
}

// 跳转到注册页面
const goToRegister = () => {
  // 方案3：带参数跳转（统一注册页）[6,8](@ref)
  router.push({
    path: '/register',
    query: { role: activeRole.value }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-size: cover;
  background-position: center;
  background-color: #f0f2f5; /* 背景图加载前的底色 */
}

.login-form {
  width: 100%;
  max-width: 450px;
  padding: 30px 40px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 6px 20px rgba(0, 0, 150, 0.15);
}

.system-title {
  text-align: center;
  margin-bottom: 25px;
}

.system-title h1 {
  font-size: 24px;
  color: #2c3e50;
  letter-spacing: 1px;
  font-weight: 600;
}

.action-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  font-size: 16px;
  letter-spacing: 2px;
}

.register-guide {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #606266;
}

.register-guide span {
  margin-right: 8px;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
  display: flex;
  align-items: center;
}

.error-message .el-icon {
  margin-right: 5px;
}

/* 模态框样式 */
:deep(.forgot-password-modal) {
  width: 400px;
  border-radius: 10px;
}

:deep(.forgot-password-modal .el-message-box__content) {
  padding: 25px 20px;
  font-size: 16px;
  text-align: center;
}
</style>