import kit from '../../libs/kit';
const merCust = {
    state: {
        merCustList:[],
        totalPage:0,
        pageNumber:1,
        pageSize:15,
        totalRow:0,
        merCust:{},
        isOper:true,
        merInfoList:[],

    },
    mutations: {

        set_merCust_list(state,map){
            state.merCustList=map.page.list
            state.totalPage=map.page.totalPage
            state.pageSize=map.page.pageSize
            state.pageNumber=map.page.pageNumber
            state.totalRow=map.page.totalRow
            state.isOper = map.isOper
            state.merInfoList = map.merInfoList

        },
        merCust_info:function(state,param){
            state.merCust = kit.clone(param)
        },
        merCust_reset(state,param){
            if(param) {
                state.merCust = kit.clone(param)
            }
        },

    },
    actions:{
        merCust_list:function ({ commit,state },param) {
            if(param&&!param.pn){
                param.pn=state.pageNumber;
            }
            this._vm.$axios.post('/mer01/list',param).then((res)=>{
                commit('set_merCust_list',res)
            });
        },
        merCust_save:function ({ commit,state },action) {
            let vm=this._vm;
            let p=kit.clone(state.merCust)
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/mer01/'+action, p).then((res) => {
                    if(res.resCode&&res.resCode=='success'){
                        commit('merCust_reset');
                    }
                    resolve(res);
                });
            });
        },

        merCust_del:function({commit,state},param){
            let vm=this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/mer01/del', param).then((res) => {
                    resolve(res.resCode)
                })
            });
        },



    }
};

export default merCust;
