import kit from '../../libs/kit';
const unionpayEntrust = {
    state: {
        entrustTradeList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        unionpayEntrust: {}
    },
    mutations: {
        set_entrust_trade_list(state, page) {
            state.entrustTradeList = page.list;
            state.totalPage = page.totalPage;
            state.pageNumber = page.pageNumber;
            state.totalRow = page.totalRow;
        },
        unionpay_entrust_set(state, obj) {
            if (obj !== undefined) {
                state.unionpayEntrust = kit.clone(obj);
            }
        }
    },
    actions: {
        get_entrust_trade_list: function ({ commit, state }, param) {
            if (param && !param.pn) {
                param.pn = state.pageNumber;
            }
            this._vm.$axios.post('/coll/entrust/trade/list', param).then((res) => {
                commit('set_entrust_trade_list', res);
            });
        }
    }
};

export default unionpayEntrust;
