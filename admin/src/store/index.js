import Vue from 'vue';
import Vuex from 'vuex';

import app from './modules/app';
import user from './modules/user';
import param from './modules/param';
import role from './modules/role';
import art from './modules/art';
import res from './modules/res';
import tax from './modules/tax';
import home from './modules/home';
import merInfo from './modules/merInfo';
import cc from './modules/collection_clear';
import collTrade from './modules/collection_trade';
import collEntrust from './modules/collection_entrust';
import collRealtime from './modules/collection_realtime';
import collBatch from './modules/collection_batch';
import collQuery from './modules/collection_query';
import collUndo from './modules/collection_undo';
import collReconciliation from './modules/collection_reconciliation';
import collMerCust from './modules/collection_trade_mercust';
import merCust from './modules/merCust';
import unionpayEntrust from './modules/unionpay_entrust';
import unionpayCollection from './modules/unionpay_collection';
import merUser from './modules/merUser';
Vue.use(Vuex);

const store = new Vuex.Store({
    state: {
        spinShow: false,
        uploadPicMaxSize: 5242880,
        ignoreSpinshow: false,

    },
    mutations: {
        upadteSpinshow(state, p) {
            if (!this.state.ignoreSpinshow)
                state.spinShow = p;
        },
        updateIgnoreSpinshow(state, p) {
            state.ignoreSpinshow = p
        }
    },
    actions: {

    },
    modules: {
        app,
        user,
        param,
        role,
        art,
        res,
        tax,
        home,
        cc,
        merInfo,
        merCust,
        collTrade,
        collEntrust,
        collRealtime,
        collBatch,
        collQuery,
        collUndo,
        collReconciliation,
        collMerCust,
        unionpayEntrust,
        unionpayCollection,
        merUser
    }
});

export default store;
