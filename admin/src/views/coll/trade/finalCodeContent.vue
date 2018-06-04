<template>
    <div class="fcc">

        <div class="fcc-refresh">
            <div class="fcc-refresh-content" v-on:click="syncStatus" v-if="!syncStatusLoadingShow">
                <Tooltip placement="top" :delay="1000" :transfer="transfer">
                    <Icon type="refresh"></Icon>
                    <div slot="content">
                        <p>同步订单状态</p>
                    </div>
                </Tooltip>
            </div>
            <Spin v-if="syncStatusLoadingShow">
                <SyncStatusLoading></SyncStatusLoading>
            </Spin>
        </div>

        <div class="fcc-steps">
            <div class="fcc-steps-item" v-bind:class="computedResItemClass">
                <div class="fcc-steps-tail">
                    <i></i>
                </div>
                <div class="fcc-steps-head">
                    <div class="fcc-steps-head-inner">
                        <span>发起交易</span>
                    </div>
                </div>
                <div class="fcc-steps-main" v-bind:class="computedResClass">
                    <div class="fcc-steps-main-code-block">
                        <div class="fcc-steps-fs-15">{{resCode}}</div>
                        <span class="fcc-steps-main-intro-text">响应码</span>
                    </div>
                    <div class="fcc-steps-main-block">
                        <div class="fcc-steps-fs-15">
                            <span class="fcc-steps-main-msg">{{resMsg}}</span>
                        </div>
                        <span class="fcc-steps-main-intro-text">响应信息</span>
                    </div>
                </div>
            </div>

            <div class="fcc-steps-item" v-bind:class="computedResultItemClass">
                <div class="fcc-steps-tail">
                    <i></i>
                </div>
                <div class="fcc-steps-head">
                    <div class="fcc-steps-head-inner">
                        <span>处理结果</span>
                    </div>
                </div>
                <div class="fcc-steps-main" v-bind:class="computedResultClass">
                    <div class="fcc-steps-main-code-block">
                        <div class="fcc-steps-fs-15">{{resultCode}}</div>
                        <span class="fcc-steps-main-intro-text">响应码</span>
                    </div>
                    <div class="fcc-steps-main-block">
                        <div class="fcc-steps-fs-15">
                            <span class="fcc-steps-main-msg">{{resultMsg}}</span>
                        </div>
                        <span class="fcc-steps-main-intro-text">响应信息</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import Vue from 'vue'
    import { mapState } from 'vuex'
    import env from '../../../../build/env'
    import axios from 'axios'
    import qs from 'qs'
    import syncStatusLoading from './syncStatusLoading.vue'

    function isBlank(o) {
        return !o || o === '';
    }

    export default {
        name: 'FinalCodeContent',
        props: [
            'index', 'tradeInfo', 'isShow'
        ],
        components: {
            SyncStatusLoading: syncStatusLoading
        },
        computed: {
            ...mapState({
                'tradeList': state => state.collTrade.tradeList,
                'totalPage': state => state.collTrade.totalPage,
                'pageNumber': state => state.collTrade.pageNumber,
                'total': state => state.collTrade.totalRow,
                'collTrade': state => state.collTrade.collTrade,
            }),
            highlightIndex: {
                get: function () {
                    return this.$store.state.collTrade.highlightIndex
                },
                set: function (newValue) {
                    this.$store.state.collTrade.highlightIndex = newValue
                }
            },
            resCode: function () {
                return isBlank(this.tradeInfo.resCode) ? '暂无' : this.tradeInfo.resCode;
            },
            resMsg: function () {
                return isBlank(this.tradeInfo.resMsg) ? '暂无' : this.tradeInfo.resMsg;
            },
            resultCode: function () {
                return isBlank(this.tradeInfo.resultCode) ? '暂无' : this.tradeInfo.resultCode;
            },
            resultMsg: function () {
                return isBlank(this.tradeInfo.resultMsg) ? '暂无' : this.tradeInfo.resultMsg;
            },
            resIsError: function () {
                return (!isBlank(this.tradeInfo.resCode) && !(this.tradeInfo.resCode === '00'));
            },
            resustIsError: function () {
                return (!isBlank(this.tradeInfo.resultCode) && !(this.tradeInfo.resultCode === '00'));
            },
            computedResClass: function () {
                return {
                    'fcc-steps-main-text-danger': (!isBlank(this.tradeInfo.resCode) && !(this.tradeInfo.resCode === '00'))
                };
            },
            computedResultClass: function () {
                return {
                    'fcc-steps-main-text-danger': this.resIsError || this.resustIsError
                };
            },
            computedResItemClass: function () {
                return {
                    'fcc-steps-success': (!isBlank(this.tradeInfo.resCode) && (this.tradeInfo.resCode === '00')),
                    'fcc-steps-alert': isBlank(this.tradeInfo.resCode),
                    'fcc-steps-error': this.resIsError,
                };
            },
            computedResultItemClass: function () {
                return {
                    'fcc-steps-success': (!isBlank(this.tradeInfo.resultCode) && (this.tradeInfo.resultCode === '00')),
                    'fcc-steps-alert': !this.resIsError && isBlank(this.tradeInfo.resultCode),
                    'fcc-steps-error': this.resIsError || this.resustIsError,
                };
            },
        },
        methods: {
            syncStatus() {
                this.syncStatusLoadingShow = true;
                setTimeout(() => {
                    this.syncStatusLoadingShow = false;
                }, 10000);
                let axiosIns = this.createAxios();
                axiosIns.post('/coll/trade/syncOrderStatus', this.tradeInfo).then((res) => {
                    this.highlightIndex = this.index;
                    this.isShow[this.index]=false;
                    Vue.set(this.tradeList, this.index, res.data);
                    this.syncStatusLoadingShow = false;
                });
            },
            createAxios() {
                let axiosIns = axios.create({});
                if (env === 'development') {
                    axiosIns.defaults.baseURL = '/api';
                } else if (env === 'debug') {
                    axiosIns.defaults.baseURL = '/api';
                } else if (env === 'production') {
                    axiosIns.defaults.baseURL = '';
                }
                axiosIns.defaults.headers.post['X-Requested-With'] = 'XMLHttpRequest';
                axiosIns.defaults.headers.get['X-Requested-With'] = 'XMLHttpRequest';
                axiosIns.defaults.responseType = 'json';
                axiosIns.defaults.transformRequest = [function (data) {
                    if (data instanceof FormData) {
                        return data;
                    }
                    return qs.stringify(data);
                }
                ];
                return axiosIns;
            }
        },
        data() {
            return {
                transfer: true,
                syncStatusLoadingShow: false,
            }
        }
    }

</script>

<style lang="less">
    @import '../../../styles/common.less';
    @import './fcc-steps.less';
</style>