<template>

            <Card>
                <p slot="title"><Icon type="help-buoy"></Icon>清分结果查询</p>
                <Row>
                    <Col span="24"  align="right">
                    <Select v-model="chargeOff" style="width:200px" :clearable="true">
                        <Option v-for="item in chargetOffData" :value="item.value" :key="item.value">{{ item.label }}
                        </Option>
                    </Select>
                    <DatePicker type="date" placeholder="查询开始日期" style="width: 200px" v-model="bTime_cc"
                                format="yyyy-MM-dd" :clearable="false"></DatePicker>
                    <DatePicker type="date" placeholder="查询结束日期" style="width: 200px" v-model="eTime_cc"
                                format="yyyy-MM-dd" :clearable="false"></DatePicker>
                    <span @click="search_cc" style="margin: 0 10px;">
                        <Button type="primary" icon="search">查询</Button>
                    </span>
                    <span @click="export_cc" style="margin: 0 10px;">
                        <Button type="primary" icon="archive">导出</Button>
                    </span>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" :data="ccList" :columns="tableColums_cc" stripe border></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total_cc" :current="pageNumber_cc" :page-size="pageSize_cc" @on-change="search_cc" show-total
                              show-elevator></Page>
                    </div>
                </div>
            </Card>

</template>

<script>
    import {mapState} from 'vuex'
    import dateKit from '../../libs/date'
    import Input from "iview/src/components/input/input";
    import consts from '../../libs/consts'
    export default {
        computed: {
            ...mapState({
                'ccList': state => state.cc.ccList,
                'totalPage_cc': state => state.cc.totalPage_cc,
                'total_cc': state => state.cc.totalRow_cc,
                'pageNumber_cc': state => state.cc.pageNumber_cc,
                'pageSize_cc': state => state.cc.pageSize_cc,
            })
        },
        data() {
            return {
                now: new Date(),
                self: this,
                chargeOff: '',
                bTime_cc: new Date(),
                eTime_cc: new Date(),
                tableColums_cc: [

                    {
                        title: '商户号',
                        key: 'merNO',
                        fixed: 'left',
                        width: 100
                    },
                    {
                        title: '商户名',
                        key: 'merName',
                        fixed: 'left',
                        width: 100
                    },
                    {
                        title: '清分流水号',
                        key: 'clearNo',
                    },

                    {
                        title: '清分日期',
                        key: 'clearTimeTxt',
                    },
                    {
                        title: '交易笔数',
                        key: 'tradeCount',
                    },
                    {
                        title: '交易金额',
                        key: 'amountSum',
                    },
                    {
                        title: '出账金额',
                        key: 'amountOff',
                    },
                    {
                        title: '交易手续费金额',
                        key: 'amountFeeSum',
                    },
                    {
                        title: '预存抵扣手续费金额',
                        key: 'accountFee',
                    },
                    {
                        title: '交易抵扣手续费金额',
                        key: 'tradeFee',
                    },
                    // {
                    //     title: '出账金额',
                    //     key: 'chargeOff',
                    // },
                    {
                        title: '出账时间',
                        key: 'chargeAtTxt',
                    },
                    {
                        title: '出账单据流水号',
                        key: 'chargeAOffTradeNo',
                    },

                ],
                chargetOffData: [
                    {
                        value: '0',
                        label: '已出账'
                    },
                    {
                        value: '1',
                        label: '未出账'
                    }
                ]

            }
        },
        methods: {
            search_cc() {
                let param = {
                    'bTime': dateKit.formatDate(this.bTime_cc, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime_cc, 'yyyy-MM-dd'),
                    merNO: this.merNO,
                    chargetOff: this.chargeOff
                }
                this.$store.dispatch("cc_list", param);
            },
            export_cc(){
                let param = {
                    'bTime': dateKit.formatDate(this.bTime_cc, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime_cc, 'yyyy-MM-dd'),
                    chargetOff: this.chargeOff
                }
                this.$store.dispatch("cc_list_export_4mer", param).then((res)=>{
                    if(res&&res.resCode&&res.resCode=='success'){
                        let url=consts.devLocation+'/cmn/act02?ePath='+res.resData;
                        window.open(url,'_blank')
                    }else if(res&&res.resCode&&res.resCode=='fail'){
                        // this.$Message.success("导出失败>>"+res.resMsg);
                    }

                });
            }
        },
        components: {Input},
        mounted() {
            this.$store.commit('set_cc_list',[])
        }

    }
</script>

<style lang="less">
    @import '../../styles/common.less';
</style>