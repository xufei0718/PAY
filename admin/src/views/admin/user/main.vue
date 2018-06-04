<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    用户列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="person-add" @click="add">新增用户</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="8" offset="8" align="right">
                    <Input v-model="searchKey" placeholder="请输入..." style="width: 200px"/>
                    <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                          icon="search">搜索</Button></span>

                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="userList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="search" :page-size="pageSize"  show-total show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <Modal
                v-model="userModal"
                @on-visible-change="vChange"
                :mask-closable="false"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="80" :model="user" :rules="ruleValidate">
                <FormItem label="用户名" prop="loginname">
                    <Input v-model="user.loginname" :disabled="!isAdd" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="姓名" prop="nickname">
                    <Input v-model="user.nickname" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="手机号" prop="phone">
                    <Input v-model="user.phone" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="EMAIL" prop="email">
                    <Input v-model="user.email" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="身份证号" prop="idcard">
                    <Input v-model="user.idcard" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="管理员身份" prop="isAdmin">
                    <!--<i-switch v-model="user.isAdmin" true-value="0" false-value="1">-->
                        <!--<span slot="open">是</span>-->
                        <!--<span slot="close">否</span>-->
                    <!--</i-switch>-->
                    <RadioGroup v-model="user.isAdmin">
                        <Radio label="0" >是</Radio>
                        <Radio label="1" >否</Radio>
                    </RadioGroup>
                </FormItem>
                <FormItem label="角色" prop="roleIds">
                    <CheckboxGroup v-model="user.roleIds" @on-change="">
                        <Checkbox v-for="role in roleList" :key="role.id" :label="role.id+''">
                            <span>{{role.description}}</span>
                        </Checkbox>
                    </CheckboxGroup>
                </FormItem>

            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button @click="reset" v-show="isAdd">重置</Button>
                <Button type="error" @click="userModal=false">关闭</Button>
            </div>
        </Modal>
    </div>

</template>

<script>
    import {mapState} from 'vuex'

    const delBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要删除这个用户信息吗？'
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
    const resetPwdBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要重置这个用户密码吗？'
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.resetPwd(param.row.id)
                }
            }
        }, [h('Button', {
            props: {
                type: 'error',
                size: 'small'
            }
        }, '重置密码')]);
    }

    const stopBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要禁用这个用户吗？'
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
    const actBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要激活这个用户吗？'
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
    const editBtn=(vm,h,param)=>{
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

    export default {
        computed: {
            ...mapState({
                'userList': state => state.user.userList,
                'totalPage': state => state.user.totalPage,
                'pageNumber': state => state.user.pageNumber,
                'pageSize': state => state.user.pageSize,
                'total': state => state.user.totalRow,
                'roleList': state => state.role.roleList,
                'user': state => state.user.user,
            })
        },
        methods: {
            del(i){
                let vm=this;
                this.$store.dispatch('user_del',{id:i}).then((res)=>{
                    setTimeout(vm.search,1000)
                })
            },
            edit(user){
                this.modalTitle="修改用户"
                this.isAdd=false;
                let vm=this
                this.$store.dispatch('role_list').then((res) => {
                    vm.$store.commit('user_reset',user);
                    vm.userModal = true;
                });
            },
            stop(i){
                let vm=this;
                this.$store.dispatch('user_stop',{ids:i}).then((res)=>{
                    setTimeout(vm.search,1000)
                })
            },
            active(i){
                let vm=this;
                this.$store.dispatch('user_active',{ids:i}).then((res)=>{
                    setTimeout(vm.search,1000)
                })
            },
            add(){
                this.isAdd=true
                this.modalTitle="新增用户"
                let vm = this;
                this.$store.dispatch('role_list').then((res) => {
                    vm.userModal = true;
                    vm.$store.commit('user_reset');
                });
            },
            save(){
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action='save';
                        if(!vm.isAdd)
                            action='update';
                        this.$store.dispatch('user_save',action).then((res) => {
                            if (res && res == 'success') {
                                vm.userModal = false;
                                this.$store.dispatch('user_list')
                            } else {
                                this.modalLoading = false;
                            }
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },

            search(pn){
                this.$store.dispatch('user_list',{search:this.searchKey,pn:pn})
            },

            refresh(){
                this.$store.dispatch('user_list',{search:this.searchKey})
            },
            setRole(i){

            },
            vChange(b){
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                    this.modalLoading = false;
                }
            },
            reset(){
                this.$refs['formValidate'].resetFields()
            },
            resetPwd(id){
                this.$store.dispatch('user_reset_pwd',{id:id}).then((res)=>{
                    if(res.resCode&&res.resCode=='success'){
                        this.$Modal.success({
                            title: '提示消息',
                            content: res.resMsg
                        });
                    }else if(res.resCode&&res.resCode=='fail'){
                        this.$Modal.error({
                            title: '提示消息',
                            content: res.resMsg
                        });
                    }else{
                        this.$Modal.error({
                            title: '提示消息',
                            content: '密码重置失败'
                        });
                    }
                });
            }
        },
        mounted () {

            this.$store.dispatch('user_list')
        },
        data () {
            return {
                self: this,
                searchKey: '',
                userModal: false,
                isAdd:true,
                modalTitle: '新增用户',
                modalLoading: false,
                ruleValidate: {
                    loginname: [
                        {type: 'string', required: true, message: '用户名不能为空', trigger: 'blur'},
                        {type: 'string', max: 50, message: '用户名长度不能超过50', trigger: 'blur'}
                    ],
                    nickname: [
                        {required: true, message: '姓名不能为空', max: 50, trigger: 'blur'},
                        {type: 'string', message: '姓名长度不能超过50', max: 50, trigger: 'blur'}
                    ],
                    phone: [
                        {required: true, message: '手机号不能为空', max: 20, trigger: 'blur'},
                        {type: 'string', message: '请输入11位手机号', len: 11, trigger: 'blur'},
                        {
                            type: 'string',
                            message: '手机号码无效',
                            pattern: /^((13|14|15|17|18)[0-9]{1}\d{8})$/,
                            trigger: 'blur'
                        }
                    ],
                    email: [
                        {type: 'email', message: 'email格式不正确', max: 255, trigger: 'blur'},
                        {type: 'string', message: 'email长度不能超过255', max: 255, trigger: 'blur'}
                    ], idcard: [
                        {type: 'string', max: 50, message: '证件号长度不能超过50', trigger: 'blur'}
                    ], roleIds: [
                        {required: true, type: 'array', min: 1, message: '至少选择一个角色', trigger: 'change'},
                    ],
                },
                tableColums: [

                    {
                        title: '登录名',
                        key: 'loginname',
                    },
                    {
                        title: '姓名',
                        key: 'nickname',
                    },
                    {
                        title: '手机号',
                        key: 'phone',
                    },
                    {
                        title: 'EMAIL',
                        key: 'email',
                    },
                    {
                        title: '证件号',
                        key: 'idcard',
                    },
                    {
                        title: '角色信息',
                        key: 'rolesDescStr',
                        render:(h,param)=>{
                            return h('span',{
                                props:{

                                },
                                style:{
                                    'font-weight':'bold',
                                    color:'#ff9900'

                                }
                            },param.row.rolesDescStr)
                        }
                    },
                    {
                        title: '最后登录时间',
                        key: 'logged',
                    },
                    {
                        title: '创建时间',
                        key: 'catTxt',
                    },
                    {
                        title: '超级权限',
                        key: 'isAdmin',
                        width:100,
                        render:(h,param)=>{
                            if(param.row.isAdmin=='0'){
                                return h('Tag',{
                                    props: {
                                        type: 'dot',
                                        color: 'green'
                                    }
                                },'是')
                            }else{
                                return h('Tag',{
                                    props: {
                                        type: 'dot',
                                        color: 'red'
                                    }
                                },'否')
                            }
                        }
                    },
                    {
                        title: '状态',
                        key: 'statusTxt',
                        width:120,
                        render:(h, param)=>{
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
                    {
                        title: '操作',
                        key: 'action',
                        width: 200,
                        align: 'center',
                        render: (h, param) =>{
                            if (!param.row.dAt) {
                                if (param.row.status == '0') {

                                    if(param.row.isAdmin=='0'){
                                        return h('div', [
                                            editBtn(this,h,param),
                                            resetPwdBtn(this,h,param),
                                        ]);
                                    }else{
                                        return h('div', [
                                            editBtn(this,h,param),
                                            delBtn(this,h,param),
                                            stopBtn(this,h,param),
                                            resetPwdBtn(this,h,param),
                                        ]);
                                    }
                                } else {
                                    if(param.row.isAdmin=='0'){
                                        return h('div', [
                                            editBtn(this,h,param),
                                            resetPwdBtn(this,h,param),
                                        ]);
                                    }else{
                                        return h('div', [
                                            editBtn(this,h,param),
                                            delBtn(this,h,param),
                                            actBtn(this,h,param),
                                            resetPwdBtn(this,h,param),
                                        ]);
                                    }

                                }

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
</style>