<template>
  <div class="qa-record-container">
    <!-- 表格区域 -->
    <el-table 
      :data="qaList" 
      style="width: 100%"
      height="calc(100vh - 220px)"
      v-loading="loading"
      :empty-text="qaList.length === 0 ? '暂无答疑记录' : ''"
    >
      <!-- 提问人列 -->
      <el-table-column prop="asker" label="提问人" width="120" align="center" />
      
      <!-- 提问时间列 -->
      <el-table-column prop="askTime" label="提问时间" width="180" align="center">
        <template #default="{ row }">
          {{ formatDate(row.askTime) }}
        </template>
      </el-table-column>
      
      <!-- 提问内容列 -->
      <el-table-column prop="content" label="提问内容" min-width="200">
        <template #default="{ row }">
          <div class="question-content">{{ row.content }}</div>
        </template>
      </el-table-column>
      
      <!-- 教师回答列 -->
      <el-table-column prop="answer" label="我的回答" min-width="200">
        <template #default="{ row }">
          <div class="answer-content">{{ row.answer }}</div>
        </template>
      </el-table-column>
      
      <!-- 回答时间列 -->
      <el-table-column prop="answerTime" label="回答时间" width="180" align="center">
        <template #default="{ row }">
          {{ row.answerTime ? formatDate(row.answerTime) : '-' }}
        </template>
      </el-table-column>
    </el-table>

    <!-- 底部提示区域 -->
    <div class="table-footer">
      <!-- 显示总共答疑记录数 -->
      <div class="total-count">总共答疑记录数: {{ total }}</div>
      
      <!-- 加载状态提示 -->
      <div v-if="isLoadingMore" class="loading-more">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <!-- 没有更多数据提示 -->
      <div v-else-if="!isLoadingMore && allLoaded" class="no-more">
        我们也是有底线的
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import axios from '@/api/request'

// 获取教师ID
const teacherId = ref(null)
try {
  const token = JSON.parse(sessionStorage.getItem('token')) || JSON.parse(localStorage.getItem('token'))
  teacherId.value = token?.id || null
} catch (error) {
  console.error('获取教师ID失败:', error)
}

// 数据相关
const qaList = ref([])
const total = ref(0)
const loading = ref(false)
const isLoadingMore = ref(false)
const allLoaded = ref(false)
const pageSize = 10 // 每次加载的记录数
let currentOffset = 0

// 获取答疑记录
const getQaList = async () => {
  if (!teacherId.value) {
    ElMessage.error('无法获取教师ID')
    return
  }
  
  try {
    loading.value = true
    const response = await axios.get('/teacher/myanswers', {
      params: {
        teacher_id: teacherId.value,
        limit: pageSize,
        offset: currentOffset
      }
    })
    
    if (response.data.code === 1) {
      const newData = response.data.data.records || []
      qaList.value = [...qaList.value, ...newData]
      total.value = response.data.data.total || 0
      
      // 判断是否还有更多数据
      if (newData.length < pageSize) {
        allLoaded.value = true
      }
    } else {
      ElMessage.error('获取答疑记录失败: ' + (response.data.msg || '未知错误'))
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message)
  } finally {
    loading.value = false
    isLoadingMore.value = false
  }
}

// 滚动加载更多
const handleScroll = () => {
  if (isLoadingMore.value || allLoaded.value) return
  
  // 判断是否滚动到底部(距离底部100px时触发)
  const scrollPosition = window.innerHeight + window.pageYOffset
  const pageHeight = document.body.scrollHeight || document.documentElement.scrollHeight
  if (pageHeight - scrollPosition < 100) {
    loadMore()
  }
}

// 加载更多数据
const loadMore = () => {
  if (allLoaded.value) return
  
  isLoadingMore.value = true
  currentOffset += pageSize
  getQaList()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '--'
  
  try {
    const date = new Date(dateString)
    if (isNaN(date)) return '--'
    
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).replace(/\//g, '-')
  } catch (e) {
    return '--'
  }
}

// 初始化加载数据
onMounted(() => {
  getQaList()
  window.addEventListener('scroll', handleScroll)
})

// 清理事件监听
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.qa-record-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  height: calc(100vh - 180px);
  display: flex;
  flex-direction: column;
}

:deep(.el-table__body-wrapper) {
  overflow-y: auto;
}

.table-footer {
  margin-top: 20px;
  padding: 10px;
  text-align: center;
}

.total-count {
  text-align: right;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
  color: #909399;
}

.no-more {
  padding: 15px 0;
  color: #909399;
  border-top: 1px solid #ebeef5;
}

.question-content,
.answer-content {
  line-height: 1.6;
  padding: 5px 0;
}
</style>