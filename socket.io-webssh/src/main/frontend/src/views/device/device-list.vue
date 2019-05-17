<template>
    <div class="home">
        <a-button class="editable-add-btn" @click="handleAdd">Add</a-button>
        <a-button shape="circle" icon="search" @click="handleSearch" style="margin-left: 8px"/>
        <a-table :columns="columns"
                 :rowKey="record => record.id"
                 :dataSource="data"
                 :pagination="pagination"
                 :loading="loading"
                 @change="handleTableChange"
        >
            <template slot="Index" slot-scope="text,record, index">
                <span>{{`${index+1}`}}</span>
            </template>
            <template slot="name" slot-scope="text,record">
                <a href="javascript:;" @click="() => gotoConsole(record)">{{text}}</a>
            </template>
            <template slot="operation" slot-scope="text, record">
                <a-popconfirm
                        v-if="data.length"
                        title="Sure to delete?"
                        @confirm="() => onDelete(record.id)">
                    <a href="javascript:;">Delete</a>
                </a-popconfirm>
            </template>
        </a-table>
        <device-modal ref="device-modal" @on-result="onResult"/>
    </div>
</template>

<script>
    import {deleteDevice, getDeviceList} from "@api/api";
    import DeviceModal from "./device-modal";
    /* eslint-disable */
    const columns = [{
        title: 'No',
        scopedSlots: {customRender: 'Index'},
    }, {
        title: 'Name',
        dataIndex: 'name',
        width: '20%',
        scopedSlots: {customRender: 'name'},
    }, {
        title: 'IP',
        dataIndex: 'ip',
        width: '20%',
    }, {
        title: 'port',
        dataIndex: 'port',
    }, {
        title: 'username',
        dataIndex: 'username',
    }, {
        title: 'operation',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'},
    }];
    export default {
        name: "device-list",
        components: {DeviceModal},
        mounted() {
            this.fetch();
        },
        data() {
            return {
                data: [],
                pagination: {},
                loading: false,
                columns,
            }
        },
        methods: {
            handleTableChange(pagination, filters, sorter) {
                const pager = {...this.pagination};
                pager.current = pagination.current;
                this.pagination = pager;
                this.fetch({
                    pageSize: pagination.pageSize,
                    pageNum: pagination.current,
                    sortField: sorter.field,
                    sortOrder: sorter.order,
                    ...filters,
                });
            },
            handleSearch() {
                this.fetch();
            },
            fetch(params = {}) {
                // console.log('params:', params);
                this.loading = true
                getDeviceList({
                    ...params,
                }).then((data) => {
                    const pagination = {...this.pagination};
                    // Read total count from server
                    // pagination.total = data.totalCount;
                    pagination.total = data.total;
                    this.loading = false;
                    this.data = data.content;
                    this.pagination = pagination;
                });
            }, handleAdd() {
                this.$refs['device-modal'].showModal();
            }, onDelete(id) {
                debugger
                deleteDevice({id}).then(data => {
                    if (data.success) {
                        this.fetch();
                    }
                })
            }, gotoConsole(record) {
                this.$router.push({name: 'console', params: {id: record.id}});
            }, onResult() {
                this.fetch();
            },reload(){

            }
        },
    }
</script>

<style scoped>
    .editable-add-btn {
        margin-bottom: 8px;
    }
</style>