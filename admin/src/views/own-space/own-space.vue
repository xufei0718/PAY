<style lang="less">
    @import './own-space.less';
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="person"></Icon>

            </p>
            <div>
                <Form ref="editPasswordForm" :model="editPasswordForm" :label-width="100" label-position="right" :rules="passwordValidate">
                    <FormItem label="原密码" prop="oldPass" >
                        <Input v-model="editPasswordForm.oldPass" type="password" placeholder="请输入现在使用的密码" ></Input>
                    </FormItem>
                    <FormItem label="新密码" prop="newPass">
                        <Input v-model="editPasswordForm.newPass" type="password" placeholder="请输入新密码，至少6位字符" ></Input>
                    </FormItem>
                    <FormItem label="确认新密码" prop="rePass">
                        <Input v-model="editPasswordForm.rePass" type="password" placeholder="请再次输入新密码" ></Input>
                    </FormItem>
                </Form>
                <div slot="footer">
                    <!--<Button type="text" @click="cancelEditPass">取消</Button>-->
                    <Button type="primary" :loading="savePassLoading" @click="saveEditPass">保存</Button>
                </div>
            </div>
        </Card>
    </div>
</template>

<script>
import Cookies from 'js-cookie';
export default {
    name: 'ownspace_index',
    data () {
        const valideRePassword = (rule, value, callback) => {
            if (value !== this.editPasswordForm.newPass) {
                callback(new Error('两次输入密码不一致'));
            } else {
                callback();
            }
        };
        return {
            loginname:'',
            editPasswordForm: {
                oldPass: '',
                newPass: '',
                rePass: ''
            },
            savePassLoading:false,
            passwordValidate: {
                oldPass: [
                    { required: true, message: '请输入原密码', trigger: 'blur' }
                ],
                newPass: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 6, message: '请至少输入6个字符', trigger: 'blur' },
                    { max: 32, message: '最多输入32个字符', trigger: 'blur' }
                ],
                rePass: [
                    { required: true, message: '请再次输入新密码', trigger: 'blur' },
                    { validator: valideRePassword, trigger: 'blur' }
                ]
            },
        };
    },
    methods: {
        saveEditPass () {
            this.$refs['editPasswordForm'].validate((valid) => {
                if (valid) {
                    this.savePassLoading = true;
                    this.$store.dispatch('update_pwd',{loginname:this.loginname,oldPwd:this.editPasswordForm.oldPass,newPwd:this.editPasswordForm.newPass}).then((res)=>{
                        this.savePassLoading=false;
                    })
                }
            });
        },
        init () {
            this.loginname= Cookies.get('loginname');
        },
    },
    mounted () {
        this.init();
    }
};
</script>
