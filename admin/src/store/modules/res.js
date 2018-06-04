
const  res={
    state: {
        resJsonArray:[],
        res:{}
    },
    mutations: {
        set_res_list_tree(state,jsonArray){
            state.resJsonArray=jsonArray
        }
    },
    actions:{
        res_list_tree:function ({ commit,state },param) {
            this._vm.$axios.post('/ad03/listTree',param).then((res)=>{
                commit('set_res_list_tree',res)
            });
        }


    }
}
export default res