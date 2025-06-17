<template>
  <div class="faq-container">
    <!-- 搜索区域 -->
    <div class="search-container">
      <el-input
        v-model="searchContent"
        placeholder="输入问题内容进行搜索"
        clearable
        @keyup.enter="handleSearch"
        class="search-input"
      >
        <template #append>
          <el-button
            type="primary"
            icon="Search"
            @click="handleSearch"
          >搜索</el-button>
        </template>
      </el-input>
      <el-button
        type="primary"
        @click="openDialog('add')"
        class="add-button"
      >
        <el-icon><Plus /></el-icon>添加问题
      </el-button>
    </div>

    <!-- 问题表格 -->
    <el-table
      :data="tableData"
      stripe
      border
      width="100%"
      v-loading="loading"
      empty-text="暂无数据"
    >
      <el-table-column prop="content" label="问题内容" min-width="200" />
      <el-table-column prop="answer" label="回答" min-width="250" />
      <el-table-column prop="lastQueryTime" label="最后查询时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.lastQueryTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="queryCount" label="查询次数" width="100" sortable />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="openDialog('edit', row)"
          >编辑</el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(row.questionId)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 完全重写的分页组件 -->
    <div class="custom-pagination">
      <!-- 左侧显示总条数 -->
      <div class="total-display">Total {{ total }}</div>
      
      <!-- 中间页码列表 -->
      <div class="page-list">
        <div 
          class="page-button prev" 
          :class="{ disabled: currentPage === 1 }"
          @click="handlePageChange(currentPage - 1)"
        >&lt;</div>
        
        <div 
          v-for="page in pageCount" 
          :key="page"
          class="page-button" 
          :class="{ 'current-page': currentPage === page }"
          @click="handlePageChange(page)"
        >
          {{ page }}
        </div>
        
        <div 
          class="page-button next" 
          :class="{ disabled: currentPage === pageCount }"
          @click="handlePageChange(currentPage + 1)"
        >&gt;</div>
      </div>
      
      <!-- 右侧跳转功能 -->
      <div class="go-to-container">
        <span class="go-to-label">Go to</span>
        <el-input
          v-model="inputPage"
          size="small"
          type="number"
          min="1"
          :max="pageCount"
          @keyup.enter="goToPage"
          class="go-to-input"
        />
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="问题内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="请输入问题内容"
          />
        </el-form-item>
        <el-form-item label="回答" prop="answer">
          <el-input
            v-model="form.answer"
            type="textarea"
            :rows="6"
            placeholder="请输入回答内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useStore } from 'vuex'

const store = useStore()
const tableData = computed(() => store.getters.getFaqList)
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)
const searchContent = ref('')
const inputPage = ref(1)  // 跳转页码的输入值

// 对话框相关变量
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  questionId: 0,
  content: '',
  answer: ''
})

// 计算最大页码数
const pageCount = computed(() => Math.ceil(total.value / 10))

// 监听currentPage变化，更新跳转输入框的值
watch(currentPage, (newVal) => {
  inputPage.value = newVal
})

// 表单验证规则
const rules = reactive({
  content: [
    { required: true, message: '请输入问题内容', trigger: 'blur' },
    { min: 5, message: '问题内容至少5个字符', trigger: 'blur' }
  ],
  answer: [
    { required: true, message: '请输入回答内容', trigger: 'blur' },
    { min: 10, message: '回答内容至少10个字符', trigger: 'blur' }
  ]
})

// 初始化加载数据
onMounted(() => {
  fetchData()
})

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    await store.dispatch('fetchFaqListByT', currentPage.value)
    total.value = computed(() => store.getters.getPagination).value.total
    inputPage.value = currentPage.value
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 处理分页变化
const handlePageChange = (page) => {
  if (page < 1 || page > pageCount.value) return
  currentPage.value = page
  fetchData()
}

// 跳转到指定页码
const goToPage = () => {
  let page = parseInt(inputPage.value)
  if (isNaN(page) || page < 1) page = 1
  if (page > pageCount.value) page = pageCount.value
  
  if (page !== currentPage.value) {
    currentPage.value = page
    fetchData()
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  store.dispatch('searchFaqsByT', { content: searchContent.value, page: 1 })
}

// 打开对话框
const openDialog = (type, row = null) => {
  if (type === 'add') {
    dialogTitle.value = '添加常见问题'
    form.questionId = 0
    form.content = ''
    form.answer = ''
  } else if (type === 'edit' && row) {
    dialogTitle.value = '编辑常见问题'
    form.questionId = row.questionId
    form.content = row.content
    form.answer = row.answer
  }
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    // 表单验证
    const valid = await formRef.value.validate()
    if (!valid) return
    
    const params = new URLSearchParams()
    params.append('id', form.questionId)
    params.append('content', form.content)
    params.append('answer', form.answer)
    
    const response = await store.dispatch('saveOrUpdateFaq', params)
    console.log(response)
    if (response.code === 1) {
      dialogVisible.value = false
      fetchData()  // 刷新数据
      ElMessage.success('操作成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message)
  }
}

// 删除处理
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该问题吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await store.dispatch('delFaq', id)
      console.log(response)
      if (response.data.code === 1) {
        fetchData()  // 刷新数据
        ElMessage.success('删除成功')
      } else {
        ElMessage.error(response.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('请求失败: ' + error.message)
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 日期格式化
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}
</script>

<style scoped>
.faq-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.search-container {
  display: flex;
  margin-bottom: 20px;
  align-items: center;
  gap: 15px;
}

.search-input {
  flex: 1;
}

.add-button {
  height: 36px;
}

.el-table {
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 自定义分页样式 - 完全根据图片实现 */
.custom-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 8px 16px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.total-display {
  font-size: 14px;
  color: #606266;
}

.page-list {
  display: flex;
  gap: 6px;
}

.page-button {
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  color: #606266;
  user-select: none;
  border: 1px solid #ddd;
  transition: all 0.3s;
}

.page-button:hover {
  color: #409eff;
  border-color: #409eff;
}

.page-button.current-page {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.page-button.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.go-to-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.go-to-label {
  font-size: 14px;
  color: #606266;
}

.go-to-input {
  width: 60px;
}

:deep(.el-textarea) textarea {
  font-family: inherit;
  resize: vertical;
}
</style>