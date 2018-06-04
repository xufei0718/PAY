<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <div>
                    <div>
                        <Row>
                            <Col span="6" align="left">
                            <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                            </Col>
                            <Col span="18" align="right">
                            <Select v-model="finalCode" style="width: 120px; text-align: center;" placeholder="最终处理结果">
                                <Option v-for="item in finalCodeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                            </Select>
                            <DatePicker type="date" placeholder="开始日期" style="width: 200px" v-model="bTime" format="yyyy-MM-dd" :transfer="true" :clearable="false"></DatePicker>
                            <DatePicker type="date" placeholder="结束日期" style="width: 200px" v-model="eTime" format="yyyy-MM-dd" :transfer="true" :clearable="false"></DatePicker>
                            <span @click="search" style="margin: 0 10px;">
                                <Button type="primary" icon="search">搜索</Button>
                            </span>
                            </Col>
                        </Row>
                    </div>

                    <Row class="margin-top-10">
                        <Table border="false" :data="headBatchTradeList" :columns="tableColums" stripe></Table>
                    </Row>
                    <div style="margin: 10px;overflow: hidden">
                        <div style="float: right;">
                            <Page :total="total" :current="pageNumber" :page-size="pageSize" @on-change="search" show-total show-elevator></Page>
                        </div>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import dateKit from '../../../libs/date'

    export default {
        computed: {
            ...mapState({
                'headBatchTradeList': state => state.collBatch.headBatchTradeList,
                'totalPage': state => state.collBatch.totalPage,
                'pageNumber': state => state.collBatch.pageNumber,
                'total': state => state.collBatch.totalRow,
                'collTrade': state => state.collBatch.collTrade,
            })
        },
        methods: {
            search(pn) {
                let param = {
                    finalCode: this.finalCode,
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                    pn: pn,
                    ps: this.pageSize
                }
                this.$store.dispatch('batch_trade_list', param)
            },
            refresh() {
                let param = {
                    finalCode: this.finalCode,
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                    search: this.searchKey,
                    ps: this.pageSize
                }
                this.$store.dispatch('batch_trade_list', param)
            },
        },
        components: {
        },
        mounted() {
            let param = {
                finalCode: this.finalCode,
                'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                search: this.searchKey,
                ps: this.pageSize
            }
            this.$store.dispatch('batch_trade_list', param)
        },
        data() {
            return {
                finalCode: '',
                bTime: new Date(),
                eTime: new Date(),
                searchKey: '',
                pageSize: 30,
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
                    {
                        value: '3',
                        label: '待初始化'
                    },
                ],
                tableColums: [
                    {
                        title: '系统商户号',
                        key: 'merId',
                        align: 'center',
                        minWidth: 150
                    },
                    {
                        title: '订单发送时间',
                        key: 'txnTime',
                        align: 'center',
                        minWidth: 150
                    },
                    {
                        title: '批次号',
                        key: 'batchNo',
                        align: 'center',
                        minWidth: 100,
                    },
                    {
                        title: '交易总金额',
                        key: 'totalAmt',
                        align: 'center',
                        minWidth: 120,
                    },
                    {
                        title: '交易比数',
                        key: 'totalQty',
                        align: 'center',
                    },
                    {
                        title: '最终处理结果',
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
                                    trigger: 'hover',
                                    //title: '结果详情',
                                    placement: 'top',
                                    width: '100%'
                                }
                            }, [
                                    h('Button', {
                                        props: {
                                            type: statusType,
                                            size: 'small',
                                            long: true,
                                            color: color,
                                        },
                                        style: {
                                            width: '80px'
                                        },
                                    }, finalStatus),
                                    h('div', {
                                        slot: 'content'
                                    }, [
                                            h('p', [
                                                h('span', {
                                                    style: {
                                                        'margin-right': '10px',
                                                    }
                                                }, '发起交易'),
                                                h('strong', {
                                                    style: {
                                                        'margin-right': '10px',
                                                    }
                                                }, row.respCode),
                                                h('strong', row.respMsg),
                                            ]),
                                            h('p', [
                                                h('span', {
                                                    style: {
                                                        'margin-right': '10px',
                                                    }
                                                }, '交易结果'),
                                                h('strong', {
                                                    style: {
                                                        'margin-right': '10px',
                                                    }
                                                }, row.resultCode),
                                                h('strong', row.resultMsg),
                                            ]),
                                        ])
                                ]);
                        },
                        align: 'center',
                        minWidth: 120,
                    },
                    {
                        title: '成功金额',
                        key: 'successAmt',
                        align: 'center',
                        minWidth: 120,
                    },
                    {
                        title: '成功笔数',
                        key: 'successQty',
                        align: 'center',
                        minWidth: 120,
                    },
                    {
                        title: '创建时间',
                        key: 'cat',
                        align: 'center',
                        minWidth: 150
                    },
                    {
                        title: '修改时间',
                        key: 'mat',
                        align: 'center',
                        minWidth: 150
                    },
                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>