import kit from '../../libs/kit';
const collBatch = {
    state: {
        headBatchTradeList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        collBatchTrade: {
        }
    },
    mutations: {
        set_batch_trade_list(state, page) {
            state.headBatchTradeList = page.list;
            state.totalPage = page.totalPage;
            state.pageNumber = page.pageNumber;
            state.totalRow = page.totalRow;
        },
        collBatchTrade_set(state, obj) {
            if (obj !== undefined) {
                state.collBatchTrade = kit.clone(obj);
            }
        }
    },
    actions: {
        batch_trade_list: function ({ commit, state }, param) {
            if (param && !param.pn) {
                param.pn = state.pageNumber;
            }
            this._vm.$axios.post('/coll/batch/list', param).then((res) => {
                commit('set_batch_trade_list', res);
            });
        },
        collBatchTrade_set: function ({ commit, state }, param) {
            commit('collBatchTrade_set', param);
        }
    }
};

export default collBatch;
