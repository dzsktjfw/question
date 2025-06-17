import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import Login from '../components/Login.vue'
import { ElMessage } from 'element-plus'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'login',
    component: Login,
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../components/Register.vue'),
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/student/Dashboard.vue'),
    meta: { requiresAuth: true }, // 需要登录
    children: [
      // 可以添加嵌套路由（如需）
    ]
  },
    {
    path: '/faq/detail/:id',
    name: 'FaqDetail',
    component: () => import('@/views/student/FaqDetail.vue'),
    meta: { requiresAuth: true }, // 需要登录
    props: true
  },{
    path: '/menu',
    name: 'Menu',
    component: () => import('@/views/teacher/TeacherMenu.vue'),
    meta: { requiresAuth: true }, // 需要登录
    children: [
      // 嵌套路由 - 教师工作台子页面
      {
        path: 'question-bank',
        name: 'QuestionBank',
        component: () => import('@/views/teacher/QuestionBank.vue'),
      },
      {
        path: 'answer-center',
        name: 'AnswerCenter',
        component: () => import('@/views/teacher/AnswerCenter.vue'),
      },
      {
        path: 'student-management',
        name: 'StudentManagement',
        component: () => import('@/views/teacher/StudentManagement.vue'),
      },
      {
        path: 'my-answers',
        name: 'MyAnswers',
        component: () => import('@/views/teacher/MyAnswers.vue'),
      },
      {
        path: 'welcome',
        name: 'welcome',
        component: () => import('@/views/teacher/Welcome.vue'),
      },
      {
        path:'',
        redirect: '/menu/welcome' // 默认重定向到欢迎页面
      }
    ]
  }
]


const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  // 1. 获取认证状态（从localStorage/sessionStorage）
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  const isAuthenticated = token

  // 2. 处理需要认证的页面
  if (to.name !== 'login' && to.name !== 'register' && !isAuthenticated) {
    ElMessage.warning('请先登录系统')
    next({
      path: '/'
    })
  }
  // 4. 其他情况正常放行
  else {
    next()
  }
})

export default router
