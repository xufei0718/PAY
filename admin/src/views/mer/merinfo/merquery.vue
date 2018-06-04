<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    商户列表
                </p>
                <Row>

                    <Col span="24"  align="right">
                    <Input v-model="searchKey" placeholder="输入商户编号/名称/负责人名称" style="width: 200px"/>
                    <span @click="search" >
                        <Button type="primary" icon="search">搜索</Button></span>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="merInfoList" :columns="tableColums" stripe></Table>
                </Row>
                <Row class="margin-top-10">
                    <Col span="24" align="right">
                    <Page  :total="total" :current="pageNumber"  :page-size="pageSize" @on-change="search" show-total show-elevator ></Page>
                    </Col>
                </Row>
            </Card>
            </Col>
        </Row>
        <Modal v-model="merInfoModal" @on-visible-change="vChange" :mask-closable="false">
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="150" :model="merInfo" :rules="ruleValidate">
                <FormItem label="商户名称" prop="merchantName">
                    <Input v-model="merInfo.merchantName" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="商户类型" prop="merchantType">
                    <Select v-model="merInfo.merchantType" style="width:300px" :disabled="isEditMerType">
                        <Option v-for="item in merchantTypeList" :value="item.text" :key="item.text">{{ item.title }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="负责人名称" prop="perName">
                    <Input v-model="merInfo.perName" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="身份证号码" prop="cardID">
                    <Input v-model="merInfo.cardID" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="负责人手机号" prop="mobile">
                    <Input v-model="merInfo.mobile" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="负责人Email" prop="email">
                    <Input v-model="merInfo.email" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="负责人联系地址" prop="address">
                    <Input v-model="merInfo.address" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="备用联系地址1" prop="mobile1">
                    <Input v-model="merInfo.mobile1" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="备用联系地址2" prop="mobile2">
                    <Input v-model="merInfo.mobile2" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="预存手续费余额" prop="feeAmount">
                    <Input v-model="merInfo.feeAmount" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="最大代扣金额" prop="maxTradeAmount">
                    <Input v-model="merInfo.maxTradeAmount" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="清算银行卡号" prop="bankNo">
                    <Input v-model="merInfo.bankNo" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="清算银行卡户名" prop="bankAccountName">
                    <Input v-model="merInfo.bankAccountName" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="清算银行卡预留手机号" prop="bankPhone">
                    <Input v-model="merInfo.bankPhone" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="清算银行卡开户行全名" prop="bankName">
                    <Input v-model="merInfo.bankName" placeholder="请输入..." style="width: 300px"/>
                </FormItem>
                <FormItem label="清算银行卡开户行行号" prop="bankCode">
                    <Input v-model="merInfo.bankCode" placeholder="请输入..." style="width: 300px"/>
                </FormItem>


                <FormItem label="手持身份证照片" prop="cardImg">
                    <!--<Input v-model="merInfo.cardImg" placeholder="请输入..." style="width: 300px"/>-->

                    <Upload
                            ref="upload"
                            :show-upload-list="false"
                            :on-success="handleSuccessCard"
                            :format="['jpg','jpeg','png']"
                            :max-size="4096"
                            :on-format-error="handleFormatError"
                            :on-exceeded-size="handleMaxSize"
                            :before-upload="handleBeforeUpload"
                            type="drag"
                            :action="uploadAction"
                            style="width:300px; line-height: 0px;">

                        <div style="line-height: 32px;">点击上传手持身份证照片</div>

                        <img :src="urlCard" v-show="showImgCard" width="300">

                    </Upload>
                </FormItem>


                <FormItem label="身份证正面" prop="cardZ">
                    <Upload
                            ref="upload"
                            :show-upload-list="false"
                            :on-success="handleSuccessCardZ"
                            :format="['jpg','jpeg','png']"
                            :max-size="4096"
                            :on-format-error="handleFormatError"
                            :on-exceeded-size="handleMaxSize"
                            type="drag"
                            :action="uploadAction"
                            style="width:300px; line-height: 0px;">
                        <div style="line-height: 32px;">点击上传身份证正面</div>
                        <img :src="urlCardZ" v-show="showImgCardZ" width="300">
                    </Upload>
                </FormItem>
                <FormItem label="身份证背面" prop="cardF">
                    <Upload
                            ref="upload"
                            :show-upload-list="false"
                            :on-success="handleSuccessCardF"
                            :format="['jpg','jpeg','png']"
                            :max-size="4096"
                            :on-format-error="handleFormatError"
                            :on-exceeded-size="handleMaxSize"
                            type="drag"
                            :action="uploadAction"
                            style="width:300px; line-height: 0px;">

                        <div style="line-height: 32px;">点击上传身份证背面</div>
                        <img :src="urlCardF" v-show="showImgCardF" width="300">
                    </Upload>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button type="error" @click="merInfoModal=false">关闭</Button>
            </div>
        </Modal>

        <Modal v-model="merFeeModal" @on-visible-change="vFeeChange" :mask-closable="false" width=700>
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{merFeeTitle}} {{merName}}</span>
            </p>
            <Row>
                <Col span="12" align="center">
                <div style="font-size: 14px; font-weight: bold; background-color: #ccc; padding: 5px; ;margin: 5px; ">加急</div>
                <table>
                    <div v-for="item in merFeeListJ" class="feeList">
                        <tr>
                            <td width="230" align="center">

                                <span v-if="item.amountLower > '0'">{{ item.amountLower }}<</span>
                                交易金额
                                <span v-if="item.amountUpper > '0'"><={{ item.amountUpper }}</span>
                            </td>
                            <td width="80" align="center">
                                <span v-if="item.feeType === '1'">每笔{{ item.amount }}元</span>
                                <span v-else-if="item.feeType  === '2'">{{item.ratio*100}}%</span>
                            </td>
                            <td align="center" width="50">
                                <Button type="error" shape="circle" size="small" @click="delFee(item.id)">删除</Button>
                            </td>
                        </tr>
                    </div>
                </table>

                </Col>
                <Col span="12" align="center">
                <div style="font-size: 14px; font-weight: bold; background-color: #ccc; padding: 5px;margin: 5px;">标准</div>
                <table>
                    <div v-for="item in merFeeListB" class="feeList">
                        <tr>
                            <td width="230" align="center">

                                <span v-if="item.amountLower > '0'">{{ item.amountLower }}<</span>
                                交易金额
                                <span v-if="item.amountUpper > '0'"><={{ item.amountUpper }}</span>
                            </td>
                            <td width="80" align="center">
                                <span v-if="item.feeType === '1'">每笔{{ item.amount }}元</span>
                                <span v-else-if="item.feeType  === '2'">{{item.ratio*100}}%</span>
                            </td>
                            <td align="center" width="50">
                                <Button type="error" shape="circle" size="small" @click="delFee(item.id)">删除</Button>
                            </td>
                        </tr>
                    </div>
                </table>
                </Col>
            </Row>
            <Row>
                <Col span="24" align="center">
                <div style="margin-top: 30px;">
                    <Form ref="formFeeValidate" :model="merFee" :rules="feeValidate">

                            <Col span="5" align="center">
                        <FormItem prop="tradeType" >
                            <RadioGroup v-model="merFee.tradeType" type="button">
                                <Radio label="1">
                                    <span>加急</span>
                                </Radio>
                                <Radio label="2">
                                    <span>标准</span>
                                </Radio>
                            </RadioGroup>
                        </FormItem>
                            </Col>
                        <Col span="5" align="left">
                        <FormItem prop="amountUpper" >
                        <Tooltip placement="bottom">
                                <Input v-model="merFee.amountUpper" placeholder="请输入金额上限" style="width: 140px"/>
                            <div slot="content">
                                <p>输入0表示基础手续费设定</p>
                                <p>商户至少需要一个基础手续费</p>
                            </div>
                        </Tooltip>
                        </FormItem>
                        </Col>
                        <Col span="5" align="center" >
                        <FormItem prop="feeType" >
                            <RadioGroup v-model="merFee.feeType" type="button">
                                <Radio label="1">
                                    <span>定额</span>
                                </Radio>
                                <Radio label="2">
                                    <span>比例</span>
                                </Radio>
                            </RadioGroup>
                        </FormItem>
                        </Col>
                        <Col span="6" align="left">
                        <FormItem prop="amount" >
                            <Input v-model="merFee.amount" placeholder="请输入手续费金额或比例" style="width: 160px"/>
                        </FormItem>
                        </Col>
                        <Col span="2" align="left">
                        <Button type="success" @click="addFee">增加</Button>
                        </Col>

                    </Form>
                </div>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="error" @click="merFeeModal=false">关闭</Button>
            </div>
        </Modal>

    </div>

</template>

<script>
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'

    const delBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                placement: 'top-end',
                width: 150,
                title: '您确定要删除吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.del(param.row.id)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '删除')]);
    }
    const editBtn = (vm, h, param) => {
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
                    vm.edit(param.row)
                }
            }
        }, '编辑')
    }
    const stopBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                placement: 'top-end',
                width: 150,
                title: '您确定要禁用吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.stop(param.row.id)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '禁用')]);
    }
    const actBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                placement: 'top-end',
                width: 150,
                title: '您确定要激活吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.active(param.row.id)
                }
            }
        }, [h('Button', {
            props: {
                type: 'success',
                size: 'small'
            }
        }, '激活')]);
    }
    const feeBtn = (vm, h, param) => {
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
                    vm.fee(param.row)
                }
            }
        }, '手续费管理')
    }

    export default {

        computed: {
            ...mapState({
                'merInfoList': state => state.merInfo.merInfoList,
                'totalPage': state => state.merInfo.totalPage,
                'pageNumber': state => state.merInfo.pageNumber,
                'pageSize': state => state.merInfo.pageSize,
                'total': state => state.merInfo.totalRow,
                'merInfo': state => state.merInfo.merInfo,
                'merchantTypeList': state => state.merInfo.merchantTypeList,
                'merFeeListJ': state => state.merInfo.merFeeListJ,
                'merFeeListB': state => state.merInfo.merFeeListB,
            })
        },
        methods: {
            add() {
                this.isAdd = true
                this.isEditMerType = false
                this.modalTitle = "新增商户"
                let vm = this;
                vm.merInfoModal = true;
                //点击添加按钮是将对象设置成空
                this.$store.commit('merInfo_reset', {})
            },
            del(i) {
                let vm = this;
                this.$store.dispatch('merInfo_del', {id: i}).then((res) => {
                    setTimeout(vm.search, 1000)
                    //vm.search()
                })
            },
            edit(merInfo) {
                this.modalTitle = "修改商户"
                this.isAdd = false
                this.isEditMerType = true
                let vm = this

                vm.$store.commit('merInfo_reset', merInfo)
                this.urlCard = consts.devLocation + "/cmn/act04?picid=" + merInfo.cardImg;
                this.urlCardF = consts.devLocation + "/cmn/act04?picid=" + merInfo.cardF;
                this.urlCardZ = consts.devLocation + "/cmn/act04?picid=" + merInfo.cardZ;
                this.showImgCard = true;
                this.showImgCardF = true;
                this.showImgCardZ = true;
                vm.merInfoModal = true

            },
            stop(i) {
                let vm = this;
                this.$store.dispatch('merInfo_stop', {id: i}).then((res) => {
                    setTimeout(vm.search, 1000)
                    //vm.search()
                    //this.$store.dispatch('merInfo_list',{search:this.searchKey,pn:pn})
                })
            },
            active(i) {
                let vm = this;
                this.$store.dispatch('merInfo_active', {id: i}).then((res) => {
                    setTimeout(vm.search, 1000)
                    //vm.search()
                    //this.$store.dispatch('merInfo_list',{search:this.searchKey,pn:pn})
                })
            },
            save() {
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action = 'save';
                        if (!vm.isAdd)
                            action = 'update';
                        this.$store.dispatch('merInfo_save', action).then((res) => {
                            if (res && res == 'success') {
                                vm.merInfoModal = false;
                                this.$store.dispatch('merInfo_list')
                            } else {
                                this.modalLoading = false;
                            }
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            vChange(b) {
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                    this.modalLoading = false;
                    this.showImgCard = false;
                    this.showImgCardF = false;
                    this.showImgCardZ = false;
                    this.urlCard = '';
                    this.urlCardF = '';
                    this.urlCardZ = '';
                }
            },
            search(pn) {
                this.$store.dispatch('merInfo_list', {search: this.searchKey, pn: pn})
            },
            refresh() {
                this.$store.dispatch('merInfo_list', {search: this.searchKey})
            },
            handleBeforeUpload(res, file) {
                this.modalLoading = true;
            },
            handleSuccessCard(res, file) {
                this.merInfo.cardImg = res.resData;
                this.urlCard = consts.devLocation + "/cmn/act04?picid=" + this.merInfo.cardImg
                this.showImgCard = true;
                this.modalLoading = false;

            },
            handleSuccessCardZ(res, file) {
                this.merInfo.cardZ = res.resData;
                this.urlCardZ = consts.devLocation + "/cmn/act04?picid=" + this.merInfo.cardZ
                this.showImgCardZ = true;
                this.modalLoading = false;

            },
            handleSuccessCardF(res, file) {
                this.merInfo.cardF = res.resData;
                this.urlCardF = consts.devLocation + "/cmn/act04?picid=" + this.merInfo.cardF
                this.showImgCardF = true;
                this.modalLoading = false;

            },
            handleFormatError(file) {
                this.$Notice.warning({
                    title: '文件格式不正确',
                    desc: '文件 ' + file.name + ' 格式不正确，请上传 jpg 或 png 格式的图片。'
                });
            },
            handleMaxSize(file) {
                this.$Notice.warning({
                    title: '超出文件大小限制',
                    desc: '文件 ' + file.name + ' 太大，不能超过 4M。'
                });
            },

            fee(i) {

                this.$store.dispatch('merFee_list', {id: i.id}).then((res) => {
                    this.merFeeModal = true
                    this.merFee.merID = i.id
                    this.merName = i.merchantName
                    //vm.search()
                })

            },
            addFee() {
                let vm = this
                this.$refs['formFeeValidate'].validate((valid) => {
                    if (valid) {
                        //获取输入的手续费数据
                        this.$store.dispatch('add_merFee', this.merFee).then((res) => {
                            vm.merFee.tradeType =''
                            vm.merFee.feeType =''
                            vm.merFee.amountUpper = null
                            vm.merFee.amount = null
                        })
                    }
                })

            },
            delFee(id) {
                this.$store.dispatch('del_merFee', id).then((res) => {
                })

            },
            vFeeChange(b) {
                if (!b) {
                    this.$refs['formFeeValidate'].resetFields()
                    this.merFee.tradeType =''
                    this.merFee.feeType =''
                    this.merFee.amountUpper = null
                    this.merFee.amount = null
                }
            },
        },
        mounted() {
            //页面加载时或数据方法
            this.$store.dispatch('merInfo_list')

        },
        data() {
            return {
                merName: '',
                merFee: {},
                merFeeTitle: '手续费管理',
                merFeeModal: false,
                feeModalLoading: false,
                showImgCard: false,
                showImgCardZ: false,
                showImgCardF: false,
                urlCard: '',
                urlCardZ: '',
                urlCardF: '',
                self: this,
                searchKey: '',
                merInfoModal: false,
                isAdd: true,
                isEditMerType: false,
                modalTitle: '新增用户',
                uploadAction: consts.env + '/cmn/act03',
                modalLoading: false,

                ruleValidate: {
                    merchantName: [
                        {type: 'string', required: true, message: '商户名称不能为空', trigger: 'blur'},
                        {type: 'string', max: 100, message: '用户名长度不能超过100', trigger: 'blur'}
                    ],
                    merchantType: [
                        {type: 'string', required: true, message: '请选择商户类型', trigger: 'blur'}
                    ],
                    perName: [
                        {required: true, message: '负责人名称不能为空', trigger: 'blur'},
                        {type: 'string', message: '负责人名称长度不能超过50', max: 50, trigger: 'blur'}
                    ],
                    mobile: [
                        {required: true, message: '负责人手机号不能为空', trigger: 'blur'},
                        {type: 'string', message: '请输入11位手机号', len: 11, trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手机号码无效',
                            pattern: /^((13|14|15|17|18)[0-9]{1}\d{8})$/,
                            trigger: 'blur'
                        }
                    ],
                    email: [
                        {type: 'string', required: true, message: 'Email不能为空', trigger: 'blur'},
                        {type: 'email', message: 'Email格式不正确', max: 255, trigger: 'blur'},
                        {type: 'string', message: 'Email长度不能超过255', max: 255, trigger: 'blur'}
                    ],
                    cardID: [
                        {required: true, message: '身份证号不能为空', trigger: 'blur'},
                        {type: 'string', max: 50, message: '身份证号长度不能超过50', trigger: 'blur'}
                    ],
                    address: [
                        {required: true, message: '联系地址不能为空', trigger: 'blur'},
                        {type: 'string', max: 200, message: '联系地址长度不能超过20', trigger: 'blur'}
                    ],

                    bankNo: [
                        {required: true, message: '清算银行卡号不能为空', trigger: 'blur'},
                    ],

                    bankAccountName: [
                        {required: true, message: '清算银行卡户名不能为空', trigger: 'blur'},
                    ],
                    maxTradeAmount: [
                        {
                            type: 'string',
                            message: '最大代扣金额无效',
                            pattern: /^\d+$/,
                            trigger: 'blur'
                        }
                    ],

                    cardImg: [
                        {required: true, message: '手持身份证照片不能为空', trigger: 'blur'}
                    ],
                    cardZ: [
                        {required: true, message: '身份证正面照片不能为空', trigger: 'blur'}
                    ],
                    cardF: [
                        {required: true, message: '身份证反面照片不能为空', trigger: 'blur'}
                    ],


                },

                feeValidate: {
                    tradeType: [
                        {type: 'string', required: true, message: '请选择交易类型', trigger: 'change'},
                    ],
                    amountUpper: [
                        {type: 'string', required: true, message: '金额上限不能为空', trigger: 'blur'},
                        {
                            type: 'string',
                            message: '请输入0或整数',
                            pattern: /^\d+$/,
                            trigger: 'blur'
                        }
                    ],
                    feeType: [
                        {type: 'string', required: true, message: '请选择手续费类型', trigger: 'change'},
                    ],
                    amount: [
                        {type: 'string', required: true, message: '手续费金额或比例不能为空', trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手续费金额或比例输入无效',
                            pattern: /^([+]?\d{1,10})(\.\d{1,10})?$/,
                            trigger: 'blur'
                        }
                    ],



                },

                tableColums: [

                    {
                        title: '商户名称',
                        key: 'merchantName',
                        align: 'center',
                    },
                    {
                        title: '商户编号',
                        key: 'merchantNo',
                        align: 'center',
                        width: 100,

                    },
                    {
                        title: '商户类型',
                        key: 'merTypeTxt',
                        align: 'center',
                        width: 100,
                    },
                    {
                        title: '负责人姓名',
                        key: 'perName',
                        align: 'center',
                        width: 120,
                    },
                    // {
                    //     title: '身份证号码',
                    //     key: 'cardID',
                    // },
                    {
                        title: '手机',
                        key: 'mobile',
                        align: 'center',
                        width: 120,
                    },
                    {
                        title: '手续费',
                        key: 'feeAmount',
                        align: 'center',
                        width: 120,
                    },
                    {
                        title: '创建时间',
                        key: 'catTxt',
                        align: 'center',
                        width: 100,
                    },

                    {
                        title: '状态',
                        key: 'statusTxt',
                        width: 120,
                        align: 'center',
                        render: (h, param) => {
                            if (param.row.status == '0') {
                                return h('Tag', {
                                    props: {
                                        type: 'dot',
                                        color: 'blue'
                                    },
                                }, param.row.statusTxt)
                            } else if (param.row.status == '1') {
                                return h('Tag', {
                                    props: {
                                        type: 'dot',
                                        color: 'red'
                                    },
                                }, param.row.statusTxt)
                            }
                        }
                    },
                    // {
                    //     title: '操作',
                    //     key: 'action',
                    //     width: 260,
                    //     align: 'center',
                    //     render: (h, param) => {
                    //         if (!param.row.dAt) {
                    //             if (param.row.status == '0') {
                    //
                    //
                    //                 return h('div', [
                    //                     feeBtn(this, h, param),
                    //                     editBtn(this, h, param),
                    //                     delBtn(this, h, param),
                    //                     stopBtn(this, h, param),
                    //                 ]);
                    //
                    //             } else {
                    //
                    //
                    //                 return h('div', [
                    //                     feeBtn(this, h, param),
                    //                     editBtn(this, h, param),
                    //                     delBtn(this, h, param),
                    //                     actBtn(this, h, param),
                    //
                    //                 ]);
                    //
                    //
                    //             }
                    //
                    //         }
                    //     }
                    // }

                ]
            }
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';

    .feeList {
        font-size: 12px;
        line-height: 30px;
    }

</style>