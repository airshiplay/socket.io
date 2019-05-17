import { axios } from '@/utils/request'

//post
export function postAction(url,parameter) {
    return axios({
        url: url,
        method:'post' ,
        data: parameter
    })
}

//post method= {post | put}
export function httpAction(url,parameter,method) {
    return axios({
        url: url,
        method:method ,
        data: parameter
    })
}

//put
export function putAction(url,parameter) {
    return axios({
        url: url,
        method:'put',
        data: parameter
    })
}

//get
export function getAction(url,parameter) {
    return axios({
        url: url,
        method: 'get',
        params: parameter
    })
}

//deleteAction
export function deleteAction(url,parameter) {
    return axios({
        url: url,
        method: 'delete',
        params: parameter
    })
}

/**
 * 下载文件 用于excel导出
 * @param url
 * @param parameter
 * @returns {*}
 */
export function downFile(url,parameter){
    return axios({
        url: url,
        params: parameter,
        method:'get' ,
        responseType: 'blob'
    })
}