import kit from '../../libs/kit';
const collEntrust = {
    state: {
        entrustList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        collEntrust: {}
    },
    mutations: {
        set_entrust_list(state, page) {
            state.entrustList = page.list;
            state.totalPage = page.totalPage;
            state.pageNumber = page.pageNumber;
            state.totalRow = page.totalRow;
        },
        collEntrust_set(state, obj) {
            if (obj !== undefined) {
                state.collEntrust = kit.clone(obj);
            }
        }
    },
    actions: {
        get_entrust_list: function ({ commit, state }, param) {
            if (param && !param.pn) {
                param.pn = state.pageNumber;
            }
            this._vm.$axios.post('/coll/entrust/list', param).then((res) => {
                commit('set_entrust_list', res);
            });
        },
        collEntrust_set: function ({ commit, state }, param) {
            commit('collEntrust_set', param);
        },
        entrust_save: function ({ commit, state }) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/coll/entrust/establish', state.collEntrust).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        entrust_terminate: function ({ commit, state }) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/coll/entrust/terminate', state.collEntrust).then((res) => {
                    resolve(res.resCode);
                });
            });
        }
    }
};

export default collEntrust;
