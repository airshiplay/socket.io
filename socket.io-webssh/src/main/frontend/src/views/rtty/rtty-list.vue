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
                <!--<a href="javascript:;" @click="() => handleAdd()">-->
                    <!--<a-icon type="plus" style="font-size: 30px;"/>-->
                <!--</a>-->
            </a-col>
            <a-col :span="8">
                <a href="https://github.com/zhaojh329/rtty" target="_blank" style="float: right">Rtty Client Install Notes </a>
            </a-col>
        </a-row>
        <a-table
:columns="columns"
                 :rowKey="record => record.devId"
                 :dataSource="data"
                 :pagination="pagination"
                 :loading="loading"
                 @change="handleTableChange"
        >
            <template slot="Index" slot-scope="text,record, index">
                <span>{{`${index+1}`}}</span>
            </template>
            <template slot="devId" slot-scope="text,record">
                <a href="javascript:;" @click="() => gotoConsole(record)">{{text}}</a>
            </template>
            <!--eslint-disable-next-line-->
            <template slot="uptime" slot-scope="text,record">
                <span>{{UptimeText(text)}}</span>
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
    </div>
</template>

<script>
import {getRttyList} from "@api/api";
/* eslint-disable */
    const columns = [{
        title: 'No',
        width: '10px',
        scopedSlots: {customRender: 'Index'},
    }, {
        title: 'Name',
        dataIndex: 'devId',
        width: '180px',
        scopedSlots: {customRender: 'devId'},
    },
        {
            title: 'Description',
            dataIndex: 'desc',
        },
        //     {
        //     title: 'port',
        //     dataIndex: 'port',
        // },
        {
            title: 'Uptime',
            dataIndex: 'timestamp',
            scopedSlots: {customRender: 'uptime'},
        }
        // , {
        //     title: 'operation',
        //     dataIndex: 'operation',
        //     width: '20px',
        //     scopedSlots: {customRender: 'operation'},
        // }
    ];
    export default {
        name: "rtty-list",
        mounted() {
            this.fetch();
        },
        data() {
            return {
                data: [],
                pagination: false,
                queryText: '',
                loading: false,
                columns,
            }
        },
        methods: {
            UptimeText(value) {
                var duration = Math.floor((new Date().getTime() - value) / 1000)
                var res = ''
                if (duration > 60 * 60 * 24) {
                    res = res + Math.floor(duration / (60 * 60 * 24)) + 'd'
                    duration = duration % (60 * 60 * 24)
                }
                if (duration > 60 * 60) {
                    res = res + Math.floor(duration / (60 * 60)) + 'h'
                    duration = duration % (60 * 60)

                }
                if (duration > 60) {
                    res = res + Math.floor(duration / (60)) + 'm'
                    duration = duration % (60)
                }
                if (duration > 0) {
                    res = res + duration + 's'
                }
                return res
            },
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
                getRttyList({
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
                // this.$refs['device-modal'].showModal();
            }, onDelete(id) {
                // debugger
                // deleteDevice({id}).then(data => {
                //     if (data.success) {
                //         this.fetch();
                //     }
                // })
            }, gotoConsole(record) {
                this.$router.push({name: 'rtty_console', params: {devid: record.devId}});
            },
            editRecord(record) {
                // this.$refs['device-modal'].editModal(record);
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