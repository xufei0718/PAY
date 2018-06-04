<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    客户列表
                </p>
                <Row>
                    <Col span="6">
                    <Button type="primary" icon="person-add" @click="add">新增客户</Button>
                    </Col>
                    <Col span="18" align="right">

                    <!--<Tooltip content="支持模糊查找" placement="top-end">-->
                        <Input v-model="searchKey" placeholder="输入客户姓名/手机号" style="width: 200px"/>
                    <!--</Tooltip>-->

                    <Input v-model="searchKey1" v-if="isOper" placeholder="商户编号" style="width: 200px"/>
                    <span @click="search" >
                        <Button type="primary" icon="search">搜索</Button></span>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="merCustList" :columns="tableColums" stripe></Table>
                </Row>
                <Row class="margin-top-10">
                    <Col span="24" align="right">
                    <Page :total="total" :current="pageNumber"  :page-size="pageSize" @on-change="search" show-total show-elevator></Page>
                    </Col>
                </Row>
            </Card>
            </Col>
        </Row>
        <Modal v-model="merCustModal" @on-visible-change="vChange" :mask-closable="false">
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{merCustModalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="150" :model="merCust" :rules="ruleValidate">

                <FormItem label="商户名称" prop="merID" v-if="isOper">
                    <Select v-model="merCust.merID" style="width:300px" :disabled="!isAdd" filterable placeholder="请输入 商户名称/编号 可模糊查找">
                        <Option v-for="item in merInfoList" :value="item.id" :key="item.id">{{item.merchantNo}} {{item.merchantName}}</Option>
                    </Select>
                </FormItem>
                <FormItem label="客户姓名" prop="custName">
                    <Input v-model="merCust.custName"  placeholder="请输入..." :disabled="!isAdd" style="width: 300px"/>
                </FormItem>
                <FormItem label="身份证号码" prop="cardID">
                    <Input v-model="merCust.cardID" placeholder="请输入..." :disabled="!isAdd" style="width: 300px"/>
                </FormItem>
                <FormItem label="银行预留手机号" prop="mobileBank">
                    <Input v-model="merCust.mobileBank" placeholder="请输入..." :disabled="!isAdd" style="width: 300px"/>
                </FormItem>
                <FormItem label="银行卡号" prop="bankcardNo">
                    <Input v-model="merCust.bankcardNo" placeholder="请输入..." :disabled="!isAdd" style="width: 300px"/>
                </FormItem>

                <FormItem label="本人现场照片" prop="selfImg">
                    <!--<Input v-model="merInfo.cardImg" placeholder="请输入..." style="width: 300px"/>-->

                    <Upload
                            ref="upload"
                            :show-upload-list="false"
                            :on-success="handleSuccessSelf"
                            :format="['jpg','jpeg','png']"
                            :max-size="10240"
                            :on-format-error="handleFormatError"
                            :on-exceeded-size="handleMaxSize"
                            :before-upload="handleBeforeUpload"
                            type="drag"
                            :action="uploadAction"
                            style="width:300px; line-height: 0px;">

                        <div style="line-height: 32px;">点击上传本人现场照片</div>

                        <img :src="urlSelfImg" v-show="showImgSelf" width="300">

                    </Upload>
                </FormItem>


                <FormItem label="身份证正面" prop="cardImgZ">
                    <Upload
                            ref="upload"
                            :show-upload-list="false"
                            :on-success="handleSuccessCardZ"
                            :format="['jpg','jpeg','png']"
                            :max-size="10240"
                            :on-format-error="handleFormatError"
                            :on-exceeded-size="handleMaxSize"
                            type="drag"
                            :action="uploadAction"
                            style="width:300px; line-height: 0px;">
                        <div style="line-height: 32px;">点击上传身份证正面</div>
                        <img :src="urlCardImgZ" v-show="showImgCardZ" width="300">
                    </Upload>
                </FormItem>
                <FormItem label="代扣授权书照片" prop="authImg">
                    <Upload
                            ref="upload"
                            :show-upload-list="false"
                            :on-success="handleSuccessAuth"
                            :format="['jpg','jpeg','png']"
                            :max-size="10240"
                            :on-format-error="handleFormatError"
                            :on-exceeded-size="handleMaxSize"
                            type="drag"
                            :action="uploadAction"
                            style="width:300px; line-height: 0px;">

                        <div style="line-height: 32px;">点击上传代扣授权书照片</div>
                        <img :src="urlAuthImg" v-show="showImgAuth" width="300">
                    </Upload>
                </FormItem>

                <Alert type="error" show-icon style="margin: 0px 38px" v-show="isResMsg" >失败原因：{{resMsg}}</Alert>

            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button type="error" @click="merCustModal=false">关闭</Button>
            </div>
        </Modal>

        <Modal  v-model="merCustInfoModal" :mask-closable="true" >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">客户名称：</div>
                </Col>
                <Col span="16">
                <div class="span-lc">{{merCust.custName}}</div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">所属商户：</div>
                </Col>
                <Col span="16">
                <div class="span-lc" v-if="merCustInfoModal">{{merCust.merchantInfo.merchantName}}</div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">商户编号：</div>
                </Col>
                <Col span="16">
                <div class="span-lc" v-if="merCustInfoModal">{{merCust.merchantInfo.merchantNo}}</div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">身份证号码：</div>
                </Col>
                <Col span="16">
                <div class="span-lc">{{merCust.cardID}}</div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">银行预留手机号：</div>
                </Col>
                <Col span="16">
                <div class="span-lc">{{merCust.mobileBank}}</div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">银行卡号：</div>
                </Col>
                <Col span="16">
                <div class="span-lc">{{merCust.bankcardNo}}</div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">创建时间：</div>
                </Col>
                <Col span="16">
                <div class="span-lc">{{merCust.catTxt}}</div>
                </Col>
            </Row>
            <!--<img :src="urlCard" v-show="showImgCard" width="300" >-->
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">本人现场照片：</div>
                </Col>
                <Col span="16">
                <div class="span-lc"><img v-if="merCustInfoModal" width="300" :src="imgUrl+merCust.selfImg"/></div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">身份证正面照片：</div>
                </Col>
                <Col span="16">
                <div class="span-lc"><img v-if="merCustInfoModal" width="300" :src="imgUrl+merCust.cardImgZ"/></div>
                </Col>
            </Row>
            <Row>
                <Col span="8" align="right">
                <div class="span-lb">代扣授权书照片：</div>
                </Col>
                <Col span="16">
                <div class="span-lc"><img v-if="merCustInfoModal" width="300" :src="imgUrl+merCust.authImg"/></div>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="error" @click="merCustInfoModal=false">关闭</Button>
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
                placement:'top-end',
                width:150,
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

    const infoBtn = (vm, h, param) => {
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
                    vm.info(param.row)
                }
            }
        }, '查看');
    }
    const editBtn = (vm, h, param) => {
        return h('Button', {
            props: {
                type: 'primary',
                size: 'small'
            },
            style: {
                marginRight: '5px',
                marginTop:'2px',
                marginBottom:'2px',
            },
            on: {
                click: () => {
                    vm.edit(param.row)
                }
            }
        }, '编辑')
    }

    export default {
        computed: {
            ...mapState({
                'merCustList': state => state.merCust.merCustList,
                'totalPage': state => state.merCust.totalPage,
                'pageSize': state => state.merInfo.pageSize,
                'pageNumber': state => state.merCust.pageNumber,
                'total': state => state.merCust.totalRow,
                'merCust': state => state.merCust.merCust,
                'isOper': state => state.merCust.isOper,
                'merInfoList': state => state.merCust.merInfoList,
            })
        },
        methods: {

            del(i) {
                let vm = this;
                this.$store.dispatch('merCust_del', {id: i}).then((res) => {
                    setTimeout(vm.search, 1000)
                    //vm.search()
                })
            },
            info(i) {
                this.$store.commit('merCust_info', i)
                this.merCustInfoModal =true

            },


            search(pn) {
                this.$store.dispatch('merCust_list', {search: this.searchKey,search1: this.searchKey1, pn: pn})
            },
            add() {
                this.isAdd = true
                this.merCustModalTitle = "新增客户"
                let vm = this;
                vm.merCustModal = true;
                //点击添加按钮是将对象设置成空
                this.$store.commit('merCust_reset', {})
            },
            edit(merCust) {

                this.modalTitle = "修改客户"
                this.isAdd = false

                let vm = this

                vm.$store.commit('merCust_reset', merCust)
                this.urlSelfImg = consts.devLocation + "/cmn/act04?picid=" + merCust.selfImg;
                this.urlCardImgZ = consts.devLocation + "/cmn/act04?picid=" + merCust.cardImgZ;
                this.urlAuthImg = consts.devLocation + "/cmn/act04?picid=" + merCust.authImg;
                this.showImgSelf = true;
                this.showImgCardZ = true;
                this.showImgAuth = true;
                vm.merCustModal = true

            },
            save() {
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action = 'save';
                        if (!vm.isAdd)
                            action = 'update';
                        this.$store.dispatch('merCust_save', action).then((res) => {
                            if (res.resCode && res.resCode == 'success') {
                                vm.merCustModal = false;
                                this.$store.dispatch('merCust_list')
                            }else{
                                vm.resMsg=res.resMsg;
                                vm.isResMsg=true;
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
                    this.showImgSelf = false;
                    this.showImgCardZ = false;
                    this.showImgAuth = false;
                    this.urlSelfImg = '';
                    this.urlCardImgZ = '';
                    this.urlAuthImg = '';
                    this.resMsg='';
                    this.isResMsg=false;
                }
            },
            handleBeforeUpload(res, file) {
                this.modalLoading = true;
            },
            handleSuccessSelf(res, file) {
                this.merCust.selfImg = res.resData;
                this.urlSelfImg = consts.devLocation + "/cmn/act04?picid=" + this.merCust.selfImg
                this.showImgSelf = true;
                this.modalLoading = false;

            },
            handleSuccessCardZ(res, file) {
                this.merCust.cardImgZ = res.resData;
                this.urlCardImgZ = consts.devLocation + "/cmn/act04?picid=" + this.merCust.cardImgZ
                this.showImgCardZ = true;
                this.modalLoading = false;

            },
            handleSuccessAuth(res, file) {
                this.merCust.authImg = res.resData;
                this.urlAuthImg = consts.devLocation + "/cmn/act04?picid=" + this.merCust.authImg
                this.showImgAuth = true;
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
                    desc: '文件 ' + file.name + ' 太大，不能超过 10M。'
                });
            },
        },
        mounted() {
            //页面加载时或数据方法
            this.$store.dispatch('merCust_list')

        },

        data() {
            return {
                self: this,
                searchKey: '',
                searchKey1: '',
                modalTitle: '客户详细信息',
                merCustInfoModal: false,
                isAdd: true,
                uploadAction: consts.env + '/cmn/act03',
                imgUrl:consts.devLocation+"/cmn/act04?picid=",
                showImgSelf: false,
                showImgCardZ: false,
                showImgAuth: false,
                urlSelfImg: '',
                urlCardImgZ: '',
                urlAuthImg: '',
                merCustModal:false,
                merCustModalTitle:'新增客户',
                modalLoading: false,
                resMsg:'',
                isResMsg:false,
                ruleValidate: {
                    merID:[
                        {type: 'number', required: true, message: '必须选择商户', trigger: 'change'},
                    ],
                    custName: [
                        {type: 'string', required: true, message: '客户姓名不能为空', trigger: 'blur'},
                        {type: 'string', max: 100, message: '客户姓名长度不能超过100', trigger: 'blur'}
                    ],
                    cardID: [
                        {required: true, message: '身份证号不能为空', trigger: 'blur'},
                        {type: 'string', max: 50, message: '身份证号长度不能超过50', trigger: 'blur'}
                    ],
                    mobileBank: [
                        {required: true, message: '银行预留手机号号不能为空', trigger: 'blur'},
                        {type: 'string', message: '请输入11位手机号', len: 11, trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手机号码无效',
                            pattern: /^((13|14|15|17|18)[0-9]{1}\d{8})$/,
                            trigger: 'blur'
                        }
                    ],
                    bankcardNo: [
                        {required: true, message: '银行卡号不能为空', trigger: 'blur'},
                        {type: 'string', message: '银行卡号长度不能超过50', max: 50, trigger: 'blur'}
                    ],

                    selfImg: [
                        {required: true, message: '手持身份证照片不能为空', trigger: 'blur'}
                    ],
                    cardImgZ: [
                        {required: true, message: '身份证正面照片不能为空', trigger: 'blur'}
                    ],
                    authImg: [
                        {required: true, message: '代扣授权书照片不能为空', trigger: 'blur'}
                    ],


                },
                tableColums: [
                    {
                        title: '客户名称',
                        key: 'custName',
                        align: 'center',
                    },
                    {
                        title: '所属商户',
                        align: 'center',
                        render: (h, param) => {
                            return param.row.merchantInfo.merchantName
                        }
                    },
                    // {
                    //     title: '商户编号',
                    //     align: 'center',
                    //     render: (h, param) => {
                    //         return param.row.merchantInfo.merchantNo
                    //     }
                    // },

                    // {
                    //     title: '身份证号',
                    //     key: 'cardID',
                    //     align: 'center',
                    // },
                    {
                        title: '银行预留手机号',
                        key: 'mobileBank',
                        align: 'center',
                    },
                    {
                        title: '银行卡卡号',
                        key: 'bankcardNo',
                        align: 'center',
                    },


                    {
                        title: '创建时间',
                        key: 'catTxt',
                        align: 'center',
                    },


                    {
                        title: '操作',
                        key: 'action',
                        width: 200,
                        align: 'center',
                        render: (h, param) => {
                            if (!param.row.dAt) {
                                return h('div', [
                                    editBtn(this, h, param),
                                    infoBtn(this, h, param),
                                    delBtn(this, h, param),
                                ]);
                            }
                        }
                    }

                ]
            }
        }

    }
</script>
<style lang="less">
    @import '../../../styles/common.less';

    .span-lb {
        margin: 5px 0px;
        padding: 5px 5px;
        font-weight: bold;

    }

    .span-lc {
        margin: 5px 0px;
        padding: 5px 5px;
    }

</style>