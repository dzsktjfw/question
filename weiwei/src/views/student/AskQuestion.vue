<template>
  <div class="qa-container">
    <!-- 顶部操作栏 -->
    <div class="header">
      <h1>人工答疑</h1>
      <div class="actions">
        <el-checkbox v-model="onlyMine" @change="loadQuestions">只看我的提问</el-checkbox>
      </div>
    </div>

    <!-- 提问表单 -->
    <el-card class="question-form">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您的问题..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitQuestion">提交问题</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 问题列表 -->
    <div class="question-list">
      <el-table 
        :data="questions" 
        v-loading="loading" 
        style="width: 100%"
        @row-click="handleRowClick"
      >
        <el-table-column prop="content" label="问题内容" min-width="200">
          <template #default="{ row }">
            <div class="question-content">
              {{ row.content }}
              <el-tag v-if="row.isOk === 1" size="small" type="success" effect="dark">已解决</el-tag>
              <el-tag v-else size="small" type="warning" effect="dark">待解答</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="asker" label="提问人" width="120" />
        <el-table-column prop="askTime" label="提问时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.askTime) }}</template>
        </el-table-column>
        <!-- 新增回答教师列 -->
        <el-table-column prop="teacherName" label="回答教师" width="120">
          <template #default="{ row }">
            {{ row.teacherName || '无' }}
          </template>
        </el-table-column>
        <!-- 新增回答时间列 -->
        <el-table-column prop="answerTime" label="回答时间" width="180">
          <template #default="{ row }">
            {{ row.answerTime ? formatDateTime(row.answerTime) : '无' }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        layout="prev, pager, next, jumper"
        @current-change="loadQuestions"
      />
    </div>

    <!-- 问题详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="问题详情"
      width="50%"
    >
      <div class="question-detail">
        <div class="detail-section">
          <h3>问题内容</h3>
          <div class="detail-content">{{ currentQuestion?.content }}</div>
        </div>
        
        <div class="detail-info">
          <div><strong>提问人：</strong>{{ currentQuestion?.asker }}</div>
          <div><strong>提问时间：</strong>{{ formatDateTime(currentQuestion?.askTime) }}</div>
          <div><strong>状态：</strong>
            <el-tag v-if="currentQuestion?.isOk === 1" type="success" size="small">已解决</el-tag>
            <el-tag v-else type="warning" size="small">待解答</el-tag>
          </div>
        </div>
        
        <div class="detail-section" v-if="currentQuestion?.answer">
          <h3>教师回答</h3>
          <div class="detail-content">{{ currentQuestion.answer }}</div>
          
          <div class="answer-info">
            <div><strong>回答教师：</strong>{{ currentQuestion.teacherName }}</div>
            <div><strong>回答时间：</strong>{{ formatDateTime(currentQuestion.answerTime) }}</div>
          </div>
        </div>
        
        <div class="detail-section" v-else>
          <h3>教师回答</h3>
          <div class="no-answer">暂无回答</div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from '@/api/request';
import  formatDateTime  from '@/utils/timeUtil';

// 问题接口
interface Question {
  questionId: number;
  asker: string;
  content: string;
  askTime: string;
  answer: string | null;
  teacherName: string | null;
  answerTime: string | null;
  teacherId: number | null;
  isOk: number; // 0-未解决, 1-已解决
}

// 状态管理
const loading = ref(false);
const onlyMine = ref(false);
const formRef = ref();
const form = reactive({
  content: ''
});

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
});

// 问题列表
const questions = ref<Question[]>([]);

// 详情对话框相关
const detailDialogVisible = ref(false);
const currentQuestion = ref<Question | null>(null);

// 表单验证规则
const rules = {
  content: [
    { required: true, message: '请输入问题内容', trigger: 'blur' },
    { min: 10, message: '问题内容至少10个字符', trigger: 'blur' }
  ]
};

// 获取当前登录用户信息
const getCurrentUser = () => {
  const tokenSession = sessionStorage.getItem('token');
  const tokenLocal = localStorage.getItem('token');
  let username = '';
  
  if (tokenSession) {
    try {
      const parsedTokenSession = JSON.parse(tokenSession);
      username = parsedTokenSession.username || '';
    } catch (e) {
      console.error('解析sessionStorage中的token失败', e);
    }
  } else if (tokenLocal) {
    try {
      const parsedTokenLocal = JSON.parse(tokenLocal);
      username = parsedTokenLocal.username || '';
    } catch (e) {
      console.error('解析localStorage中的token失败', e);
    }
  }
  
  return username;
};

// 加载问题列表
const loadQuestions = async () => {
  try {
    loading.value = true;
    
    const params = {
      page: pagination.current,
      size: pagination.size,
      asker: '', // 默认不筛选提问人
    };
    
    // 如果勾选了"只看我的"，添加asker参数
    if (onlyMine.value) {
      const user = getCurrentUser();
      if (user) {
        params.asker = user;
      } else {
        ElMessage.warning('无法获取用户信息，将显示全部问题');
      }
    }
    
    const response = await axios.get('/student/show', { params });
    questions.value = response.data.data.records;
    pagination.total = response.data.data.total;
  } catch (error) {
    ElMessage.error('加载问题列表失败');
    console.error('加载问题列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 提交问题
const submitQuestion = async () => {
  try {
    // 表单验证
    await formRef.value.validate();
    
    const user = getCurrentUser();
    
    if (!user) {
      ElMessage.error('无法获取用户信息，请重新登录');
      return;
    }
    
    const questionData = {
      content: form.content,
      asker: user
    };
    
    await axios.post('/student/add', questionData);
    
    ElMessage.success('问题提交成功');
    form.content = ''; // 清空表单
    
    // 重新加载问题列表
    loadQuestions();
  } catch (error: any) {
    if (error.response?.data?.message) {
      ElMessage.error(`提交失败: ${error.response.data.message}`);
    } else {
      ElMessage.error('提交问题失败，请稍后重试');
    }
    console.error('提交问题失败:', error);
  }
};

// 处理行点击事件
const handleRowClick = (row: Question) => {
  currentQuestion.value = row;
  detailDialogVisible.value = true;
};

// 初始化加载问题
onMounted(() => {
  loadQuestions();
});
</script>

<style scoped>
.qa-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.question-form {
  margin-bottom: 30px;
}

.question-content {
  line-height: 1.6;
  .el-tag {
    margin-left: 10px;
    vertical-align: middle;
  }
}

.question-list {
  margin-top: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: center;
}

/* 详情对话框样式 */
.question-detail {
  line-height: 1.8;
}

.detail-section {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.detail-section h3 {
  margin-bottom: 10px;
  color: #409EFF;
}

.detail-content {
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 10px;
}

.detail-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
}

.answer-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-top: 10px;
}

.no-answer {
  color: #909399;
  font-style: italic;
  padding: 10px;
}
</style>