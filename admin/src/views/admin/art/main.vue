<template>
    <div>
        <Row>
            <Col span="24">
            <Card>
                <p slot="title">
                    <Icon type="help-buoy"></Icon>
                    文章列表
                </p>
                <Row>
                    <Col span="8">
                    <Button type="primary" icon="android-add" @click="add">新增文章</Button>
                    <Button type="primary" @click="refresh" icon="refresh">刷新</Button>
                    </Col>
                    <Col span="8" offset="8" align="right">
                    <Input v-model="searchKey" placeholder="请输入..." style="width: 200px"/>
                    <span @click="search" style="margin: 0 10px;"><Button type="primary"
                                                                          icon="search">搜索</Button></span>
                    </Col>
                </Row>
                <Row class="margin-top-10">
                    <Table :context="self" :data="artList" :columns="tableColums"  stripe border></Table>
                </Row>
                <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                        <Page :total="total" :current="pageNumber" @on-change="search" :page-size="pageSize" show-total show-elevator></Page>
                    </div>
                </div>
            </Card>
            </Col>
        </Row>
        <Publish ref="ap"></Publish>
        <artView ref="view"></artView>
    </div>
</template>

<script>

    const publishBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '是否终审发布',
            },
            style: {
                marginRight: '5px'
            },
            on: {
                'on-ok': () => {
                    vm.publish(param.row.id)
                },
            }
        }, [h('Button', {
            props: {
                type: 'success',
                size: 'small'
            }
        }, '终审发布')]);
    }

    const taxViewBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                trigger: 'hover',
                title: param.row.taxList.length + "个栏目",
                placement: 'bottom',

            },
            style: {
                marginRight: '5px'
            },

        }, [h('Tag', param.row.taxList.length),
            h('div', {

                slot: 'content'

            }, [
                h('ul', vm.artList[param.index].taxList.map(item => {
                    return h('li', {
                        style: {
                            textAlign: 'center',
                            padding: '4px'
                        }
                    }, item.title)
                }))
            ])]);
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

    const viewBtn = (vm, h, param) => {
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
                    vm.view(param.row.id)
                }
            }
        }, '查看')
    }

    const delBtn = (vm, h, param) => {
        return h('Poptip', {
            props: {
                confirm: '',
                title: '您确定要删除这个文章信息吗？'
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

    import {mapState} from 'vuex'
    import Publish from './publish.vue'
    import artView from './view.vue'
    export default {
        data () {
            return {
                searchKey: '',
                self: this,
                taxList: [],
                tableColums: [

                    {
                        title: '标题',
                        key: 'title',
                    },
                    {
                        title: '状态',
                        key: 'status',
                        render: (h, param) => {
                            let color = (param.row.status == '00') ? 'green' : (param.row.status == '01') ? 'yellow' : 'red'
                            let txt = (param.row.status == '00') ? '正常' : (param.row.status == '01') ? '待审核' : '审核拒绝'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '精华',
                        key: 'good',
                        render: (h, param) => {
                            let color = (param.row.good == '0') ? 'green' : 'red'
                            let txt = (param.row.good == '0') ? '是' : '否'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '置顶',
                        key: 'top',
                        render: (h, param) => {
                            let color = (param.row.top == '0') ? 'green' : 'red'
                            let txt = (param.row.top == '0') ? '是' : '否'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },
                    {
                        title: '发布状态',
                        key: 'flag',
                        width:150,
                        render: (h, param) => {
                            let color = (param.row.flag == '00') ? 'green' : 'red'
                            let txt = (param.row.flag == '00') ? '已发布' : '草稿'
                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, txt)
                        }
                    },

                    {
                        title: '所属栏目',
                        key: 'taxList',
                        render: (h, param) => {
                            return taxViewBtn(this, h, param)
                        }
                    }
                    ,

                    {
                        title: '创建时间',
                        key: 'cAtTxt',
                    },
                    {
                        title: '发布时间',
                        key: 'pAtTxt',
                    },
                    {
                        title: '作者',
                        key: 'author',
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 250,
                        align: 'center',
                        render: (h, param) => {
                            let btns = [editBtn(this, h, param),
                                delBtn(this, h, param), viewBtn(this, h, param)]
                            if (param.row.flag == '01')
                                btns.push(publishBtn(this, h, param))
                            return h('div', btns);
                        }
                    }
                ]
            }
        },
        methods: {
            add(){
                this.$store.dispatch('art_tax_jsonArray').then(()=>{
                    this.$refs.ap.open('新增文章', true);
                })

            },
            refresh(){
                this.$store.dispatch('art_list', {search: this.searchKey});
            },
            search(pn){
                this.$store.dispatch('art_list', {search: this.searchKey, pn: pn});
            },
            edit(art){
                this.$store.dispatch('art_get',{id:art.id}).then(()=>{
                    this.$refs.ap.open('修改文章', false);
                })
            },
            view(id){
                this.$store.dispatch('art_get',{id:id}).then(()=>{
                    this.$refs.view.open();
                })
            },

            publish(id){
                this.$store.dispatch('art_publish', {id: id}).then((res) => {
                    this.refresh()
                })
            },
            del(id){
                this.$store.dispatch('art_del', {ids: id}).then((res) => {
                    if (res && res == 'success') {
                        this.$store.dispatch('art_list')
                    }
                })
            }
        },
        components: {
            Publish,artView
        },
        computed: {
            ...mapState({
                'artList': state => state.art.artList,
                'totalPage': state => state.art.totalPage,
                'total': state => state.art.totalRow,
                'pageNumber': state => state.art.pageNumber,
                'pageSize': state => state.art.pageSize,
                'art': state => state.art.art,
            })
        },
        mounted () {
            this.$store.dispatch('art_list');
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>