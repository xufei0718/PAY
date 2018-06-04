<template>


            <Card>
                <Tabs >
                    <TabPane label="清分详细查询" icon="help-buoy">
                        <Row>
                            <Col span="24"  align="right">
                            <Select v-model="chargeOff" style="width:200px" :clearable="true">
                                <Option v-for="item in chargetOffData" :value="item.value" :key="item.value">{{ item.label }}
                                </Option>
                            </Select>
                            <Input v-model="merNO" placeholder="商户号" style="width: 200px"></Input>
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
                            <Table :context="self" :data="ccList" :columns="tableColums_cc" :row-class-name="rowClassName_cc"  border></Table>
                        </Row>
                        <div style="margin: 10px;overflow: hidden">
                            <div style="float: right;">
                                <Page :total="total_cc" :current="pageNumber_cc" :page-size="pageSize_cc" @on-change="search_cc" show-total
                                      show-elevator></Page>
                            </div>
                        </div>
                    </TabPane>
                    <TabPane label="清分汇总查询" icon="help-buoy" style="min-height: 400px">
                <Row>
                    <Col span="24"  align="right">
                    <DatePicker type="date" placeholder="查询开始日期" style="width: 200px" v-model="bTime"
                                format="yyyy-MM-dd" :clearable="false"></DatePicker>
                    <DatePicker type="date" placeholder="查询结束日期" style="width: 200px" v-model="eTime"
                                format="yyyy-MM-dd" :clearable="false"></DatePicker>
                    <span @click="search" style="margin: 0 10px;">
                        <Button type="primary" icon="search">查询</Button>
                    </span>
                    <span @click="export_cct" style="margin: 0 10px;">
                        <Button type="primary" icon="archive">导出</Button>
                    </span>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" :row-class-name="rowClassName" :data="cctList" :columns="tableColums"  border>
                        <!--<div slot="footer">Use a card with a shadow effect</div>-->
                    </Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="search" :page-size="pageSize" show-total
                              show-elevator></Page>
                    </div>
                </div>
               </TabPane>

                    <TabPane label="手动清分处理" icon="help-buoy" v-if="enableHmClear()">
                        <Row>
                            <Col span="8" offset="8">
                            <Form :label-width="80" inline>
                                <FormItem>
                                    <DatePicker type="date" placeholder="请选择清分日期" style="width: 200px"
                                                v-model="clearDate"></DatePicker>

                                </FormItem>
                                <FormItem>
                                    <Button type="primary" @click="hmClear">提交</Button>
                                </FormItem>
                            </Form>
                            </Col>
                        </Row>
                    </TabPane>
                </Tabs>
            </Card>

</template>

<script>
    import {mapState} from 'vuex'
    import dateKit from '../../libs/date'
    import Input from "iview/src/components/input/input";
    import consts from '../../libs/consts'
    import Cookies from 'js-cookie';
    import kit from '../../libs/kit';

    export default {
        computed: {
            ...mapState({
                'cctList': state => state.cc.cctList,
                'ccList': state => state.cc.ccList,
                'totalPage': state => state.cc.totalPage,
                'total': state => state.cc.totalRow,
                'pageSize': state => state.cc.pageSize,
                'pageNumber': state => state.cc.pageNumber,
                'totalPage_cc': state => state.cc.totalPage_cc,
                'total_cc': state => state.cc.totalRow_cc,
                'pageSize_cc': state => state.cc.pageSize_cc,
                'pageNumber_cc': state => state.cc.pageNumber_cc,
            })
        },
        data() {
            return {
                now: new Date(),
                self: this,
                bTime: new Date(),
                eTime: new Date(),
                merNO: '',
                chargeOff: '',
                clearDate: '',
                bTime_cc: new Date(),
                eTime_cc: new Date(),
                tableColums: [

                    {
                        title: '清分日期',
                        key: 'cleartotleTimeTxt',
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
                        title: '银行代收手续费金额',
                        key: 'bankFee',
                    },
                    {
                        title: '利润',
                        key: 'profit',
                    },

                ],
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
                        width: 150
                    },

                    {
                        title: '清分日期',
                        key: 'clearTimeTxt',
                        width: 100
                    },
                    {
                        title: '交易笔数',
                        key: 'tradeCount',
                        width: 100
                    },
                    {
                        title: '交易金额',
                        key: 'amountSum',
                        width: 100
                    },
                    {
                        title: '交易手续费金额',
                        key: 'amountFeeSum',
                        width: 100
                    },
                    {
                        title: '银行代收手续费金额',
                        key: 'bankFee',
                        width: 100
                    },
                    {
                        title: '利润',
                        key: 'profit',
                        width: 100
                    },
                    {
                        title: '预存抵扣手续费金额',
                        key: 'accountFee',
                        width: 100
                    },
                    {
                        title: '交易抵扣手续费金额',
                        key: 'tradeFee',
                        width: 100
                    },
                    {
                        title: '出账金额',
                        key: 'amountOff',
                        width: 100
                    },
                    {
                        title: '收款卡号',
                        key: 'bankNo',
                        width:150
                    },
                    {
                        title: '户名',
                        key: 'bankAccountName',
                        width: 100
                    },

                    {
                        title: '开户行',
                        key: 'bankName',
                        width:200
                    },
                    {
                        title: '预留手机号',
                        key: 'bankPhone',
                        width:100
                    },
                    {
                        title: '行号',
                        key: 'bankCode',
                        width: 100
                    },
                    {
                        title: '出账时间',
                        key: 'chargeAtTxt',
                        width: 100,
                        fixed: 'right',
                    },
                    {
                        title: '出账单据流水号',
                        key: 'chargeOffTradeNo',
                        width: 100,
                        fixed: 'right',
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
            hmClear() {
                if (this.clearDate == '') {
                    this.$Message.error('请填写要清分的日期');
                    return;
                } else {
                    this.$Modal.confirm({
                        title: '提示',
                        content: '请再次确认要执行手动清分操作吗？',
                        onOk: () => {
                            this.$store.dispatch('hmClear')
                        },
                        onCancel: () => {

                        }
                    });


                }
            },
            search() {

                let param = {
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd')
                }
                this.$store.dispatch("cct_list", param);
            }
            ,
            search_cc() {
                let param = {
                    'bTime': dateKit.formatDate(this.bTime_cc, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime_cc, 'yyyy-MM-dd'),
                    merNO: this.merNO,
                    chargeOff: this.chargeOff
                }
                this.$store.dispatch("cc_list", param);
            },
            export_cct(){
                let param = {
                    'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd')
                }
                this.$store.dispatch("cct_list_export",param).then((res)=>{
                    if(res&&res.resCode&&res.resCode=='success'){
                        let url=consts.devLocation+'/cmn/act02?ePath='+res.resData;
                        window.open(url,'_blank')
                    }else if(res&&res.resCode&&res.resCode=='fail'){
                        // this.$Message.success("导出失败>>"+res.resMsg);
                    }

                })
            },
            export_cc(){
                let param = {
                    'bTime': dateKit.formatDate(this.bTime_cc, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime_cc, 'yyyy-MM-dd'),
                    merNO: this.merNO,
                    chargeOff: this.chargeOff
                }
                this.$store.dispatch("cc_list_export", param).then((res)=>{
                    if(res&&res.resCode&&res.resCode=='success'){
                        let url=consts.devLocation+'/cmn/act02?ePath='+res.resData;
                        window.open(url,'_blank')
                    }else if(res&&res.resCode&&res.resCode=='fail'){
                        // this.$Message.success("导出失败>>"+res.resMsg);
                    }

                });
            },
            rowClassName (row, index) {
                // if (index === this.cctList.length-1) {
                //     return 'demo-table-error-row';
                // }
                return '';
            },
            rowClassName_cc (row, index) {
                // if (index === this.ccList.length-1) {
                //     return 'demo-table-error-row';
                // }
                return '';
            },
            enableHmClear(){

                let ma = Cookies.get('serviceArray');
                if(ma!=undefined)ma= eval ("(" + ma+ ")")
                return kit.arrayContains(ma,'/cclear/hmClear');


            }


        },
        components: {Input},
        mounted() {

            this.$store.commit('set_cct_list',[])
            this.$store.commit('set_cc_list',[])
        }

    }
</script>

<style lang="less">
    @import '../../styles/common.less';
    .ivu-table .demo-table-error-row td{
        background-color: #ff6600;
        color: #fff;
    }
</style>