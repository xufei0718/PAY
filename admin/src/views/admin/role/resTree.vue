<template>
    <div>
        <Modal
                v-model="resModal"
                :mask-closable="false"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
                <Tree ref='resTree' :data="resJsonArray" show-checkbox></Tree>
            <div slot="footer">
                <Button type="success" :loading="modalLoading" @click="save">保存</Button>
                <Button type="error" @click="resModal=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
    import {mapState} from 'vuex'

    export default {
        name: 'resTree',
        computed: {
            ...mapState({
                'resJsonArray': state => state.role.resJsonArray,
            })
        },
        methods: {
            open(title, roleId){
                this.roleId=roleId
                this.modalTitle = title;
                this.resModal=true;
                this.modalLoading=false;
                this.$store.dispatch('role_res_load',{roleId:this.roleId})
            },
            save(){

                let checkedData=this.$refs.resTree.getCheckedNodes();

                if(checkedData.length==0){
                    this.$Message.error('请选择权限')
                    return ;
                }

                //console.info(checkedData);

                let resIdAry=new Array;

                for(let obj of checkedData){
                    resIdAry.push(obj.id)
                }

                let param={'roleId':this.roleId,'resIds':resIdAry.join(",")}

                let vm = this;
                this.modalLoading = true;
                this.$store.dispatch('role_res_save',param).then((res) => {
                    this.modalLoading = false;
                    if (res && res == 'success') {
                        vm.resModal = false;
                    }
                })
            },

        },
        data () {
            return {
                self: this,
                roleId:'',
                resModal: false,
                modalTitle: '权限设置',
                modalLoading: false,
            }
        },
        mounted () {

        },

    }

</script>

<style lang="less">
    @import '../../../styles/common.less';
</style>