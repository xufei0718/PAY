import Cookies from 'js-cookie';
import kit from '../../libs/kit';

const user = {
    state: {
        userList:[],
        totalPage:0,
        pageNumber:1,
        totalRow:0,
        pageSize:15,
        user:{}
    },
    mutations: {
        logout (state, vm) {
            Cookies.remove('user');
            Cookies.remove('loginname');
            Cookies.remove('menuArray');
            Cookies.remove('serviceArray');
            // 恢复默认样式
            let themeLink = document.querySelector('link[name="theme"]');
            themeLink.setAttribute('href', '');
            // 清空打开的页面等数据，但是保存主题数据
            let theme = '';
            if (localStorage.theme) {
                theme = localStorage.theme;
            }
            localStorage.clear();
            if (theme) {
                localStorage.theme = theme;
            }
        },
        set_user_list(state,page){
            state.userList=page.list
            state.totalPage=page.totalPage
            state.pageNumber=page.pageNumber
            state.pageSize=page.pageSize;
            state.totalRow=page.totalRow
        },
        user_reset(state,param){

            if(param) {
                state.user = kit.clone(param)
            }
            else
                state.user={roleIds:[],isAdmin:'1'};

        }
    },
    actions:{
        user_list:function ({ commit,state },param) {
            if(param&&!param.pn){
                param.pn=state.pageNumber;
            }
            this._vm.$axios.post('/ad01/list',param).then((res)=>{
                commit('set_user_list',res)
            });
        },

        user_save:function ({ commit,state },action) {
            let vm=this._vm;
            let p=kit.clone(state.user)
            let rIds=p.roleIds;
            let rIds_str=rIds.join(",");
            p.roleIds=rIds_str;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad01/'+action, p).then((res) => {
                    if(res.resCode&&res.resCode=='success'){
                        commit('user_reset');
                    }
                    resolve(res.resCode);
                });
            });
        },
        user_del:function({commit,state},param){
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad01/del', param).then((res) => {
                    resolve(res.resCode)
                })
            });
        },
        user_stop:function({commit,state},param){
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad01/forbidden', param).then((res) => {
                    resolve(res.resCode)
                })
            });
        },
        user_active:function({commit,state},param){
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad01/resumed', param).then((res) => {
                    resolve(res.resCode)
                })
            });
        },
        user_reset_pwd:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad01/resetPwd', param).then((res) => {
                    resolve(res);
                });
            });
        },
        user_login:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad06/login', param).then((res) => {
                    resolve(res);
                });
            });
        },
        user_logout:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad06/logout', param).then((res) => {
                    resolve(res);
                });
            });
        },
        update_pwd:function ({commit,state},param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad01/modifyPassword', param).then((res) => {
                    resolve(res);
                });
            });
        }

    }
};

export default user;
