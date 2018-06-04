<template>
    <div>
        <Modal v-model="terminateEntrustModal" @on-visible-change="vChange" :mask-closable="false" width="500">
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="120" :model="entrust" :rules="ruleValidate">
                <FormItem label="商户类型" prop="merCode">
                    <Select v-model="entrust.merCode">
                        <Option v-for="item in merCodeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="卡号" prop="accNo">
                    <Input v-model="entrust.accNo" placeholder="请输入..."></Input>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">解除委托</Button>
                <Button @click="reset">重置</Button>
                <Button type="error" @click="terminateEntrustModal=false">关闭</Button>
            </div>
        </Modal>

    </div>
</template>


<script>
    import { mapState } from 'vuex'

    export default {
        name: 'terminateEntrustModal',
        props: [
            'pageSize'
        ],
        computed: {
            ...mapState({
                'entrust': state => state.collEntrust.collEntrust,
            })
        },
        methods: {
            open() {
                this.terminateEntrustModal = true;
                this.$store.commit('collEntrust_set', {});
                this.modalLoading = false;
            },
            close(){
                this.terminateEntrustModal = false;
                this.$store.commit('collEntrust_set', {});
                this.modalLoading = false;
            },
            vChange(b) {
                if (!b) {
                    this.$refs['formValidate'].resetFields()
                }
            },
            save() {
                let vm = this;
                this.modalLoading = true;
                this.$refs['formValidate'].validate((valid) => {
                    if (valid) {
                        this.$store.dispatch('entrust_terminate').then((res) => {
                            this.$store.dispatch('get_entrust_list', { ps: this.pageSize })
                            this.close()
                        })
                    } else {
                        this.modalLoading = false;
                    }
                })
            },
            reset() {
                this.$store.dispatch('collEntrust_set', {})
            }

        },
        data() {
            return {
                self: this,
                terminateEntrustModal: false,
                modalTitle: '解除委托',
                modalLoading: false,
                merCodeList: [
                    {
                        value: 'all',
                        label: '全部'
                    },
                    {
                        value: '0',
                        label: '实时商户'
                    },
                    {
                        value: '1',
                        label: '批量商户'
                    },
                ],
                ruleValidate: {
                    merCode: [
                        { type: 'string', required: true, message: '商户类型不能为空', trigger: 'blur' },
                    ],
                    accNo: [
                        { type: 'string', required: true, message: '卡号不能为空', trigger: 'blur' },
                        { type: 'string', max: 50, message: '卡号长度不能超过50', trigger: 'blur' }
                    ]
                }
            }
        }
    }

</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>