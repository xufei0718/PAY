<template>
    <div>
        <Modal
                v-model="roleModal"
                @on-visible-change="vChange"
                :mask-closable="false"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="80" :model="role" :rules="ruleValidate">
                <FormItem label="角色名" prop="name">
                    <Input v-model="role.name" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
                <FormItem label="说明" prop="description">
                    <Input v-model="role.description" placeholder="请输入..." style="width: 300px"></Input>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button @click="reset" v-show="isAdd">重置</Button>
                <Button type="error" @click="roleModal=false">关闭</Button>
            </div>
        </Modal>

    </div>
</template>


<script>
    import {mapState} from 'vuex'

    export default {
        name: 'roleForm',
        computed: {
            ...mapState({
                'role': state => state.role.role,
            })
        },
        methods: {
            open(title, isAdd){
                this.isAdd = isAdd
                this.modalTitle = title;
                this.roleModal=true;
                if(isAdd)
                    this.$store.commit('role_set',{});
                this.modalLoading=false;
            },
            vChange(b){
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                }
            },
            save(){
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action='save';
                        if(!vm.isAdd)
                            action='update';
                        this.$store.dispatch('role_save',action).then((res) => {
                            this.modalLoading = false;
                            if (res && res == 'success') {
                                vm.roleModal = false;
                                this.$store.dispatch('role_list')
                            }
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            reset(){
                this.$store.dispatch('role_set',{});
            }

        },
        data () {
            return {
                self: this,
                roleModal: false,
                isAdd: true,
                modalTitle: '新增角色',
                modalLoading: false,
                ruleValidate: {
                    name: [
                        {type: 'string', required: true, message: '角色名不能为空', trigger: 'blur'},
                        {type: 'string', max: 50, message: '角色名长度不能超过50', trigger: 'blur'}
                    ],
                    description: [
                        {type: 'string', max: 255, message: '角色说明长度不能超过50', trigger: 'blur'}
                    ],
                }
            }
        }
    }

</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>