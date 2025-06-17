import axios from "@/api/request";

export const updatedPwd =(param:{id:number,oldPassword:string,newPassword:string}) =>{
    return axios.post('/teacher/updatepwd', param);
}

export const updateInfo = (param: {}) => {
    return axios.put('/teacher/updateinfo', param);
}

export const getTeacherInfo = (id: number) => {
    return axios.get(`/teacher/info/${id}`);
}