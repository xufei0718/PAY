<template>
    <div>
        <Row>
            <Col span="24">
            <Row>
                <Col span="6">
                <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                </Col>
                <Col span="18" align="right">
                <Select v-model="txnType" style="width: 120px; text-align: center;">
                    <Option v-for="item in txnTypeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>
                <DatePicker type="date" placeholder="开始日期" style="width: 200px" v-model="bTime" format="yyyy-MM-dd" :clearable="false"></DatePicker>
                <DatePicker type="date" placeholder="结束日期" style="width: 200px" v-model="eTime" format="yyyy-MM-dd" :clearable="false"></DatePicker>
                <Input v-model="searchKey" placeholder="请输入..." style="width: 200px" />
                <span @click="search" style="margin: 0 10px;">
                    <Button type="primary" icon="search">搜索</Button>
                </span>
                </Col>
            </Row>
            <Row class="margin-top-10">
                <Table border :data="entrustTradeList" :columns="tableColums" stripe width="100%"></Table>
            </Row>
            <div style="margin: 10px;overflow: hidden">
                <div style="float: right;">
                    <Page :page-size="pageSize" :total="total" :current="pageNumber" @on-change="search" show-total show-elevator></Page>
                </div>
            </div>
            </Col>
        </Row>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import dateKit from '../../../libs/date'
    export default {
        name: 'entrustTradePaneContent',
        computed: {
            ...mapState({
                'entrustTradeList': state => state.unionpayEntrust.entrustTradeList,
                'totalPage': state => state.unionpayEntrust.totalPage,
                'pageNumber': state => state.unionpayEntrust.pageNumber,
                'total': state => state.unionpayEntrust.totalRow,
                'unionpayEntrust': state => state.unionpayEntrust.unionpayEntrust,
            })
        },
        methods: {
            search(pn) {
                let param = {
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                    search: this.searchKey,
                    pn: pn,
                    ps: this.pageSize,
                    txnType: this.txnType,
                }
                this.$store.dispatch('get_entrust_trade_list', param)
            },
            refresh() {
                let param = {
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                    search: this.searchKey,
                    pn: this.pageNumber,
                    ps: this.pageSize,
                    txnType: this.txnType,
                }
                this.$store.dispatch('get_entrust_trade_list', param)
            },
        },
        mounted() {
            let param = {
                'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                search: this.searchKey,
                ps: this.pageSize,
                txnType: this.txnType,
            }
            this.$store.dispatch('get_entrust_trade_list', param)
        },
        data() {
            return {
                bTime: new Date(),
                eTime: new Date(),
                searchKey: '',
                pageSize: 30,
                txnType: '0',
                txnTypeList: [
                    {
                        value: '',
                        label: '全部'
                    },
                    {
                        value: '0',
                        label: '建立委托关系'
                    },
                    {
                        value: '1',
                        label: '解除委托关系'
                    }
                ],
                tableColums: [
                    {
                        title: '姓名',
                        key: 'customerNm',
                        align: 'center',
                        minWidth: 80,
                    },
                    {
                        title: '证件号码',
                        key: 'certifId',
                        align: 'center',
                        minWidth: 180,
                    },
                    {
                        title: '卡号',
                        key: 'accNo',
                        align: 'center',
                        minWidth: 180,
                    },
                    {
                        title: '手机号',
                        key: 'phoneNo',
                        align: 'center',
                        minWidth: 150,
                    },
                    {
                        title: '订单号',
                        key: 'orderId',
                        align: 'center',
                        minWidth: 200,
                    },
                    {
                        title: '应答码',
                        key: 'respCode',
                        align: 'center',
                        minWidth: 80,
                    },
                    {
                        title: '应答信息',
                        key: 'respMsg',
                        align: 'center',
                        minWidth: 220,
                    },
                    {
                        title: '商户',
                        key: 'merId',
                        align: 'center',
                        minWidth: 180,
                    },
                    {
                        title: '创建时间',
                        key: 'cat',
                        align: 'center',
                        minWidth: 150,
                    }
                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>