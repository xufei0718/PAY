<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    角色列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="android-add" @click="add">新增角色</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" border :data="roleList" :columns="tableColums" stripe></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="changePage" :page-size="pageSize" show-total show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <roleForm ref="rf"></roleForm>
        <resTree ref="rt"></resTree>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    import roleForm from './form.vue'
    import resTree from './resTree.vue'
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
    };
    const setResBtn=(vm,h,param)=>{
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
                    vm.setRes(param.row)
                }
            }
        }, '权限设置')
    };
    const delBtn=(vm,h,param)=>{
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要删除这个角色信息吗？'
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
    };

    export default {

        computed: {
            ...mapState({
                'roleList': state => state.role.roleList,
                'totalPage': state => state.role.totalPage,
                'total': state => state.role.totalRow,
                'pageNumber': state => state.role.pageNumber,
                'pageSize': state => state.role.pageSize,
                'role': state => state.role.role,

            })
        },
        methods: {
            del(i){

                this.$store.dispatch('role_del',{'ids':i}).then((res)=>{
                    if (res && res == 'success') {
                        this.$store.dispatch('role_list')
                    }
                });
            },
            edit(role){
                this.$store.commit('role_set',role);
                this.$refs.rf.open('编辑角色',false);
            },
            add(){
                this.$refs.rf.open('新增角色',true)
            },
            changePage(pn){
                this.$store.dispatch('role_list',{pn:pn})
            },
            refresh(){
                this.$store.dispatch('role_list')
            },
            setRes(role){
                let title=role.name+'权限设置';
                this.$refs.rt.open(title,role.id)
            }

        },
        mounted () {
            this.$store.dispatch('role_list')
            this.$store.dispatch('res_list_tree')
        },

        components: {
            roleForm: roleForm,
            resTree: resTree
        },

        data () {
            return {

                self: this,
                tableColums: [

                    {
                        title: '角色名',
                        key: 'name',
                    },
                    {
                        title: '说明',
                        key: 'description',
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 200,
                        align: 'center',
                        render:(h,param)=>{
                            return h('div', [
                                editBtn(this,h,param),
                                setResBtn(this,h,param),
                                delBtn(this,h,param),

                            ])
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