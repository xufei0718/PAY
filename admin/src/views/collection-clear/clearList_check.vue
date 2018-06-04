<template>
    <div>

                <Card>
                    <p slot="title"><Icon type="help-buoy"></Icon>清分审批处理</p>
                    <Row>
                        <Col span="24"  align="right">
                        <Select v-model="chargeCheck" style="width:200px" :clearable="true" placeholder="审核状态">
                            <Option v-for="item in chargeCheckData" :value="item.value" :key="item.value">{{ item.label }}
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
                        </Col>
                    </Row>
                    <Row class="margin-top-10">
                        <Table :context="self" :data="ccList" :columns="tableColums_cc"   border></Table>
                    </Row>
                    <div style="margin: 10px;overflow: hidden">
                        <div style="float: right;">
                            <Page :total="total_cc" :current="pageNumber_cc" :page-size="pageSize_cc" @on-change="search_cc" show-total
                                  show-elevator></Page>
                        </div>
                    </div>

                </Card>

        <Modal
                v-model="chargeCheckModal"
                @on-visible-change="vChange"
                :mask-closable="false"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>出账审核</span>
            </p>
            <Form ref="formValidate" :label-width="80" :model="collectionClear" :rules="ruleValidate">
                <FormItem label="审批结果" >
                    <RadioGroup v-model="ccStatus">
                        <Radio label="通过" true-value="0"></Radio>
                        <Radio label="未通过" true-value="1"></Radio>
                    </RadioGroup>
                </FormItem>
                <FormItem label="审批备注" >
                    <Input v-model="chargeCheckBak" type="textarea" :rows="4" placeholder="请输入未通过审核原因..."></Input>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="chargeCheckSave">保存</Button>
                <Button type="error" @click="chargeCheckModal=false">关闭</Button>
            </div>
        </Modal>
        <Modal
                v-model="viewTradesModal"
                @on-visible-change="vChange"
                :mask-closable="false"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{}}交易明细</span>
            </p>
            <Row class="margin-top-10">
                <Table :context="self" :data="tradeList" :columns="tableColums_trade"  border></Table>
            </Row>
            <div slot="footer">
                <Button type="error" @click="chargeCheckModal=false">关闭</Button>
            </div>
        </Modal>


    </div>

</template>

<script>
    import {mapState} from 'vuex'
    import dateKit from '../../libs/date'
    import Input from "iview/src/components/input/input";
    import consts from '../../libs/consts'
    const chargeCheckDialog=(vm,h,param)=>{
        return h('Button', {
            props: {
                type: 'primary',
                size: 'small'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                click: () => {
                    vm.openChargeCheckDialog(param.row)
                }
            }
        }, '出账审核');
    }
    const viewTradesDialog=(vm,h,param)=>{
        return h('Button', {
            props: {
                type: 'primary',
                size: 'small'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                click: () => {
                    vm.openViewTradesDialog(param.row)
                }
            }
        }, '出账审核');
    }
    export default {
        computed: {
            ...mapState({
                'ccList': state => state.cc.ccList,
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
                merNO: '',
                chargeCheck: '',
                bTime_cc: new Date(),
                eTime_cc: new Date(),
                chargeCheckModal:false,
                viewTradesModal:false,
                modalLoading:false,
                chargeCheckBak:'',
                tradeViewTitle:'',
                ccStatus:'0',
                ccId:'',
                tableColums_trade:[
                    {
                        title: '交易流水号',
                        key: 'tradeNo',
                        fixed: 'left',
                        width: 100
                    },
                    {
                        title: '交易时间',
                        key: 'tradeTime',
                        fixed: 'left',
                        width: 100
                    }
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
                    {
                        title: '预存抵扣手续费金额',
                        key: 'accountFee',
                    },
                    {
                        title: '交易抵扣手续费金额',
                        key: 'tradeFee',
                    },
                    {
                        title: '出账金额',
                        key: 'amountOff',
                    },
                    {
                        title: '操作',
                        key: 'action',
                        fixed: 'right',
                        width: 200,
                        align: 'center',
                        render: (h, param) =>{

                            let btns=[];

                            if(param.chargeCheck!='0'){
                                btns.push(chargeCheckDialog(this,h,param))
                            }
                            btns.push(viewTradesDialog(this,h,param))

                            return h('div', btns);
                        }
                    }

                ],
                chargeCheckData: [
                    {
                        value: '0',
                        label: '已审'
                    },
                    {
                        value: '1',
                        label: '未审核'
                    },
                    {
                        value: '2',
                        label: '审核未通过'
                    },
                ]

            }
        },
        methods: {
            vChange(b){
                if (!b) {
                    this.ccStatus='0'
                    this.chargeCheckBak=''
                    this.modalLoading = false;
                }

            },
            search_cc() {
                let param = {
                    'bTime': dateKit.formatDate(this.bTime_cc, 'yyyy-MM-dd'),
                    'eTime': dateKit.formatDate(this.eTime_cc, 'yyyy-MM-dd'),
                    merNO: this.merNO,
                    chargeCheck: this.chargeCheck
                }
                this.$store.dispatch("cc_check_list", param);
            },

            chargeCheckSave(){

            },

            openViewTradesDialog(data){
                this.tradeViewTitle=data.merName+",【"+data.clearTimeTxt+"】";
            },

            openChargeCheckDialog(data){

            }

        },
        components: {Input},
        mounted() {

        }

    }
</script>

<style lang="less">
    @import '../../styles/common.less';
</style>