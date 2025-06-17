<template>
  <div class="register-container" >
    <el-card class="register-card">
      <!-- 角色提示 -->
      <div class="role-hint">
        <el-icon :size="60" :color="roleColor">
          <component :is="roleIcon" />
        </el-icon>
        <h2>{{ roleText }}注册</h2>
        <p>请填写以下信息完成{{ roleText }}账号注册</p>
      </div>

      <!-- 注册表单 -->
      <el-form 
        ref="registerForm" 
        :model="formData" 
        :rules="formRules" 
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="请输入3-16位字母或数字" 
            clearable
            :prefix-icon="User"
          />
          <div class="input-tip">3-16位字符，只能包含字母和数字</div>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="6-20位字符，包含字母和数字" 
            show-password
            :prefix-icon="Lock"
          />
          <div class="password-strength">
            <div 
              class="strength-bar"
              :class="strengthClass"
            ></div>
            <div class="strength-text">
              {{ strengthText }}
            </div>
          </div>
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="formData.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-button 
          type="primary" 
          native-type="submit" 
          class="register-btn"
          :loading="loading"
        >
          注册{{ roleText }}账号
        </el-button>
      </el-form>

      <!-- 登录引导 -->
      <div class="login-guide">
        <span>已有账号?</span>
        <el-link type="primary" @click="goToLogin">立即登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Lock, User as UserIcon, Avatar } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import service from '@/api/request' 

const route = useRoute()
const router = useRouter()
const loading = ref(false)

// 添加表单引用
const registerForm = ref(null) // 解决 registerForm is not defined 问题

// 从路由参数获取注册角色
const registerRole = ref(route.query.role || 'student')


// 角色相关计算属性
const roleText = computed(() => registerRole.value === 'student' ? '学生' : '教师')
const roleIcon = computed(() => registerRole.value === 'student' ? UserIcon : Avatar)
const roleColor = computed(() => registerRole.value === 'student' ? '#409EFF' : '#F56C6C')

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 16, message: '用户名长度在3到16个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '用户名只能包含字母和数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' },
    { pattern: /[a-zA-Z]/, message: '密码必须包含字母', trigger: 'blur' },
    { pattern: /[0-9]/, message: '密码必须包含数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== formData.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 密码强度计算
const passwordStrength = computed(() => {
  if (!formData.password) return 0
  
  let strength = 0
  if (formData.password.length >= 6) strength += 1
  if (formData.password.length >= 8) strength += 1
  if (/[A-Z]/.test(formData.password)) strength += 1
  if (/[0-9]/.test(formData.password)) strength += 1
  if (/[^A-Za-z0-9]/.test(formData.password)) strength += 1
  
  return Math.min(strength, 4)
})

// 密码强度文本和样式
const strengthText = computed(() => {
  const texts = ['', '弱', '一般', '良好', '强']
  return texts[passwordStrength.value]
})

const strengthClass = computed(() => {
  return [
    'strength-level-' + passwordStrength.value,
    passwordStrength.value > 0 ? 'active' : ''
  ]
})

// 处理注册提交
const handleRegister = async () => {
   // 表单验证 - 添加安全访问检查
  if (!registerForm.value) return
  

  try {
     // 执行表单验证
    const valid = await registerForm.value.validate()
    if (!valid) {
      ElMessage.warning('请检查表单中的错误')
      return
    }
    
    loading.value = true
    // 调用后端注册接口
    const response = await service.post('/register', {
      role: registerRole.value,
      username: formData.username,
      password: formData.password
    });

    // 检查响应状态码（根据您的后端设计调整）
    if (response.data && response.data.code === 1) {
      ElMessage.success(`${roleText.value}账号注册成功！`);

      // 注册成功后跳转到登录页面
      router.push('/');
    } else {
      // 处理非200状态码的响应
      const errorMsg = response.data?.msg || '注册失败，请稍后重试';
      ElMessage.error(errorMsg);
    }
  } catch (error) {
    // 详细的错误处理
    let errorMessage = '请检查表单内容';

    // 优先使用服务器返回的错误信息
    if (error.response && error.response.data) {
      errorMessage = error.response.data.message || error.response.data;
    }
    // 使用Axios提供的错误信息
    else if (error.message) {
      errorMessage = error.message;
    }

    // 特殊处理超时错误
    if (error.code === 'ECONNABORTED') {
      errorMessage = '请求超时，请检查网络连接或稍后重试';
    }

    ElMessage.error(`注册失败: ${errorMessage}`);
  } finally {
    loading.value = false;
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/')
}

// 组件挂载时验证角色有效性
onMounted(() => {
  if (!['student', 'teacher'].includes(registerRole.value)) {
    ElMessage.warning('无效的注册角色，已默认为学生注册')
    registerRole.value = 'student'
  }
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;
  padding: 30px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.role-hint {
  text-align: center;
  margin-bottom: 30px;
}

.role-hint h2 {
  color: #2c3e50;
  margin: 15px 0 5px;
}

.role-hint p {
  color: #7f8c8d;
  font-size: 14px;
}

.register-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  margin-top: 20px;
}

.login-guide {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #606266;
}

.login-guide span {
  margin-right: 8px;
}

.input-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}

.password-strength {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.strength-bar {
  height: 6px;
  flex-grow: 1;
  background: #e0e0e0;
  border-radius: 3px;
  margin-right: 10px;
  overflow: hidden;
  position: relative;
}

.strength-bar::after {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 0;
  background: #f56c6c;
  transition: width 0.3s ease, background 0.3s ease;
}

.strength-level-1.active::after {
  width: 25%;
  background: #f56c6c;
}

.strength-level-2.active::after {
  width: 50%;
  background: #e6a23c;
}

.strength-level-3.active::after {
  width: 75%;
  background: #409eff;
}

.strength-level-4.active::after {
  width: 100%;
  background: #67c23a;
}

.strength-text {
  font-size: 12px;
  color: #909399;
  min-width: 30px;
}
</style>