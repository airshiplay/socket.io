<template>
    <div class="home">
        <a-row type="flex" justify="space-between" style="margin: 4px">
            <a-col :span="8">
                <a-input-search
@change="handleChange"
@pressEnter="handleChange"
v-model="queryText"
                                placeholder="input search text"
                                style="width: 200px;"
                                @search="handleSearch"
                />
            </a-col>
            <a-col :span="8">
                <a href="javascript:;" @click="() => handleAdd()" style="float: right;">
                    <a-icon type="plus" style="font-size: 30px;"/>
                </a>
            </a-col>
        </a-row>
        <a-table
:columns="columns"
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
                <a href="javascript:;" @click="() => editRecord(record)">
                    <a-icon type="edit"/>
                </a>
                <a-popconfirm
                        v-if="data.length"
                        title="Sure to delete?"
                        @confirm="() => onDelete(record.id)">
                    <a href="javascript:;" style="margin-left: 8px">
                        <a-icon type="delete"/>
                    </a>
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
        width: '10px',
        scopedSlots: {customRender: 'Index'},
    }, {
        title: 'Name',
        dataIndex: 'name',
        width: '180px',
        scopedSlots: {customRender: 'name'},
    },
        {
            title: 'IP',
            dataIndex: 'ip',
            width: '100px',
        },
        {
            title: 'Description',
            dataIndex: 'desc',
        },
        {
            title: 'operation',
            dataIndex: 'operation',
            width: '20px',
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
                pagination: {showSizeChanger: true, showQuickJumper: true},
                queryText: '',
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
                    pageSize: pager.pageSize,
                    pageNum: pager.current,
                    sortField: sorter.field,
                    sortOrder: sorter.order,
                    ...filters,
                });
            }, handleChange(e) {
                this.fetch({
                    pageSize: this.pagination.pageSize,
                    pageNum: 1,
                    query: this.queryText,
                });
            },
            handleSearch(value, event) {
                this.fetch({
                    pageSize: this.pagination.pageSize,
                    pageNum: 1,
                    query: value,
                });
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
            },
            editRecord(record) {
                this.$refs['device-modal'].editModal(record);
            }
            , onResult() {
                this.fetch();
            }, reload() {

            }
        },
    }
</script>

<style scoped>
    .editable-add-btn {
        margin-bottom: 8px;
    }
</style>