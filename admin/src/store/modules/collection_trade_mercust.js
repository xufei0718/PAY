import kit from '../../libs/kit';
const collMerCust = {
    state: {
        merCustList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        merCust: {
        }
    },
    mutations: {
        set_mercust_list(state, page) {
            state.merCustList = page.list;
            state.totalPage = page.totalPage;
            state.pageNumber = page.pageNumber;
            state.totalRow = page.totalRow;
        },
        mercust_set(state, obj) {
            if (obj !== undefined) {
                state.merCust = kit.clone(obj);
            }
        }
    },
    actions: {
        mercust_list: function ({ commit, state }, param) {
            if (param && !param.pn) {
                param.pn = state.pageNumber;
            }
            this._vm.$axios.post('/coll/trade/getMerCustPage', param).then((res) => {
                commit('set_mercust_list', res);
            });
        },
        mercust_set: function ({ commit, state }, param) {
            commit('mercust_set', param);
        }
    }
};

export default collMerCust;
