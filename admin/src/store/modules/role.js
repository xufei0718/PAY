import kit from '../../libs/kit';
const  role={
    state: {
        roleList:[],
        totalPage:0,
        pageNumber:1,
        totalRow:0,
        pageSize:15,
        role:{},
        resJsonArray:[]
    },
    mutations: {
        set_role_list(state,page){
            state.roleList=page.list
            state.totalPage=page.totalPage
            state.pageNumber=page.pageNumber
            state.pageSize=page.pageSize
            state.totalRow=page.totalRow
        },
        role_set(state,obj){
            if(obj !=undefined)
                state.role=kit.clone(obj);
        },
        set_role_res(state,jsonArray){
            state.resJsonArray=jsonArray
        }
    },
    actions:{
        role_list:function ({ commit,state },param) {
            this._vm.$axios.post('/ad02/list',param).then((res)=>{
                commit('set_role_list',res)
            });
        },
        role_save:function ({ commit,state },action) {
            let vm=this._vm;



            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad02/'+action, state.role).then((res) => {
                    resolve(res.resCode);
                })
            });
        },
        role_del:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad02/del', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        role_res_load:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad02/loadRes', param).then((res) => {
                    commit('set_role_res',res)
                });
            });
        },
        role_res_save:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad02/setRes', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },


    }
}
export default role