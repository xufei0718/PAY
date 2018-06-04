<template>
    <div>
        <Row>
            <Col span="24">
            <div>
                <div>
                    <Row>
                        <Col span="4" align="left">
                        <Button type="primary" icon="person-add" @click="initiate">发起交易</Button>
                        <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                        </Col>
                        <Col span="20" align="right">
                        <div>
                            <template v-if="isAdmin">
                                <Input v-model="merSearchKey" placeholder="请输入商户编号/名称/负责人名称..." style="width: 240px" />
                            </template>
                            <Select v-model="finalCode" style="width: 120px; text-align: center;" placeholder="最终处理结果" :transfer="true">
                                <Option v-for="item in finalCodeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                            </Select>
                            <Select v-model="clearStatus" style="width: 100px; text-align: center;" placeholder="清分状态" :transfer="true">
                                <Option v-for="item in clearStatusCodeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                            </Select>
                            <DatePicker type="date" placeholder="开始日期" style="width: 200px" v-model="bTime" format="yyyy-MM-dd" :transfer="true" :clearable="false"></DatePicker>
                            <DatePicker type="date" placeholder="结束日期" style="width: 200px" v-model="eTime" format="yyyy-MM-dd" :transfer="true" :clearable="false"></DatePicker>
                            <Input v-model="searchKey" placeholder="请输入客户姓名/身份证号/手机号/卡号..." style="width: 240px" />
                            <div style="display: inline-block;">
                                <Button @click="search" type="primary" icon="search">搜索</Button>
                                <Button @click="exportDetail" type="primary" icon="archive">导出</Button>
                            </div>
                        </div>
                        </Col>
                    </Row>
                </div>
                <Row class="margin-top-10">
                    <Table :border="false" :data="tradeList" :columns="tableColumns" :row-class-name="rowClassName" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" :page-size="pageSize" @on-change="search" show-total show-elevator></Page>
                    </div>
                </div>
            </div>
            </Col>
        </Row>
        <addForm ref="aem" :pageSize="pageSize" :finalCode="finalCode" :bTime="bTime" :eTime="eTime" :merSearchKey="merSearchKey"
            :searchKey="searchKey" :clearStatus="clearStatus"></addForm>
    </div>
</template>

<script>
    import Vue from 'vue'
    import { mapState } from 'vuex'
    import dateKit from '../../../libs/date'
    import consts from '../../../libs/consts'
    import addTradeModal from './addTradeForm.vue'
    import FinalCodeContent from './finalCodeContent.vue'

    export default {
        computed: {
            ...mapState({
                'tradeList': state => state.collTrade.tradeList,
                'totalPage': state => state.collTrade.totalPage,
                'pageNumber': state => state.collTrade.pageNumber,
                'total': state => state.collTrade.totalRow,
                'collTrade': state => state.collTrade.collTrade,
                'isAdmin': state => state.collTrade.isAdmin,
            }),
            highlightIndex: {
                get: function () {
                    return this.$store.state.collTrade.highlightIndex
                },
                set: function (newValue) {
                    this.$store.state.collTrade.highlightIndex = newValue
                }
            },
        },
        methods: {
            search(pn) {
                let param = {
                    finalCode: this.finalCode,
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                    merSearchKey: this.merSearchKey,
                    search: this.searchKey,
                    clearStatus: this.clearStatus,
                    pn: pn,
                    ps: this.pageSize
                }
                this.isShow = {};
                this.highlightIndex = -1;
                this.$store.dispatch('trade_list', param);
                this.getTableColums();
            },
            refresh() {
                let param = {
                    finalCode: this.finalCode,
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                    merSearchKey: this.merSearchKey,
                    search: this.searchKey,
                    clearStatus: this.clearStatus,
                    ps: this.pageSize
                }
                this.isShow = {};
                this.highlightIndex = -1;
                this.$store.dispatch('trade_list', param);
                this.getTableColums();
            },
            initiate() {
                this.$refs.aem.open();
            },
            rowClassName(row, index) {
                if (this.highlightIndex === index) {
                    return 'highlight';
                } else {
                    return '';
                }
            },
            exportDetail(){
                let param = {
                    finalCode: this.finalCode,
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                    merSearchKey: this.merSearchKey,
                    search: this.searchKey,
                    clearStatus: this.clearStatus,
                    ps: this.pageSize
                }
                this.$store.dispatch("trade_detail_export", param).then((res)=>{
                    if(res&&res.resCode&&res.resCode=='success'){
                        let url=consts.devLocation+'/cmn/act02?ePath='+res.resData;
                        window.open(url,'_blank');
                    }else if(res&&res.resCode&&res.resCode=='fail'){
                        
                    }
                });
            },
            getTableColums() {
                const tableColumnList = {
                    // {
                    //     title: '详情',
                    //     key: 'action',
                    //     width: 60,
                    //     align: 'center',
                    //     fixed: 'right',
                    //     render: (h, params) => {
                    //         const row = params.row;
                    //         return h('div', {
                    //             style: {
                    //                 'padding-left': '0px',
                    //                 'padding-right': '0px',
                    //             }
                    //         }, [
                    //                 h('a', {
                    //                     style: {
                    //                         display: 'inline-block',
                    //                         width: '100%',
                    //                     },
                    //                     on: {
                    //                         click: () => {
                    //                             // this.remove(params.index)
                    //                         }
                    //                     }
                    //                 }, [
                    //                         h('Icon', {
                    //                             props: {
                    //                                 type: 'chevron-right',
                    //                                 size: 18
                    //                             }
                    //                         })
                    //                     ]),
                    //             ]);
                    //     }
                    // },
                    index: {
                        type: 'index',
                        width: 60,
                        align: 'center'
                    },
                    tradeTime: {
                        title: '交易时间',
                        key: 'tradeTime',
                        align: 'center',
                        minWidth: 180,
                        render: function (h, params) {
                            return h('span', dateKit.formatDate(new Date(params.row.tradeTime), 'yyyy-MM-dd hh:mm:ss'));
                        }
                    },
                    tradeNo: {
                        title: '交易流水号',
                        key: 'tradeNo',
                        align: 'center',
                        minWidth: 255,
                    },
                    merchantNo: {
                        title: '商户号',
                        key: 'merchantNo',
                        align: 'center',
                        minWidth: 100,
                        render: (h, params) => {
                            const row = params.row;
                            const merchantNo = row.merchantInfo.merchantNo;
                            return h('span', merchantNo);
                        },
                    },
                    bussType: {
                        title: '业务类型',
                        key: 'bussType',
                        render: (h, params) => {
                            const row = params.row;
                            const type = row.bussType === '1' ? '加急' : '批量';
                            return h('span', type);
                        },
                        align: 'center',
                        minWidth: 100,
                    },
                    amount: {
                        title: '交易金额',
                        key: 'amount',
                        align: 'center',
                        minWidth: 100,
                    },
                    bankFee: {
                        title: '银行手续费',
                        key: 'bankFee',
                        align: 'center',
                        minWidth: 100,
                    },
                    merFee: {
                        title: '商户手续费',
                        key: 'merFee',
                        align: 'center',
                        minWidth: 100,
                    },
                    finalCode: {
                        title: '处理结果',
                        key: 'finalCode',
                        render: (h, params) => {
                            const row = params.row;
                            var finalStatus = '';
                            var statusType = '';
                            var color = ''
                            if (row.finalCode === '0') {
                                finalStatus = '成功'
                                statusType = 'success'
                                color = '#19be6b';//绿色
                            } else if (row.finalCode === '1') {
                                finalStatus = '处理中'
                                statusType = 'primary'
                                color = '#2d8cf0';//蓝色
                            } else if (row.finalCode === '2') {
                                finalStatus = '失败'
                                statusType = 'error'
                                color = '#ed3f14';//红色
                            }
                            return h('Poptip', {
                                props: {
                                    trigger: 'click',
                                    placement: 'bottom',
                                    transfer: true,
                                    width: '100%'
                                },
                                on: {
                                    'on-popper-show': () => {
                                        Vue.set(this.isShow, params.index, true);
                                    },
                                    'on-popper-hide': () => {
                                        Vue.set(this.isShow, params.index, false);
                                    }
                                }
                            }, [
                                    h('div', {
                                        style: {
                                            'min-width': '80px',
                                            color: color,
                                            cursor: 'pointer',
                                            padding: '7px 5px'
                                        },
                                    }, [
                                            h('span', {
                                                style: {
                                                    'margin-right': '6px',
                                                }
                                            }, finalStatus),
                                            h('div', {
                                                class: {
                                                    'cell-td-expand': true,
                                                    'cell-td-expand-expanded': this.isShow[params.index]
                                                },
                                                style: {
                                                    'display': 'inline-block'
                                                }
                                            }, [
                                                    h('Icon', {
                                                        props: {
                                                            type: 'chevron-right',
                                                        }
                                                    })
                                                ]),
                                        ]
                                    ),
                                    h(FinalCodeContent, {
                                        slot: 'content',
                                        props: {
                                            index: params.index,
                                            tradeInfo: row,
                                            isShow: this.isShow
                                        },
                                        style: {
                                            padding: '0px'
                                        }
                                    }),
                                ]);
                        },
                        align: 'center',
                        minWidth: 120,
                    },
                    custName: {
                        title: '客户姓名',
                        key: 'custName',
                        align: 'center',
                        minWidth: 100,
                    },
                    cardID: {
                        title: '身份证号',
                        key: 'cardID',
                        align: 'center',
                        minWidth: 160,
                    },
                    bankcardNo: {
                        title: '银行卡号',
                        key: 'bankcardNo',
                        align: 'center',
                        minWidth: 180,
                    },
                    mobileBank: {
                        title: '手机号',
                        key: 'mobileBank',
                        align: 'center',
                        minWidth: 160,
                    },
                    clearStatus: {
                        title: '清分状态',
                        key: 'clearStatus',
                        render: (h, params) => {
                            const row = params.row;
                            var clearStatus = '';
                            if (row.clearStatus === '0') {
                                clearStatus = '已清分'
                            } else if (row.clearStatus === '1') {
                                clearStatus = '未清分'
                            }
                            return h('span', clearStatus);
                        },
                        align: 'center',
                        minWidth: 100,
                    },
                    clearDate: {
                        title: '清分时间',
                        key: 'clearDate',
                        align: 'center',
                        minWidth: 180,
                        render: function (h, params) {
                            var clearDate = params.row.clearDate ? dateKit.formatDate(new Date(params.row.clearDate), 'yyyy-MM-dd hh:mm:ss') : '';
                            return h('span', clearDate);
                        }
                    },
                    mat: {
                        title: '修改时间',
                        key: 'mat',
                        align: 'center',
                        minWidth: 180,
                        render: function (h, params) {
                            return h('span', dateKit.formatDate(new Date(params.row.mat), 'yyyy-MM-dd hh:mm:ss'));
                        }
                    }
                };

                let data = [];
                if (this.isAdmin) {
                    this.adminTableColumnNames.forEach(col => data.push(tableColumnList[col]));
                } else {
                    this.tableColumnNames.forEach(col => data.push(tableColumnList[col]));
                }
                this.tableColumns = data;
            },
        },
        components: {
            addForm: addTradeModal,
            FinalCodeContent: FinalCodeContent
        },
        mounted() {
            let param = {
                finalCode: this.finalCode,
                'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                merSearchKey: this.merSearchKey,
                search: this.searchKey,
                clearStatus: this.clearStatus,
                ps: this.pageSize
            }
            this.isShow = {};
            this.$store.dispatch('trade_list', param)
        },
        data() {
            return {
                isShow: {},
                clearStatus: '',
                finalCode: '',
                bTime: new Date(),
                eTime: new Date(),
                merSearchKey: '',
                searchKey: '',
                pageSize: 30,
                clearStatusCodeList: [
                    {
                        value: '',
                        label: '全部',
                    },
                    {
                        value: '0',
                        label: '已清分'
                    },
                    {
                        value: '1',
                        label: '未清分'
                    },
                ],
                finalCodeList: [
                    {
                        value: '',
                        label: '全部',
                    },
                    {
                        value: '0',
                        label: '成功'
                    },
                    {
                        value: '1',
                        label: '处理中'
                    },
                    {
                        value: '2',
                        label: '失败'
                    },
                ],
                adminTableColumnNames: ['index', 'tradeTime', 'merchantNo', 'tradeNo', 'bussType', 'amount', 'bankFee', 'merFee', 'finalCode', 'custName', 'cardID', 'bankcardNo', 'mobileBank', 'clearStatus', 'clearDate', 'mat'],
                tableColumnNames: ['index', 'tradeTime', 'tradeNo', 'bussType', 'amount', 'merFee', 'finalCode', 'custName', 'cardID', 'bankcardNo', 'mobileBank', 'clearStatus', 'clearDate', 'mat'],
                tableColumns: []
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
    @import './tc.less';
</style>