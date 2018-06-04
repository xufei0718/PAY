<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    手续费明细列表
                </p>
                <Row v-if="isOper">

                    <Col span="24"  align="right">
                    <Input v-model="searchKey"  placeholder="输入商户编号/名称" style="width: 200px"/>
                    <span @click="search" >
                        <Button type="primary" icon="search">搜索</Button></span>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="feeAmountList" :columns="tableColums" stripe></Table>
                </Row>
                <Row class="margin-top-10">
                    <Col span="24" align="right">
                    <Page  :total="total" :current="pageNumber"  :page-size="pageSize" @on-change="search" show-total show-elevator ></Page>
                    </Col>
                </Row>
            </Card>
            </Col>
        </Row>


    </div>

</template>

<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'



    export default {

        computed: {
            ...mapState({
                'feeAmountList': state => state.merInfo.feeAmountList,
                'totalPage': state => state.merInfo.totalPage,
                'pageNumber': state => state.merInfo.pageNumber,
                'pageSize': state => state.merInfo.pageSize,
                'total': state => state.merInfo.totalRow,
                'isOper': state => state.merInfo.isOper,
            })
        },
        methods: {

            search(pn) {
                this.$store.dispatch('merInfo_listFeeAmount', {search: this.searchKey, pn: pn})
            },
            refresh() {
                this.$store.dispatch('merInfo_listFeeAmount', {search: this.searchKey})
            },

        },
        mounted() {
            //页面加载时或数据方法
            this.$store.dispatch('merInfo_listFeeAmount')

        },
        data() {
            return {

                self: this,
                searchKey: '',
                tableColums: [

                    {
                        title: '商户名称',
                        key: 'merName',
                        align: 'center',
                    },
                    {
                        title: '商户编号',
                        key: 'merNo',
                        align: 'center',


                    },
                    {
                        title: '变动金额',
                        key: 'amount',
                        align: 'right',
                        render: (h, param) => {
                            if (param.row.type === '1') {
                                return  h('div', {
                                    style: {

                                        color:'#2d8cf0',
                                        fontWeight:'bold'
                                    }
                                },['+'+param.row.amount]);

                            }else{
                                return  h('div', {
                                    style: {
                                        color:'#ff9900',
                                        fontWeight:'bold'
                                }
                                },['-'+param.row.amount]);
                            }
                        }

                    },
                    {
                        title: '手续费余额',
                        key: 'aAmount',
                        align: 'right',

                    },

                    {
                        title: '变动方式',
                        key: 'typeTxt',
                        align: 'center',

                    },

                    {
                        title: '变动时间',
                        key: 'catTxt',
                        align: 'center',

                    },
                    {
                        title: '操作员姓名',
                        key: 'operName',
                        align: 'center',

                    },



                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';


</style>