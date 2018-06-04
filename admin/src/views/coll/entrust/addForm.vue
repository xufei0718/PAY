<template>
    <div>
        <Modal v-model="addEntrustModal" @on-visible-change="vChange" :mask-closable="false" width="500">
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Form ref="formValidate" :label-width="120" :model="entrust" :rules="ruleValidate">
                <FormItem label="商户" prop="merCode">
                    <Select v-model="entrust.merCode">
                        <Option v-for="item in merCodeList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="姓名" prop="customerNm">
                    <Input v-model="entrust.customerNm" placeholder="请输入..."></Input>
                </FormItem>
                <FormItem label="卡号" prop="accNo">
                    <Input v-model="entrust.accNo" placeholder="请输入..."></Input>
                </FormItem>
                <FormItem label="证件类型" prop="certifTp">
                    <Select v-model="entrust.certifTp">
                        <Option value="01" label="身份证">身份证</Option>
                    </Select>
                </FormItem>
                <FormItem label="证件号码" prop="certifId">
                    <Input v-model="entrust.certifId" placeholder="请输入..."></Input>
                </FormItem>
                <FormItem label="手机号" prop="phoneNo">
                    <Input v-model="entrust.phoneNo" placeholder="请输入..."></Input>
                </FormItem>
                <FormItem label="cvn2-仅贷记卡" prop="cvn2">
                    <Input v-model="entrust.cvn2" placeholder="请输入..."></Input>
                </FormItem>
                <FormItem label="有效期-仅贷记卡" prop="expired">
                    <Input v-model="entrust.expired" placeholder="请输入..."></Input>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">建立委托</Button>
                <Button @click="reset">重置</Button>
                <Button type="error" @click="addEntrustModal=false">关闭</Button>
            </div>
        </Modal>

    </div>
</template>


<script>
    import { mapState } from 'vuex'

    export default {
        name: 'addEntrustModal',
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
                this.addEntrustModal = true;
                this.$store.commit('collEntrust_set', {});
                this.modalLoading = false;
            },
            close(){
                this.addEntrustModal = false;
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
                        this.$store.dispatch('entrust_save').then((res) => {
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
                addEntrustModal: false,
                modalTitle: '建立委托',
                modalLoading: false,
                merCodeList: [
                    {
                        value: 'all',
                        label: '全部'
                    },
                    {
                        value: '0',
                        label: '春城实时商户'
                    },
                    {
                        value: '1',
                        label: '春城批量商户'
                    },
                    {
                        value: '2',
                        label: '银盛实时4商户'
                    },
                    {
                        value: '3',
                        label: '银盛实时2商户'
                    },
                ],
                ruleValidate: {
                    merCode: [
                        { type: 'string', required: true, message: '商户类型不能为空', trigger: 'blur' },
                    ],
                    customerNm: [
                        { type: 'string', required: true, message: '姓名不能为空', trigger: 'blur' },
                        { type: 'string', max: 50, message: '姓名长度不能超过50', trigger: 'blur' }
                    ],
                    accNo: [
                        { type: 'string', required: true, message: '卡号不能为空', trigger: 'blur' },
                        { type: 'string', max: 50, message: '卡号长度不能超过50', trigger: 'blur' }
                    ],
                    certifTp: [
                        { type: 'string', required: true, message: '证件类型不能为空', trigger: 'blur' },
                    ],
                    certifId: [
                        { type: 'string', required: true, message: '证件号码不能为空', trigger: 'blur' },
                        { type: 'string', max: 18, message: '证件号码长度不能超过18', trigger: 'blur' }
                    ],
                    phoneNo: [
                        { type: 'string', required: true, message: '手机号不能为空', trigger: 'blur' },
                        { type: 'string', max: 50, message: '手机号长度不能超过50', trigger: 'blur' }
                    ]
                }
            }
        }
    }

</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>