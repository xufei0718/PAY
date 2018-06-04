import kit from '../../libs/kit';
const collTrade = {
    state: {
        tradeList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        collTrade: {},
        highlightIndex: -1,
        isAdmin: false
    },
    mutations: {
        set_trade_list(state, page) {
            state.tradeList = page.list;
            state.totalPage = page.totalPage;
            state.pageNumber = page.pageNumber;
            state.totalRow = page.totalRow;
        },
        collTrade_set(state, obj) {
            if (obj !== undefined) {
                state.collTrade = kit.clone(obj);
            }
        },
        isAdmin_set(state, obj) {
            state.isAdmin = obj;
        }
    },
    actions: {
        trade_list: function ({ commit, state }, param) {
            if (param && !param.pn) {
                param.pn = state.pageNumber;
            }
            this._vm.$axios.post('/coll/trade/list', param).then((res) => {
                commit('set_trade_list', res.pageInfo);
                commit('isAdmin_set', res.isAdmin);
            });
        },
        collTrade_set: function ({ commit, state }, param) {
            commit('collTrade_set', param);
        },
        trade_save: function ({ commit, state }, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/coll/trade/initiate', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        trade_detail_export: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/coll/trade/export/detail', param).then((res) => {
                    resolve(res);
                });
            });
        }
    }
};

export default collTrade;
