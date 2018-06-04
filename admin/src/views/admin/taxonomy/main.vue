<template>
    <div>
        <Row>
            <Col span="12">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    分类列表
                </p>
                <Row>
                    <Col span="24">
                    <Button type="primary" icon="android-add" @click="addChild">新增分类</Button>
                    <Button type="primary" icon="edit" @click="edit">修改分类</Button>
                    <Poptip title="确定要删除该分类吗?" @on-ok="del" confirm>
                    <Button type="error" icon="android-remove" >删除分类</Button></Poptip>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Tree ref='taxTree' :data="taxJsonArray" ></Tree>
                </Row>
            </Card>
            </Col>
            <Col span="12" class="padding-left-10">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    {{formTitle}}
                </p>
                <Row>
                    <Form ref="formValidate" :label-width="80" :model="tax" :rules="ruleValidate">
                        <FormItem label="父标题" prop="parent_title">
                            <Input v-model="tax.parent_title" placeholder="请输入..." style="width: 300px" disabled></Input>
                        </FormItem>
                        <FormItem label="标题" prop="title">
                            <Input v-model="tax.title" placeholder="请输入..." style="width: 300px"></Input>
                        </FormItem>
                        <FormItem label="模块" prop="module">
                            <Input v-model="tax.module" placeholder="请输入..." style="width: 300px" :disabled="ac"></Input>
                        </FormItem>
                        <FormItem label="说明" prop="text">
                            <Input v-model="tax.text" placeholder="请输入..." style="width: 300px"></Input>
                        </FormItem>
                        <FormItem>
                            <Button type="success" :loading="buttonLoading" icon="checkmark-round" @click="saveData">
                                <span v-if="!buttonLoading">保存</span>
                                <span v-else>数据通信中...</span>
                            </Button>
                        </FormItem>
                    </Form>
                </Row>
            </Card>
            </Col>
        </Row>
    </div>
</template>

<script>
    import {mapState} from 'vuex'
    export default {

        computed: {
            ...mapState({
                'taxJsonArray': state => state.tax.taxJsonArray,
                'tax': state => state.tax.tax,

            })
        },
        methods: {
            del(){
                let taxSelected=this.$refs.taxTree.getSelectedNodes();
                if(taxSelected.length==0){
                    this.$Message.error('请选择要删除的分类')
                    return ;
                }
                this.$store.dispatch('tax_del',{'id':taxSelected[0].id}).then((res)=>{
                    if (res && res == 'success') {
                        this.refresh()
                    }
                });
            },
            addChild(){
                this.$refs['formValidate'].resetFields()
                this.formTitle="新增分类"
                let taxSelected=this.$refs.taxTree.getSelectedNodes();

                if(taxSelected.length==0){
                    this.ac=false;
                    this.$store.commit('set_tax',{})
                }else{
                    this.ac=true;
                    let tax=taxSelected[0];
                    this.$store.commit('set_tax',{parentId:tax.id,parent_title:tax.title,module:tax.module})
                }
            },
            edit(){
                this.$refs['formValidate'].resetFields()
                this.formTitle="修改分类"
                this.ac=false;
                let taxSelected=this.$refs.taxTree.getSelectedNodes();
                if(taxSelected.length==0){
                    this.$Message.error('请选择要修改的分类')
                    return ;
                }

                this.$store.commit('set_tax',taxSelected[0]);
            },
            saveData(){
                let vm = this;
                this.modalLoading = true;
                let tax=this.tax;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        let action='save';
                        if(tax.id)
                            action='update';
                        this.$store.dispatch('tax_save',action).then((res) => {
                            if (res && res == 'success') {
                                this.$store.commit('set_tax')
                                this.refresh();
                            }
                            this.modalLoading = false;
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            refresh(){
                this.$store.dispatch('tax_jsonArray')
                this.$store.commit('set_tax',{})
            },
        },
        mounted () {
            this.$store.dispatch('tax_jsonArray')
            this.$store.commit('set_tax',{})
        },

        components: {

        },

        data () {
            return {
                self: this,
                buttonLoading:false,
                ac:false,
                formTitle:'新增分类',
                ruleValidate: {
                    title: [
                        {type: 'string', required: true, message: '标题不能为空', trigger: 'blur'},
                        {type: 'string', max: 150, message: '标题长度不能超过50', trigger: 'blur'}
                    ],
                    module: [
                        {type: 'string', required: true, message: '模块不能为空', trigger: 'blur'},
                        {type: 'string', max: 32, message: '模块长度不能超过50', trigger: 'blur'}
                    ],
                }

            }
        }
    }

</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>