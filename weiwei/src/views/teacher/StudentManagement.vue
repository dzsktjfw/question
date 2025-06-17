<template>
  <div class="student-management">
    <el-card>
      <!-- 关键：给表格加样式让列自适应，填满容器 -->
      <el-table 
        :data="studentList" 
        stripe 
        style="width: 100%; table-layout: fixed;" 
      >
        <!-- 去掉固定宽度，让列自适应 -->
        <el-table-column 
          prop="username" 
          label="用户名" 
        />
        <el-table-column 
          prop="created_at" 
          label="创建时间" 
        >
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag 
              :type="row.isBlacklisted ? 'danger' : 'success'" 
              size="small"
            >
              {{ row.isBlacklisted ? '黑名单' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- 操作列可根据需求固定宽度，避免压缩变形 -->
        <el-table-column label="操作" width="180"> 
          <template #default="{ row }">
            <el-button
              size="small"
              type="primary"
              @click="handleResetPassword(row.id)"
            >
              重置密码
            </el-button>
            <el-button
              size="small"
              :type="row.isBlacklisted ? 'success' : 'warning'"
              @click="handleToggleBlacklist(row)"
            >
              {{ row.isBlacklisted ? '移出黑名单' : '加入黑名单' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/api/request'

// 学生列表数据
const studentList = ref([])

// 格式化日期显示
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取学生列表
const fetchStudents = async () => {
  try {
    const response = await axios.get('/teacher/look')
    if (response.data.code === 1) {
      studentList.value = response.data.data
    } else {
      ElMessage.error('获取学生列表失败：' + response.data.msg)
    }
  } catch (error) {
    ElMessage.error('获取学生列表出错：' + error.message)
  }
}

// 重置密码操作
const handleResetPassword = (studentId) => {
  ElMessageBox.confirm('确定要重置该学生的密码吗？密码将重置为"111aaa"', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.put(`/teacher/resetpwd/${studentId}`)
      if (response.data.code === 1) {
        ElMessage.success('密码重置成功')
        // 刷新列表
        await fetchStudents()
      } else {
        ElMessage.error('密码重置失败：' + response.data.msg)
      }
    } catch (error) {
      ElMessage.error('密码重置出错：' + error.message)
    }
  }).catch(() => {})
}

// 切换黑名单状态
const handleToggleBlacklist = (student) => {
  const action = student.isBlacklisted ? '移出黑名单' : '加入黑名单'
  const message = student.isBlacklisted 
    ? `确定要将学生 ${student.username} 移出黑名单吗？`
    : `确定要将学生 ${student.username} 加入黑名单吗？`
  
  ElMessageBox.confirm(message, action, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.put(`/teacher/stop/${student.id}`)
      if (response.data.code === 1) {
        ElMessage.success(`${action}操作成功`)
        // 更新本地状态避免重新请求
        student.isBlacklisted = !student.isBlacklisted
      } else {
        ElMessage.error(`${action}操作失败：` + response.data.msg)
      }
    } catch (error) {
      ElMessage.error(`${action}操作出错：` + error.message)
    }
  }).catch(() => {})
}

// 组件挂载时获取学生列表
onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.student-management {
  padding: 20px;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table {
  margin-top: 10px;
}

.el-button + .el-button {
  margin-left: 8px;
}
</style>