import {deleteAction, getAction, postAction, putAction} from '@/api/manage'

const getDeviceList = (params) => getAction("/api/device/list", params);
const addDevice = (params) => postAction("/api/device/add", params);
const editDevice = (params) => putAction("/api/device/edit", params);
const deleteDevice = (params) => deleteAction("/api/device/delete", params);

const getRttyList = (params) => getAction("/api/rtty/list", params);

export {
    getDeviceList,
    addDevice,
    editDevice,
    deleteDevice,
    getRttyList
}