import axios from "@/api/request";

export const getTask=()=>{
    return axios.request({
        url:"/teacher/task",
        method:"get"
    })
}