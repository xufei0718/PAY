/**
 * Created by yuhaihui on 2017/12/2.
 */
import '../../libs/util.js'
const param = {
    state: {
        map:{}
    },
    mutations: {
        setParamList:function (state,param) {
            state.map=param;
        }
    },
    actions:{
        param_list :function ({ commit,state },param) {
            this._vm.$axios.get('/ad00/getSettingJSON').then((res)=>{
                commit('setParamList',res)
            });
        },
        save_param:function ({ commit,state },param) {
            this._vm.$axios.post('/ad00/save',state.map).then((res)=>{

            });
        }
    }
};

export default param;
