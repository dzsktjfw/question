<template>
  <div class="faq-container">
    <!-- 搜索区域 -->
    <div class="search-box">
      <el-input
        v-model="searchKeyword"
        placeholder="输入关键词搜索问题..."
        clearable
        class="search-input"
        @keyup.enter="searchFaq"
      >
        <template #append>
          <el-button :icon="Search" @click="searchFaq">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 问题列表 -->
    <el-table 
      :data="faqList" 
      v-loading="loading"
      style="width: 100%"
      @row-click="handleRowClick"
      class="faq-table"
    >
      <el-table-column label="问题内容" min-width="400">
        <template #default="scope">
          <div class="question-content">
            <el-icon><QuestionFilled /></el-icon>
            <span>{{ scope.row.content }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="最后查询时间" width="180" align="center">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.lastQueryTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="查询次数" width="120" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.queryCount > 100 ? 'success' : 'info'">
            {{ scope.row.queryCount }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="scope">
          <el-button 
            type="primary" 
            link 
            @click.stop="viewDetail(scope.row.questionId)"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalItems"
        layout="total, prev, pager, next, jumper"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { Search, QuestionFilled } from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()
console.log('store', store)
// 从Vuex获取数据
const faqList = computed(() => store.getters.getFaqList)
const pagination = computed(() => store.getters.getPagination)
const loading = computed(() => store.getters.isLoading)
const searchParams = computed(() => store.getters.getSearchParams)

// 本地状态
const searchKeyword = ref(searchParams.value.content)
const currentPage = ref(searchParams.value.page)
const pageSize = ref(10)
const totalItems = computed(() => pagination.value.total)

const formatDateTime = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const seconds = date.getSeconds().toString().padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 搜索问题
const searchFaq = async () => {
  try {
    await store.dispatch('searchFaqs', {content: searchKeyword.value, page: 1})
    currentPage.value = 1
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

// 分页改变
const handlePageChange = async (newPage) => {
  try {
    currentPage.value = newPage
    await store.dispatch('fetchFaqList', newPage)
  } catch (error) {
    console.error('切换页面失败:', error)
  }
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/faq/detail/${id}`)
}

// 点击行查看详情
const handleRowClick = (row) => {
  viewDetail(row.questionId)
}

// 初始化加载数据
onMounted(async () => {
  try {
    await store.dispatch('fetchFaqList', currentPage.value)
  } catch (error) {
    console.error('加载FAQ列表失败:', error)
  }
})
</script>

<style scoped>
.faq-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.search-box {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.search-input {
  width: 80%;
  max-width: 600px;
}

.faq-table {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.question-content {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-table__row) {
  cursor: pointer;
  transition: background-color 0.3s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}
</style>
