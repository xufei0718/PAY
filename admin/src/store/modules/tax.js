/**
 * Created by yuhaihui8913 on 2017/12/6.
 */


import kit from '../../libs/kit';
const  tax={
    state: {
        taxJsonArray:[],
        tax:{},
    },
    mutations: {
        set_tax_jsonArray(state,jsonArray){
            state.taxJsonArray=jsonArray;
        },
        set_tax(state,obj){
            if(obj !=undefined)
                state.tax=kit.clone(obj);
            else
                state.tax={};
        },

    },
    actions:{
        tax_jsonArray:function ({ commit,state },param) {
            this._vm.$axios.post('/ad05/treeJsonArray',param).then((res)=>{
                commit('set_tax_jsonArray',res)
            });
        },
        tax_save:function ({ commit,state },action) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad05/'+action, state.tax).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        tax_del:function ({ commit,state },param) {
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad05/del', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
    }
}
export default tax
