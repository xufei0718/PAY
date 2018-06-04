import kit from '../../libs/kit';
const art = {
    state: {
        artList: [],
        totalPage: 0,
        pageNumber: 1,
        totalRow: 0,
        pageSize:15,
        art: {title: '', flag: '01', top: '1', good: '1', module: 'art'},
        artTaxList: []
    },
    mutations: {
        set_art_list(state, page){
            state.artList = page.list
            state.totalPage = page.totalPage
            state.totalRow = page.totalRow
            state.pageSize = page.pageSize
            state.pageNumber = page.pageNumber
        },
        set_art(state, obj){
            if (obj != undefined)
                state.art = kit.clone(obj);
            else {
                state.art = {title: '', flag: '01', top: '1', good: '1', module: 'art'};
            }
        },
        set_art_taxJsonarray(state, obj){
            state.artTaxList = obj;
        },
        set_artTax_list(state, list){
            state.artTaxList = list
        },
        set_art_content(state,txt){
            state.art.text=txt;
        }

    },
    actions: {
        art_list: function ({commit, state}, param) {
            this._vm.$axios.post('/ad04/list', param).then((res) => {
                commit('set_art_list', res)
            });
        },
        art_get: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad04/get', param).then((res) => {
                    commit('set_art', res.art)
                    commit('set_artTax_list', res.artTaxList)
                    resolve()
                });
            });
        },
        art_save: function ({commit, state}, param) {
            let vm = this._vm;
            let p = kit.clone(state.art);
            //p['text'] = param.text
            p['tax'] = param.tax
            p['thumbnailBase64'] = param.thumbnailBase64
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad04/' + param.action, p).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        art_del: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad04/del', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        art_publish: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad04/publish', param).then((res) => {
                    resolve(res.resCode);
                });
            });
        },
        art_tax_jsonArray: function ({commit, state}) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad05/treeJsonArray', {'module': 'art'}).then((res) => {
                    commit('set_art_taxJsonarray', res)
                    resolve()
                });
            });
        },

        art_tax_list: function ({commit, state}, param) {
            let vm = this._vm;
            return new Promise(function (resolve, reject) {
                vm.$axios.post('/ad04/getTax', param).then((res) => {
                    resolve(res)
                });
            });
        },

    }
}
export default art