<template>
  <div class="dashboard-container">
    <el-container style="height: 100vh;">
      <!-- Header区域 -->
      <el-header class="header">
        <div class="system-title">网上辅导答疑管理系统教师端</div>
        <div class="user-info">
          <el-dropdown trigger="hover" placement="bottom-end">
            <span class="el-dropdown-link">
              <el-avatar :size="40" :src="userAvatar" class="avatar" />
              <span class="teacher-name">{{ teacherName }}</span>
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showEditDialog">
                  <el-icon>
                    <Edit />
                  </el-icon>修改个人信息
                </el-dropdown-item>
                <el-dropdown-item @click="showPasswordDialog">
                  <el-icon>
                    <Lock />
                  </el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon>
                    <SwitchButton />
                  </el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <!-- Aside侧边栏 -->
        <el-aside width="220px" class="aside">
          <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" @select="handleMenuSelect"
            background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF" router>
            <div class="menu-header">
              <div class="menu-title">教师工作台</div>
              <div class="welcome">您好，{{ teacherName }}老师</div>
            </div>

            <el-menu-item index="/menu/welcome">
              <el-icon>
                <House />
              </el-icon>
              <span>总览</span>
            </el-menu-item>

            <el-menu-item index="/menu/question-bank">
              <el-icon>
                <Notebook />
              </el-icon>
              <span>常见问题库管理</span>
            </el-menu-item>

            <el-menu-item index="/menu/answer-center">
              <el-icon>
                <ChatDotRound />
              </el-icon>
              <span>答疑中心</span>
              <transition name="fade">
                <el-badge v-if="task > 0" :value="task" class="badge" />
              </transition>
            </el-menu-item>

            <el-menu-item index="/menu/student-management">
              <el-icon>
                <User />
              </el-icon>
              <span>学生管理</span>
            </el-menu-item>


            <el-menu-item index="/menu/my-answers">
              <el-icon>
                <List />
              </el-icon>
              <span>我的答疑记录</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- Main主内容区 -->
        <el-main class="main-container">
          <div class="main-content-wrapper">
            <div class="main-content">
              <router-view />

            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>

    <!-- 修改信息对话框 -->


    <!-- 修改密码对话框 -->
    <el-dialog v-model="editDialogVisible" title="修改个人信息" width="700px">
      <div class="form-container">
        <div class="form-title">基本信息</div>
        <div class="form-row">
          <div class="form-item">
            <el-form-item label="姓名" label-width="100px">
              <el-input v-model="teacherForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </div>
          <div class="form-item">
            <el-form-item label="性别" label-width="100px">
              <el-radio-group v-model="teacherForm.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </div>

        <div class="form-row">
          <div class="form-item">
            <el-form-item label="出生日期" label-width="100px">
              <el-date-picker v-model="teacherForm.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
                :disabled-date="pickerOptions" style="width: 100%" />
            </el-form-item>
          </div>
          <div class="form-item">
            <el-form-item label="职称" label-width="100px">
              <el-select v-model="teacherForm.title" placeholder="请选择职称" style="width: 100%">
                <el-option label="教授" value="教授" />
                <el-option label="副教授" value="副教授" />
                <el-option label="讲师" value="讲师" />
                <el-option label="助教" value="助教" />
              </el-select>
            </el-form-item>
          </div>
        </div>

        <div class="form-row">
          <div class="form-item">
            <el-form-item label="所属专业" label-width="100px">
              <el-select v-model="teacherForm.major" placeholder="请选择专业" style="width: 100%">
                <el-option label="计算机科学与技术" value="计算机科学与技术" />
                <el-option label="软件工程" value="软件工程" />
                <el-option label="人工智能" value="人工智能" />
                <el-option label="数据科学与大数据技术" value="数据科学与大数据技术" />
                <el-option label="网络工程" value="网络工程" />
              </el-select>
            </el-form-item>
          </div>
          <div class="form-item">
            <el-form-item label="职业类型" label-width="100px">
              <el-select v-model="teacherForm.employmentType" placeholder="请选择职业类型" style="width: 100%">
                <el-option label="专职" value="专职" />
                <el-option label="兼职" value="兼职" />
              </el-select>
            </el-form-item>
          </div>
        </div>

        <div class="form-title">工作信息</div>
        <div class="form-row">
          <div class="form-item">
            <el-form-item label="入职日期" label-width="100px">
              <el-date-picker v-model="teacherForm.hireDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD"
                :disabled-date="pickerOptions" style="width: 100%" />
            </el-form-item>
          </div>
          <div class="form-item">
            <el-form-item label="联系电话" label-width="100px">
              <el-input v-model="teacherForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="editDialogVisible = false">取消</el-button>
          <el-button class="submit-btn" type="primary" @click="saveTeacherInfo">保存</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="500px">
      <el-form :model="passwordForm" :rules="passwordRules" label-width="100px" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { updatedPwd,updateInfo,getTeacherInfo} from '@/api/question/updateinfo'
import {useStore} from 'vuex'

const router = useRouter()
const store = useStore()
const task=computed(() => store.getters.getTask)


// 从localStorage或sessionStorage获取教师信息
const teacherName = ref('教师用户')
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
const activeMenu = ref('')

// 显示欢迎面板的状态
const showWelcome = ref(true)

// 对话框可见性
const editDialogVisible = ref(false)
const passwordDialogVisible = ref(false)

// 教师信息表单
const teacherForm = ref({
  name: '张教授',
  gender: '',// 性别（可选男、女）
  birthDate: '', // 出生日期
  title: '', // 职称
  major: '', // 所属专业
  employmentType: '', // 职业类型（可选专职、兼职）
  hireDate: '', // 入职日期
  phone: '',
})

// 密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ]
}

// 日期选择器选项
const pickerOptions=(time)=> {
    return time.getTime() > Date.now() - 8.64e7;
}

// 密码验证方法
function validatePassword(rule, value, callback) {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 显示修改信息对话框
async function showEditDialog() {
  editDialogVisible.value = true
    try{
        const response = await getTeacherInfo(JSON.parse(sessionStorage.getItem('token')).id || JSON.parse(localStorage.getItem('token')).id)
        if(response.data.code==1){
            teacherForm.value = response.data.data
        }else{
            ElMessage.error(response.data.msg || '获取教师信息失败，请重试')
        }
    }catch (error) {
        ElMessage.error(error.message || '未知错误，请联系管理员')
    }
}

// 显示修改密码对话框
function showPasswordDialog() {
  passwordDialogVisible.value = true
}

// 保存教师信息
async function saveTeacherInfo() {
  // 实际项目中这里应该调用API
  try{
    const response = await updateInfo(teacherForm.value)
    if(response.data.code == 1) {
        teacherName.value = teacherForm.value.name
        if(localStorage.getItem('token')) {
            const token = JSON.parse(localStorage.getItem('token'))
            token.username = teacherForm.value.name
            localStorage.setItem('token', JSON.stringify(token))
        } else if(sessionStorage.getItem('token')) {
            const token = JSON.parse(sessionStorage.getItem('token'))
            token.username = teacherForm.value.name
            sessionStorage.setItem('token', JSON.stringify(token))
        }
      ElMessage.success('更新成功^_^')
    } else {
      ElMessage.error(response.data.msg || '更新失败，请重试')
    }
  }catch (error) {
    ElMessage.error(error.message || '未知错误，请联系管理员')
  }
  editDialogVisible.value = false
}

// 修改密码
async function changePassword() {
  // 实际项目中这里应该调用API
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
        ElMessage.error('新密码和确认密码不一致')
        return
    }
    try{
        const params ={
            id: JSON.parse(sessionStorage.getItem('token'))?.id || JSON.parse(localStorage.getItem('token'))?.id,
            oldPassword: passwordForm.value.oldPassword,
            newPassword: passwordForm.value.newPassword
        }
        const response = await updatedPwd(params)
        console.log('修改密码响应:', response)
        console.log(response.data)
        passwordDialogVisible.value = false
        if(response.data.code==1){
            ElMessage.success('密码修改成功')
              // 清除存储的登录信息
            localStorage.removeItem('token')
            sessionStorage.removeItem('token')
            router.push('/')
        }else{
            ElMessage.error(response.data.msg || '密码修改失败，请重试')
        }
    }catch (error) {
        ElMessage.error(error.message ||'未知错误，请联系管理员')
    }
    // 重置表单
     passwordForm.value = {
         oldPassword: '',
         newPassword: '',
         confirmPassword: ''
     }
}

// 处理菜单选择
function handleMenuSelect(index) {
  activeMenu.value = index
  // 当选择了具体菜单时，隐藏欢迎面板
  showWelcome.value = false
}

// 退出登录
function handleLogout() {
  // 清除存储的登录信息
  localStorage.removeItem('token')
  sessionStorage.removeItem('token')
  
  // 跳转到登录页面
  router.push('/')
  ElMessage.success('您已成功退出登录')
}

// 组件挂载时从存储中获取教师信息
onMounted(() => {
  // 从localStorage或sessionStorage获取教师信息
  const storedName = JSON.parse(sessionStorage.getItem('token'))?.username||JSON.parse(localStorage.getItem('token'))?.username || '教师用户'
  if (storedName) {
    teacherName.value = storedName
    teacherForm.value.name = storedName
  }
  store.dispatch('getTask') // 获取任务数
  // 设置当前激活的菜单
  activeMenu.value = router.currentRoute.value.path || '/menu'
  
})
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
  overflow: hidden;
}

/* Header样式 */
.header {
  height: 60px; /* 固定Header高度 */
  background-color: #2d3a4b;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  border-bottom: 1px solid #1f2d3d;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.system-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.user-info {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  color: white;
  cursor: pointer;
}

.teacher-name {
  margin: 0 10px;
  font-size: 16px;
}

.avatar {
  margin-right: 10px;
}

/* Aside样式 */
.aside {
  background-color: #304156;
  color: white;
  height: calc(100vh - 60px); /* 减去Header高度 */
  overflow: hidden;
  transition: width 0.3s;
}

.menu-header {
  padding: 20px 20px 15px;
  border-bottom: 1px solid #1f2d3d;
}

.menu-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #409EFF;
}

.welcome {
  font-size: 13px;
  color: #a0aebe;
}

.el-menu {
  border-right: none;
}

.el-menu-item {
  height: 56px;
  line-height: 56px;
  margin: 5px 0;
  border-radius: 4px;
}

.el-menu-item:hover {
  background-color: #263445 !important;
}

.el-menu-item.is-active {
  background-color: #1890ff !important;
  color: white !important;
}

.el-menu-item span {
  margin-left: 10px;
}

.badge {
  position: absolute;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
}

/* Main样式 */
.main-container {
  background-color: #f0f2f5;
  padding: 0; /* 移除内边距 */
  display: flex; /* 使用flex布局 */
  flex-direction: column;
  height: calc(100vh - 60px); /* 减去Header高度 */
}

.main-content-wrapper {
  flex: 1; /* 占据可用空间 */
  display: flex;
  flex-direction: column;
  padding: 20px; /* 添加内边距 */
  overflow-y: auto; /* 添加垂直滚动条 */
  width: 100%; /* 占据全部宽度 */
  box-sizing: border-box; /* 包含内边距 */
}

.main-content {
  background: #fff;
  border-radius: 8px;
  min-height: 100%;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 100%; /* 占据全部宽度 */
  box-sizing: border-box; /* 包含内边距 */
  overflow: visible; /* 允许内容溢出 */
}

/* 修复滚动条样式 */
.main-content-wrapper::-webkit-scrollbar {
  width: 8px;
  background-color: #f5f5f5;
}

.main-content-wrapper::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: #c1c1c1;
}

.main-content-wrapper::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}

</style>