import axios from '@/api/request';

export const fetchFaqList = (page: number) => {
  return axios.get('/student/list', { params: { page } });
};

export const searchFaqs = (params: { content: string; page: number }) => {
  return axios.get('/student/query', { params });
};

export const getFaqDetail = (questionId: number) => {
  return axios.get(`/student/detail/${questionId}`);
};

export const delFaq = (questionId: number) => {
  return axios.delete(`/teacher/delete/${questionId}`);
}
;
export const saveOrUpdateFaq = (param: { questionId?: number; content: string; answer: string }) => {{
  return axios.post('/teacher/saveOrUpdate', param);
};
}
export const fetchFaqListByT = (page: number) => {
  return axios.get('/teacher/list', { params: { page } });
};

export const searchFaqsByT = (params: { content: string; page: number }) => {
  return axios.get('/teacher/query', { params });
};