<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <div>
                    <div>
                        <Row>
                            <Col span="8" align="left">
                            <!-- <Button type="primary" icon="person-add" @click="initiate">发起交易</Button> -->
                            <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                            </Col>
                            <Col span="16" align="right">
                            <Input v-model="searchKey" placeholder="请输入..." style="width: 200px" />
                            <span @click="search" style="margin: 0 10px;">
                                <Button type="primary" icon="search">搜索</Button>
                            </span>
                            </Col>
                        </Row>

                    </div>

                    <Row class="margin-top-10">
                        <Table border :data="merCustList" :columns="tableColums" stripe></Table>
                    </Row>
                    <div style="margin: 10px;overflow: hidden">
                        <div style="float: right;">
                            <Page :total="total" :current="pageNumber" @on-change="search" show-total show-elevator></Page>
                        </div>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <!-- <addForm ref="aem" :pageSize="pageSize" :finalCode="finalCode" :bTime="bTime" :eTime="eTime" :searchKey="searchKey"></addForm> -->
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import dateKit from '../../../libs/date'

    export default {
        computed: {
            ...mapState({
                'merCustList': state => state.collMerCust.merCustList,
                'totalPage': state => state.collMerCust.totalPage,
                'pageNumber': state => state.collMerCust.pageNumber,
                'total': state => state.collMerCust.totalRow,
                'merCust': state => state.collMerCust.merCust,
            })
        },
        methods: {
            search(pn) {
                let param = {
                    search: this.searchKey,
                    pn: pn,
                    ps: this.pageSize
                }
                this.$store.dispatch('mercust_list', param)
            },
            refresh() {
                let param = {
                    search: this.searchKey,
                    ps: this.pageSize
                }
                this.$store.dispatch('mercust_list', param)
            },
            initiate() {
                this.$refs.aem.open();
            },
        },
        components: {

        },
        mounted() {
            let param = {
                search: this.searchKey,
                ps: this.pageSize
            }
            this.$store.dispatch('mercust_list', param)
        },
        data() {
            return {
                searchKey: '',
                pageSize: 30,
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
                    {
                        title: '商户编号',
                        align: 'center',
                        render: (h, param) => {
                            return param.row.merchantInfo.merchantNo
                        }
                    },
                    {
                        title: '身份证号',
                        key: 'cardID',
                        align: 'center',
                    },
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
                    // {
                    //     title: '操作',
                    //     key: 'action',
                    //     width: 130,
                    //     align: 'center',
                    //     render: (h, param) => {
                    //         if (!param.row.dAt) {
                    //             return h('div', [
                    //                 infoBtn(this, h, param),
                    //                 delBtn(this, h, param),
                    //             ]);
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
</style>