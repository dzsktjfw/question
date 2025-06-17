<template>
  <div class="teacher-qa-container">
    <!-- 顶部筛选区域 -->
    <div class="filter-container">
      <div class="filter-left">
        <el-switch
          v-model="onlyUnresolved"
          active-text="只看未解决"
          inactive-text="显示全部"
          active-color="#ff6b6b"
          @change="fetchQuestions"
        />
      </div>
      <div class="filter-right">
        <el-button type="primary" :icon="Refresh" @click="fetchQuestions">
          刷新
        </el-button>
      </div>
    </div>

    <!-- 问题列表 -->
    <el-table 
      :data="questions" 
      stripe
      style="width: 100%"
      v-loading="loading"
      empty-text="暂无待回答问题"
    >
      <el-table-column prop="asker" label="提问人" width="120" align="center" />
      
      <el-table-column prop="askTime" label="提问时间" width="180" align="center">
        <template #default="{ row }">
          {{ formatTime(row.askTime) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="content" label="提问内容" min-width="250">
        <template #default="{ row }">
          <div class="question-content">{{ row.content }}</div>
        </template>
      </el-table-column>
      
      <el-table-column prop="answer" label="回答" min-width="250">
        <template #default="{ row }">
          <div v-if="row.answer">
            <div class="answer-content">{{ row.answer }}</div>
            <div class="answer-meta">
              解答人: {{ row.teacherName || '未填写' }}
            </div>
          </div>
          <el-tag v-else type="danger" effect="plain">待回答</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="isOk" label="解决状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isOk ? 'success' : 'danger'">
            {{ row.isOk ? '已解决' : '未解决' }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="answerTime" label="回答时间" width="180" align="center">
        <template #default="{ row }">
          {{ row.answerTime ? formatTime(row.answerTime) : '-' }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            size="small" 
            @click="openAnswerDialog(row)"
          >
            {{ row.answer ? '修改' : '回答' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

   <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        :total="totalCount"
        @size-change="fetchQuestions"
        @current-change="fetchQuestions"
      />
    </div>

    <!-- 回答对话框 -->
    <el-dialog
      v-model="answerDialogVisible"
      :title="`回答学生提问`"
      width="50%"
      :close-on-click-modal="false"
    >
      <el-form :model="currentQuestion" label-width="80px">
        <el-form-item label="提问内容">
          <div class="question-preview">{{ currentQuestion.content }}</div>
          <div class="question-meta">
            <span>提问人: {{ currentQuestion.asker }}</span>
            <span>{{ formatTime(currentQuestion.ask_time) }}</span>
          </div>
        </el-form-item>
        
        <el-form-item label="回答内容" prop="answer">
          <el-input
            v-model="currentQuestion.answer"
            type="textarea"
            :rows="6"
            placeholder="请输入您的解答"
            clearable
          />
        </el-form-item>

        <!-- 状态自动标记区域 -->
        <el-form-item label="状态标记">
          <el-alert type="info" :closable="false">
            提交回答后问题将<strong class="status-alert">自动标记为已解决</strong>
          </el-alert>
          <div class="status-note">
            如需保持未解决状态，请留空回答内容并点击取消
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="answerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAnswer">提交回答</el-button>
      </template>
    </el-dialog>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import axios from '@/api/request';
import { useStore } from 'vuex';
const store = useStore();

const teacherName = ref(JSON.parse(sessionStorage.getItem('token'))?.username||JSON.parse(localStorage.getItem('token'))?.username || '教师用户');
const teacherId = ref(JSON.parse(sessionStorage.getItem('token'))?.id||JSON.parse(localStorage.getItem('token'))?.id || '0');

// 数据相关
const questions = ref([]);
const loading = ref(false);
const onlyUnresolved = ref(true);
const currentPage = ref(1);
const pageSize = ref(10); // 固定每页10条
const totalCount = ref(0);

// 对话框相关
const answerDialogVisible = ref(false);
const currentQuestion = ref({
  question_id: null,
  content: '',
  answer: '',
  is_ok: 0
});

// 获取问题列表
const fetchQuestions = async () => {
  try {
    loading.value = true;
    
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      only: onlyUnresolved.value ? 1 : 0,
    };
    
    const response = await axios.get('/teacher/show', { params });
    if (response.data.code === 1) {
      
      questions.value = response.data.data.records;
      totalCount.value = response.data.data.total;
    } else {
      ElMessage.error(response.data.message || '获取问题列表失败');
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  } finally {
    loading.value = false;
  }
};

// 打开回答对话框
const openAnswerDialog = (question) => {
  currentQuestion.value = { ...question };
  console.log('打开回答对话框', currentQuestion.value.questionId);
  answerDialogVisible.value = true;
};

// 提交回答
const submitAnswer = async () => {
  try {
    if (!currentQuestion.value.answer) {
      ElMessage.warning('请填写回答内容');
      return;
    }
    
    const params = {
      id:  Number(currentQuestion.value.questionId),
      answer: currentQuestion.value.answer,
      teacherId: Number(teacherId.value),
      teacherName: teacherName.value
    };
    const response = await axios.put('/teacher/answer', params);
    
    if (response.data.code === 1) {
      ElMessage.success('回答已提交');
      answerDialogVisible.value = false;
      fetchQuestions();
      store.dispatch('getTask')
    } else {
      ElMessage.error(response.data.message || '提交回答失败');
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};


// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return '';
  const date = new Date(timeString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-');
};

// 初始化加载数据
onMounted(() => {
  fetchQuestions();
});
</script>

<style scoped>
.teacher-qa-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8fafc;
  border-radius: 8px;
}

.filter-right {
  display: flex;
  gap: 10px;
}

.question-content {
  line-height: 1.6;
  font-weight: 500;
}

.answer-content {
  line-height: 1.6;
  padding: 8px 0;
}

.question-preview {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-table .cell) {
  padding-top: 12px;
  padding-bottom: 12px;
}

:deep(.el-switch) {
  margin-left: 10px;
}
</style>