import { createStore } from 'vuex';
import { ElMessage } from 'element-plus';
import { fetchFaqList, searchFaqs, getFaqDetail,delFaq,saveOrUpdateFaq,fetchFaqListByT,searchFaqsByT } from '@/api/question/faq';
import { getTask } from '@/api/question/task';

// FAQ相关类型定义
interface Faq {
  questionId: number;
  content: string;
  answer: string;
  lastQueryTime: string;
  queryCount: number;
}

interface Pagination {
  current: number;
  size: number;
  total: number;
  pages: number;
}

interface SearchParams {
  content: string;
  page: number;
}

// 状态定义
interface State {
  faq: {
    faqList: Faq[];
    faqDetail: Faq | null;
    pagination: Pagination;
    searchParams: SearchParams;
    loading: boolean;
    error: string | null;
  };
  task: number;
}

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


// 创建Vuex store
export default createStore<State>({
  state: {
    faq: {
      faqList: [],
      faqDetail: null,
      pagination: {
        current: 1,
        size: 10,
        total: 0,
        pages: 0,
      },
      searchParams: {
        content: '',
        page: 1,
      },
      loading: false,
      error: null,
    },
    task: 12
  },
  
  getters: {
    // FAQ相关getters
    getFaqList: (state) => state.faq.faqList,
    getFaqDetail: (state) => state.faq.faqDetail,
    getPagination: (state) => state.faq.pagination,
    getSearchParams: (state) => state.faq.searchParams,
    isLoading: (state) => state.faq.loading,
    getError: (state) => state.faq.error,
    getTask: (state) => state.task,
  },
  
  mutations: {
    // FAQ相关mutations
    SET_FAQ_LIST(state, payload) {
      state.faq.faqList = payload.list;
      state.faq.pagination = payload.pagination;
    },
    
    SET_FAQ_DETAIL(state, faq) {
      state.faq.faqDetail = faq;
    },
    
    SET_PAGINATION(state, pagination) {
      state.faq.pagination = pagination;
    },
    
    SET_SEARCH_PARAMS(state, params) {
      state.faq.searchParams = params;
    },
    
    SET_LOADING(state, loading) {
      state.faq.loading = loading;
    },
    
    SET_ERROR(state, error) {
      state.faq.error = error;
    },
    
    RESET_FAQ_STATE(state) {
      state.faq = {
        faqList: [],
        faqDetail: null,
        pagination: {
          current: 1,
          size: 10,
          total: 0,
          pages: 0,
        },
        searchParams: {
          content: '',
          page: 1,
        },
        loading: false,
        error: null,
      };
    },
    SET_TASK(state, task) {
      state.task = task;
    } 
  },
  
  actions: {
    // FAQ相关actions
    async fetchFaqList({ commit }, page) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      
      try {
        const response = await fetchFaqList(page);
        const data = response.data.data;
        
        commit('SET_FAQ_LIST', {
          list: data.records,
          pagination: {
            current: data.current,
            size: data.size,
            total: data.total,
            pages: data.pages,
          },
        });
        
        commit('SET_SEARCH_PARAMS', {
          content: '',
          page: page,
        });
        
        // 成功提示
        // ElMessage({
        //   message: '获取FAQ列表成功',
        //   type: 'success',
        //   duration: 2000,
        // });
        
        return response;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '获取FAQ列表失败';
        commit('SET_ERROR', errorMsg);
        
        // 错误提示
        ElMessage({
          message: errorMsg,
          type: 'error',
          duration: 3000,
        });
        
        console.error('获取FAQ列表失败:', error);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async searchFaqs({ commit }, params) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      
      try {
        const response = await searchFaqs(params);
        const data = response.data.data;
        
        commit('SET_FAQ_LIST', {
          list: data.records,
          pagination: {
            current: data.current,
            size: data.size,
            total: data.total,
            pages: data.pages,
          },
        });
        
        commit('SET_SEARCH_PARAMS', params);
        
        // 成功提示
        ElMessage({
          message: '搜索FAQ成功',
          type: 'success',
          duration: 2000,
        });
        
        return response;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '搜索FAQ失败';
        commit('SET_ERROR', errorMsg);
        
        // 错误提示
        ElMessage({
          message: errorMsg,
          type: 'error',
          duration: 3000,
        });
        
        console.error('搜索FAQ失败:', error);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },

     async fetchFaqListByT({ commit }, page) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      
      try {
        const response = await fetchFaqListByT(page);
        const data = response.data.data;
        
        commit('SET_FAQ_LIST', {
          list: data.records,
          pagination: {
            current: data.current,
            size: data.size,
            total: data.total,
            pages: data.pages,
          },
        });
        
        commit('SET_SEARCH_PARAMS', {
          content: '',
          page: page,
        });
        
        // 成功提示
        // ElMessage({
        //   message: '获取FAQ列表成功',
        //   type: 'success',
        //   duration: 2000,
        // });
        
        return response;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '获取FAQ列表失败';
        commit('SET_ERROR', errorMsg);
        
        // 错误提示
        ElMessage({
          message: errorMsg,
          type: 'error',
          duration: 3000,
        });
        
        console.error('获取FAQ列表失败:', error);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async searchFaqsByT({ commit }, params) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      
      try {
        const response = await searchFaqsByT(params);
        const data = response.data.data;
        
        commit('SET_FAQ_LIST', {
          list: data.records,
          pagination: {
            current: data.current,
            size: data.size,
            total: data.total,
            pages: data.pages,
          },
        });
        
        commit('SET_SEARCH_PARAMS', params);
        
        // 成功提示
        ElMessage({
          message: '搜索FAQ成功',
          type: 'success',
          duration: 2000,
        });
        
        return response;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '搜索FAQ失败';
        commit('SET_ERROR', errorMsg);
        
        // 错误提示
        ElMessage({
          message: errorMsg,
          type: 'error',
          duration: 3000,
        });
        
        console.error('搜索FAQ失败:', error);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async getFaqDetail({ commit }, questionId) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      
      try {
        const response = await getFaqDetail(questionId);
        const faq = response.data.data;
        
        commit('SET_FAQ_DETAIL', faq);
        
        // 成功提示
        ElMessage({
          message: '获取FAQ详情成功',
          type: 'success',
          duration: 2000,
        });
        
        return response;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '获取FAQ详情失败';
        commit('SET_ERROR', errorMsg);
        
        // 错误提示
        ElMessage({
          message: errorMsg,
          type: 'error',
          duration: 3000,
        });
        
        console.error('获取FAQ详情失败:', error);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    resetFaqState({ commit }) {
      commit('RESET_FAQ_STATE');
    },

    async delFaq({ commit }, questionId, currentPage = 1) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      try {
        const response = await delFaq(questionId);
          
        return response;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '删除FAQ失败';
        commit('SET_ERROR', errorMsg);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    async saveOrUpdateFaq({ commit }, param) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      
      try {
        const response = await saveOrUpdateFaq(param);
        const data = response.data;
        
        return data;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '保存或更新FAQ失败';
        commit('SET_ERROR', errorMsg);
        
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    async getTask({ commit }) {
      commit('SET_LOADING', true);
      commit('SET_ERROR', null);
      
      try {
        const response = await getTask();
        const data = response.data.data;
        
        commit('SET_TASK', data);
        
        // 成功提示
        ElMessage({
          message: '获取任务成功',
          type: 'success',
          duration: 2000,
        });
        
        return response;
      } catch (error: any) {
        const errorMsg = error.response?.data?.message || '获取任务失败';
        commit('SET_ERROR', errorMsg);
        
        // 错误提示
        ElMessage({
          message: errorMsg,
          type: 'error',
          duration: 3000,
        });
        
        console.error('获取任务失败:', error);
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    }

  },
  
  modules: {
    // 这里可以添加其他模块
  },
});
