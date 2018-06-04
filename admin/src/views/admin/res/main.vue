<template>
    <div>
        <Row>
            <Col span="24">
                <Card>
                    <p slot="title">
                        <Icon type="help-buoy"></Icon>
                        功能管理
                    </p>
                    <Row>
                        <Col span="8" >
                            <Card style="margin-right: 8px">
                                <p slot="title">
                                    <Icon type="help-buoy"></Icon>
                                    功能列表
                                </p>

                                    <Tree :data="data1" :render="treeStyle" on-select-change="handleSelect"></Tree>

                            </Card>
                        </Col>
                        <Col span="16">
                            <Card style="margin-left: 8px">

                                    <Tabs value="name1">
                                        <TabPane label="编辑功能" name="name1" icon="help-buoy">
                                            <Form :model="formRight" label-position="right" :label-width="100">
                                                <FormItem label="功能名称">
                                                    <Input v-model="formRight.input1" size="large"></Input>
                                                </FormItem>
                                                <FormItem label="功能地址">
                                                    <Input v-model="formRight.input2" size="large"></Input>
                                                </FormItem>
                                                <FormItem label="功能描述">
                                                    <Input type="textarea" :autosize="{minRows: 2,maxRows: 5}" size="large"></Input>
                                                </FormItem>
                                                <FormItem label="显示顺序">
                                                    <InputNumber size="large"></InputNumber>
                                                </FormItem>
                                                <FormItem label="是否显示">
                                                    <i-switch size="large">
                                                        <span slot="open">On</span>
                                                        <span slot="close">Off</span>
                                                    </i-switch>
                                                </FormItem>

                                            </Form>

                                            <Card >
                                                <p slot="title">
                                                    <Icon type="navicon-round"></Icon>
                                                    服务列表
                                                </p>
                                                <Row>
                                                    <Col :xs="24" :sm="24" :md="24"  :lg="12" style="padding-left: 10px; padding-right: 10px">

                                                    <Table border :columns="columns1" :data="data3" size="small"></Table>

                                                <Form ref="formDynamic" :model="formDynamic" :label-width="80" style=" margin-top: 20px" >
                                                    <FormItem
                                                            v-for="(item, index) in formDynamic.items"
                                                            v-if="item.status"
                                                            :key="index"
                                                            :label="'服务 ' + item.index"
                                                            :prop="'items.' + index + '.value'"
                                                            :rules="{required: true, message: 'Item ' + item.index +' can not be empty', trigger: 'blur'}">
                                                        <Row>
                                                            <Col span="9">
                                                                <Input type="text" v-model="item.value" placeholder="服务名..."></Input>
                                                            </Col>
                                                            <Col span="9" offset="1">
                                                                <Input type="text" v-model="item.value" placeholder="服务地址..."></Input>
                                                            </Col>
                                                            <Col span="4" offset="1">
                                                                <Button type="ghost" @click="handleRemove(index)" >删除</Button>
                                                            </Col>
                                                        </Row>
                                                    </FormItem>
                                                    <FormItem>
                                                        <Row>
                                                            <Col span="12">
                                                                <Button type="dashed" long @click="handleAdd" icon="plus-round">添加</Button>
                                                            </Col>
                                                        </Row>
                                                    </FormItem>
                                                    <FormItem>
                                                        <Button type="primary" @click="handleSubmit('formDynamic')">保存</Button>
                                                        <Button type="ghost" @click="handleReset('formDynamic')" style="margin-left: 8px">重置</Button>
                                                    </FormItem>
                                                </Form>


                                                </Col>
                                                <Col :xs="24" :sm="24" :md="24"  :lg="12" style="padding-left: 10px; padding-right: 10px">
                                                    <Card  >
                                                        <p slot="title">
                                                            <Icon type="navicon-round"></Icon>
                                                            可选择的服务
                                                        </p>
                                                        <Row>

                                                            <Col span="24"  align="left" style="padding-bottom: 15px">
                                                                <Input placeholder="请输入服务名/地址" style="width: 200px"/>
                                                                <span style="margin: 0 10px;"><Button type="primary"  icon="search">搜索</Button></span>

                                                            </Col>
                                                        </Row>
                                                    <Table height="400" :columns="columns1" :data="data2"></Table>
                                                    </Card>
                                                </Col>
                                                </Row>
                                            </Card>
                                        </TabPane>
                                        <TabPane label="新增子功能" name="name2" icon="help-buoy">新增子功能</TabPane>

                                    </Tabs>

                            </Card>

                        </Col>
                    </Row>
                </Card>
            </Col>

        </Row>
    </div>
</template>
<script>
    export default {
        data() {
            return {
                data1: [
                    {
                        title: 'parent 1',
                        expand: true,

                        children: [
                            {
                                title: 'parent 1-1',
                                expand: true,

                                children: [
                                    {
                                        title: 'leaf 1-1-1'
                                    },
                                    {
                                        title: 'leaf 1-1-2'
                                    }
                                ]
                            },
                            {
                                title: 'parent 1-2',
                                expand: true,
                                children: [
                                    {
                                        title: 'leaf 1-2-1'
                                    },
                                    {
                                        title: 'leaf 1-2-1'
                                    }
                                ]
                            }
                        ]
                    }
                ],

                formLeft: {
                    input1: '',
                    input2: '',
                    input3: ''
                },
                formRight: {
                    input1: '',
                    input2: '',
                    input3: ''
                },
                formTop: {
                    input1: '',
                    input2: '',
                    input3: ''
                },
                index: 1,
                formDynamic: {
                    items: [
                        {
                            value: '',
                            index: 1,
                            status: 1
                        }
                    ]
                },
                columns1: [
                    {
                        type: 'index',
                        width: 60,
                        align: 'center'
                    },
                    {
                        title: '服务名称',
                        key: 'name'
                    },
                    {
                        title: '服务地址',
                        key: 'age'
                    },
                    {
                        title: '服务描述',
                        key: 'address'
                    }
                ],
                data3: [
                    {
                        name: '功能查询',
                        age: '/admin/res',
                        address: '查询功能列表',

                    },],

                data2: [
                    {
                        name: 'John Brown',
                        age: 18,
                        address: 'New York No. 1 Lake Park',
                        date: '2016-10-03'
                    },
                    {
                        name: 'Jim Green',
                        age: 24,
                        address: 'London No. 1 Lake Park',
                        date: '2016-10-01'
                    },
                    {
                        name: 'Joe Black',
                        age: 30,
                        address: 'Sydney No. 1 Lake Park',
                        date: '2016-10-02'
                    },
                    {
                        name: 'Jon Snow',
                        age: 26,
                        address: 'Ottawa No. 2 Lake Park',
                        date: '2016-10-04'
                    },
                    {
                        name: 'John Brown',
                        age: 18,
                        address: 'New York No. 1 Lake Park',
                        date: '2016-10-03'
                    },
                    {
                        name: 'Jim Green',
                        age: 24,
                        address: 'London No. 1 Lake Park',
                        date: '2016-10-01'
                    },
                    {
                        name: 'Joe Black',
                        age: 30,
                        address: 'Sydney No. 1 Lake Park',
                        date: '2016-10-02'
                    },
                    {
                        name: 'Jon Snow',
                        age: 26,
                        address: 'Ottawa No. 2 Lake Park',
                        date: '2016-10-04'
                    }
                ],
            }
        },

        methods: {
            treeStyle (h, { root, node, data })  {

                return h('span', {
                        style: {
                            fontSize:'16px',
                            width: '100%'
                        },
                    },
                    data.title
                )
            },
            handleSelect(selectedList){
                var ele = selectedList[selectedList.length-1] //当前选中的树节点
                ele.expand = true //设置为展开
            },
            handleSubmit (name) {
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        this.$Message.success('Success!');
                    } else {
                        this.$Message.error('Fail!');
                    }
                })
            },
            handleReset (name) {
                this.$refs[name].resetFields();
            },
            handleAdd () {
                this.index++;
                this.formDynamic.items.push({
                    value: '',
                    index: this.index,
                    status: 1
                });
            },
            handleRemove (index) {
                this.formDynamic.items[index].status = 0;
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