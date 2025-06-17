<template>
  <div class="faq-detail-container">
    <el-button 
      type="primary" 
      link 
      icon="ArrowLeft" 
      @click="goBack"
      class="back-button"
    >
      返回问题列表
    </el-button>
    
    <el-card class="faq-card" v-if="faqDetail">
      <template #header>
        <div class="card-header">
          <el-icon class="question-icon"><QuestionFilled /></el-icon>
          <h2>{{ faqDetail.content }}</h2>
        </div>
      </template>
      
      <div class="faq-meta">
        <div class="meta-item">
          <span class="meta-label">最后查询时间：</span>
          <span>{{ formatDate(faqDetail.lastQueryTime) }}</span>
        </div>
        <div class="meta-item">
          <span class="meta-label">查询次数：</span>
          <el-tag type="info">{{ faqDetail.queryCount }}</el-tag>
        </div>
      </div>
      
      <el-divider />
      
      <div class="answer-container">
        <h3>问题解答：</h3>
        <div class="answer-content">
          {{ faqDetail.answer }}
        </div>
      </div>
    </el-card>
    
    <el-empty v-else description="未找到该问题详情" />
  </div>
</template>

<script setup>
import { ref, onMounted,computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { QuestionFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const store = useStore()

// 从Vuex获取数据
const faqDetail = computed(() => store.getters.getFaqDetail)
const loading = computed(() => store.getters.isLoading)

// 本地状态
const questionId = ref(route.params.id)

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 返回问题列表
const goBack = () => {
  router.push('/dashboard')
}

// 加载问题详情
const loadFaqDetail = async () => {
  try {
    await store.dispatch('getFaqDetail', questionId.value)
  } catch (error) {
    console.error('加载FAQ详情失败:', error)
    router.push('/dashboard')
  }
}

// 监听路由变化，加载新的详情
watch(() => route.params.id, (newId) => {
  questionId.value = newId
  loadFaqDetail()
})

// 初始化加载
onMounted(() => {
  loadFaqDetail()
})
</script>

<style scoped>
.faq-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.back-button {
  margin-bottom: 20px;
  display: inline-flex;
  align-items: center;
}

.faq-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.question-icon {
  font-size: 28px;
  color: #409eff;
}

.faq-meta {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-label {
  font-weight: 500;
  color: #606266;
}

.answer-container {
  padding: 10px 0;
}

.answer-content {
  margin-top: 15px;
  line-height: 1.8;
  white-space: pre-line;
}
</style>
