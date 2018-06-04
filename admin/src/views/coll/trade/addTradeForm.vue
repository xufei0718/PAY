<template>
    <div>
        <Modal v-model="initiateTradeModal" @on-visible-change="vChange" :mask-closable="false" width="500">
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>

            <Form ref="formValidate" :label-width="80" :model="trade" :rules="ruleValidate">
                <FormItem label="业务类型" prop="bussType">
                    <Select v-model="trade.bussType">
                        <Option v-for="item in bussTypeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="客户" prop="custID">
                    <Select ref="merCustSelect" placeholder="输入 姓名/身份证号/手机号/卡号 进行搜索..." v-model="trade.custID" clearable filterable remote :remote-method="selectCustID"
                        :loading="selectCustIdLoading">
                        <Option v-for="(option, index) in custIDOptions" :value="option.id" :key="index" :label="option.custName">
                            <div style="margin: 5px auto;">
                                <div>
                                    <span style="font-weight:bold; margin-right: 10px;">{{option.custName}}</span>
                                    <span style="color:#ccc">{{option.cardID}}</span>
                                </div>
                                <div>
                                    <span style="margin-right: 10px;">{{option.bankcardNo}}</span>
                                    <span v-if="option.cardBin && option.cardBin.cardName" style="color:#ccc">{{option.cardBin.cardName}}</span>
                                </div>
                            </div>
                        </Option>
                    </Select>
                </FormItem>
                <FormItem label="金额" prop="txnAmt" required>
                    <Input v-model="trade.txnAmt" placeholder="请输入..."></Input>
                    <!-- <InputNumber :min='0.01' v-model="trade.txnAmt" :step="1" style="width: 100%;"></InputNumber> -->
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">发起</Button>
                <Button @click="reset">重置</Button>
                <Button type="error" @click="initiateTradeModal=false">关闭</Button>
            </div>
        </Modal>

        <Modal v-model="addConfirmModal" :closable="false" :mask-closable="false">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="information-circled"></Icon>
                <span>发起确认</span>
            </p>
            <div style="height: 200px;font-size: 1.2em; padding-left:20px;">
                <div>
                    <span style="font-size: 1.5em;">{{custNameForSelected}}</span>
                    <span style="font-size: 1.5em; margin-left:5px; color:#ccc">{{cardIDForSelected}}</span>
                </div>
                <div style="position: relative;">
                    <div style="margin-top: 15px;width:400px;border: 1px solid rgb(233, 233, 233);border-radius: 5px; ">
                        <div style="background: rgb(43, 133, 228);color: #fff;font-size: 1em;border-top-left-radius: 5px;border-top-right-radius: 5px;">
                            <div style="padding-left:10px;">
                                <span style="margin-left:5px;">{{bankNameForSelected}}</span>
                            </div>
                        </div>
                        <div style="padding-left:10px;">
                            <p>
                                <span style="font-size: 1.8em;">{{bankcardNoForSelected}}</span>
                                <span style="padding-left:10px;">{{cardTypeForSelected}}</span>
                            </p>
                            <p style="color:#ccc">{{cardNameForSelected}}</p>
                        </div>
                    </div>
                </div>
                <div style="margin-top: 20px;">
                    <span>交易金额</span>
                    <span style="font-size: 1.8em; color: rgb(220, 147, 135); margin-left: 5px;">
                        <span>{{trade.txnAmt}}</span>
                    </span>
                    <span style="margin-left:5px;">({{bussTypeToString}})</span>
                    <span style="margin-left:45px">商户手续费</span>
                    <span style="font-size: 1.8em; color: rgb(220, 147, 135); margin-left: 5px;">
                        <span>{{collectionTrade.merFee}}</span>
                    </span>
                </div>
            </div>
            <div slot="footer">
                <Button type="ghost" @click="cancelConfirm">取消</Button>
                <Button type="primary" ref="confirmButton" :disabled="disableConfirmButton" @click="confirmed">确认</Button>
            </div>
        </Modal>
    </div>
</template>


<script>
    import { mapState } from 'vuex'
    import dateKit from '../../../libs/date'

    export default {
        name: 'initiateTradeModal',
        props: [
            'pageSize', 'finalCode', 'bTime', 'eTime', 'searchKey','merSearchKey','clearStatus'
        ],
        computed: {
            disableConfirmButton: function(){
                return this.feeResult.errorMessage ?true :false;
            },
            custNameForSelected: function () {
                if (this.custMap && this.trade.custID) {
                    return this.custMap[this.trade.custID].custName;
                } else {
                    return '';
                }
            },
            cardIDForSelected: function () {
                if (this.custMap && this.trade.custID) {
                    return this.custMap[this.trade.custID].cardID;
                } else {
                    return '';
                }
            },
            bankcardNoForSelected: function () {
                if (this.custMap && this.trade.custID) {
                    return this.custMap[this.trade.custID].bankcardNo;
                } else {
                    return '';
                }
            },
            bankNameForSelected: function () {
                if (this.custMap && this.trade.custID) {
                    return this.custMap[this.trade.custID].cardBin.bankName;
                } else {
                    return '';
                }
            },
            cardNameForSelected: function () {
                if (this.custMap && this.trade.custID) {
                    return this.custMap[this.trade.custID].cardBin.cardName;
                } else {
                    return '';
                }
            },
            cardTypeForSelected: function () {
                if (this.custMap && this.trade.custID) {
                    if (this.custMap[this.trade.custID].cardBin) {
                        var cardBin = this.custMap[this.trade.custID].cardBin;
                        return cardBin.cardType === '0' ? '借记卡' : cardBin.cardType === '1' ? '贷记卡' : '其他';
                    } else {
                        return '其他';
                    }
                } else {
                    return '其他';
                }
            },
            bussTypeToString: function () {
                return this.trade.bussType === '1' ? '加急' : '批量';
            },
        },
        methods: {
            confirmed() {
                this.addConfirmModal = false;
                this.$store.dispatch('trade_save', this.trade).then((res) => {
                    let param = {
                        finalCode: this.finalCode,
                        'bTime': dateKit.formatDate(this.bTime, 'yyyy-MM-dd'),
                        'eTime': dateKit.formatDate(this.eTime, 'yyyy-MM-dd'),
                        merSearchKey: this.merSearchKey,
                        clearStatus: this.clearStatus,
                        search: this.searchKey,
                        ps: this.pageSize
                    }
                    this.$store.dispatch('trade_list', param)
                    this.close()
                })
            },
            cancelConfirm() {
                this.addConfirmModal = false;
                this.modalLoading = false;
            },
            open() {
                this.initiateTradeModal = true;
                this.trade = {};
                this.modalLoading = false;
                this.$axios.post('/coll/trade/getMerCust').then((res) => {
                    this.custIDOptionsList = res;
                    for (var i in this.custIDOptionsList) {
                        var option = this.custIDOptionsList[i];
                        this.custMap[option.id] = option;
                    }
                });
            },
            close() {
                this.initiateTradeModal = false;
                this.trade = {};
                this.modalLoading = false;
            },
            vChange(b) {
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                }
            },
            save() {
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        this.$axios.post('/coll/trade/fee', this.trade).then((res) => {
                            this.feeResult = res
                            this.collectionTrade = this.feeResult.collectionTrade;
                            if (this.feeResult.errorMessage) {
                                this.modalLoading = false;
                                this.$Message.error({
                                    content: this.feeResult.errorMessage,
                                    duration: 5
                                });
                            }else{
                                this.addConfirmModal = true;
                            }
                        });
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            reset() {
                this.trade = {};
            },
            selectCustID(query) {
                if (query !== '') {
                    this.selectCustIdLoading = true;
                    setTimeout(() => {
                        this.selectCustIdLoading = false;
                        this.custIDOptions = this.custIDOptionsList.filter(item =>
                            (item.custName.toLowerCase().indexOf(query.toLowerCase()) > -1) ||
                            (item.cardID.toLowerCase().indexOf(query.toLowerCase()) > -1) ||
                            (item.mobileBank.toLowerCase().indexOf(query.toLowerCase()) > -1) ||
                            (item.bankcardNo.toLowerCase().indexOf(query.toLowerCase()) > -1));
                    }, 200);
                } else {
                    this.custIDOptions = [];
                }
            }
        },
        data() {
            return {
                self: this,
                trade: {},
                initiateTradeModal: false,
                addConfirmModal: false,
                collectionTrade: {},
                feeResult: {},
                modalTitle: '发起交易',
                modalLoading: false,
                custIDOptions: [],
                selectCustIdLoading: false,
                custIDOptionsList: [],
                custMap: {},
                bussTypeList: [
                    {
                        value: '1',
                        label: '加急'
                    },
                    {
                        value: '2',
                        label: '批量'
                    },
                ],
                ruleValidate: {
                    bussType: [
                        { type: 'string', required: true, message: '请选择业务类型', trigger: 'blur' },
                    ],
                    custID: [
                        { type: 'number', required: true, message: '请选择客户', trigger: 'blur' },
                    ],
                    txnAmt: [
                        {
                            type: 'string', required: true, message: '请输入有效金额', trigger: 'blur'
                        },
                    ]
                }
            }
        }
    }

</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>