<template>
    <!-- 默认展示的欢迎内容 -->
    <div  class="welcome-container">
        <div class="welcome-card">
            <div class="welcome-header">
                <h1>欢迎使用教师端管理系统</h1>
                <p>请从左侧菜单选择功能开始工作</p>
            </div>
            <div class="welcome-stats">
                <el-row :gutter="20">
                    <el-col :span="6">
                        <div class="stat-card">
                            <div class="stat-icon" style="background-color: #e6f7ff;">
                                <el-icon color="#1890ff" :size="30">
                                    <User />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ board.studentCount }}</div>
                                <div class="stat-label">管理学生数</div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="stat-card">
                            <div class="stat-icon" style="background-color: #f6ffed;">
                                <el-icon color="#52c41a" :size="30">
                                    <ChatDotRound />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ board.todayAnswer }}</div>
                                <div class="stat-label">今日答疑数</div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="stat-card">
                            <div class="stat-icon" style="background-color: #fff7e6;">
                                <el-icon color="#fa8c16" :size="30">
                                    <Notebook />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ board.questionCount }}</div>
                                <div class="stat-label">问题库数量</div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="stat-card">
                            <div class="stat-icon" style="background-color: #fff0f6;">
                                <el-icon color="#eb2f96" :size="30">
                                    <Clock />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ board.taskCount }}</div>
                                <div class="stat-label">待处理问题</div>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <br><br><br><br>
            <!-- 新增饼图区域 -->
            <div class="chart-container">
                <div ref="pieChart" style="width: 100%; height: 400px;"></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import axios from '@/api/request';
import { ElMessage } from 'element-plus';
import { ref,onMounted } from 'vue';
import * as echarts from 'echarts'; // 引入ECharts
const board=ref({})
const pieChart = ref(null); // 饼图DOM引用
// 饼图配置项
const pieOptions = ref({
    title: {
        text: '数据统计概览',
        subtext: '各项指标占比分布',
        left: 'center'
    },
    tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: ['管理学生数', '今日答疑数', '问题库数量', '待处理问题']
    },
    series: [
        {
            name: '数据统计',
            type: 'pie',
            radius: ['30%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
            },
            label: {
                show: true,
                formatter: '{b}\n{c}'
            },
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            },
            data: [] // 初始为空，数据加载后填充
        }
    ]
});
async function getBoardData() {
    try {
        const teacher_id= JSON.parse(sessionStorage.getItem('token'))?.id || JSON.parse(localStorage.getItem('token'))?.id || '0';
        const response = await axios.get('/teacher/welcome',{params: { teacher_id }});
        if (response.data.code===0) {
            ElMessage.error(response.data.msg || '获取数据失败');
        }
        board.value = response.data.data || {};
        // 更新饼图数据
        updatePieChartData();
    } catch (error) {
        ElMessage.error("未知错误，请联系管理员")
    }
}
function updatePieChartData() {
    if (!board.value || !pieChart.value) return;
    
    pieOptions.value.series[0].data = [
        { value: board.value.studentCount || 0, name: '管理学生数', itemStyle: { color: '#1890ff' }},
        { value: board.value.todayAnswer || 0, name: '今日答疑数', itemStyle: { color: '#52c41a' }},
        { value: board.value.questionCount || 0, name: '问题库数量', itemStyle: { color: '#fa8c16' }},
        { value: board.value.taskCount || 0, name: '待处理问题', itemStyle: { color: '#eb2f96' }}
    ];
    
    // 渲染饼图
    const chart = echarts.init(pieChart.value);
    chart.setOption(pieOptions.value);
    
    // 响应窗口大小变化
    window.addEventListener('resize', () => chart.resize());
}
onMounted(() => {
    getBoardData();
});
</script>
<style>
.welcome-container {
  padding: 30px;
}

.welcome-card {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.welcome-header {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.welcome-header p {
  font-size: 16px;
  color: #666;
}

/* 统计卡片样式 */
.stat-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 最近问题区域 */
.recent-questions {
  margin-top: 40px;
}

.recent-questions h2 {
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
</style>