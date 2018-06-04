<style lang="less">
    @import '../../../styles/common.less';
    @import './preview.less';


    /* table 样式 */
    /*table {*/
        /*border-top: 1px solid #ccc;*/
        /*border-left: 1px solid #ccc;*/
    /*}*/
    /*table td,*/
    /*table th {*/
        /*border-bottom: 1px solid #ccc;*/
        /*border-right: 1px solid #ccc;*/
        /*padding: 3px 5px;*/
    /*}*/
    /*table th {*/
        /*border-bottom: 2px solid #ccc;*/
        /*text-align: center;*/
    /*}*/

    /*!* blockquote 样式 *!*/
    /*blockquote {*/
        /*display: block;*/
        /*border-left: 8px solid #d0e5f2;*/
        /*padding: 5px 10px;*/
        /*margin: 10px 0;*/
        /*line-height: 1.4;*/
        /*font-size: 100%;*/
        /*background-color: #f1f1f1;*/
    /*}*/

    /*!* code 样式 *!*/
    /*code {*/
        /*display: inline-block;*/
        /**display: inline;*/
        /**zoom: 1;*/
        /*background-color: #f1f1f1;*/
        /*border-radius: 3px;*/
        /*padding: 3px 5px;*/
        /*margin: 0 3px;*/
    /*}*/
    /*pre code {*/
        /*display: block;*/
    /*}*/

    /*!* ul ol 样式 *!*/
    /*ul, ol {*/
        /*margin: 10px 0 10px 20px;*/
    /*}*/
</style>
<template>

    <div>
        <Modal
                v-model="artViewModal"
                :mask-closable="false"
                width="900"
                :styles="{top: '20px'}"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>文章预览</span>
            </p>
            <Card>
                <h1>{{ art.title }}</h1>
                <p class="preview-publish-time">
                    <Icon type="android-alarm-clock"></Icon>
                    &nbsp;发布时间：{{ art.pAtTxt }}
                </p>
                <div class="preview-content-con" v-html="art.text"></div>
                <div class="preview-classifition-con">
                    <b class="preview-tip">
                        <Icon type="navicon-round"></Icon>
                        &nbsp;栏目：</b>
                    <a class="preview-classifition-item" v-for="item in art.taxList" :key="item.id">
                        <Icon type="android-folder-open"></Icon>
                        {{ item.title }}
                    </a>
                </div>
            </Card>
        </Modal>
    </div>
</template>
<script>

    import {mapState} from 'vuex'
    export default {
        name:'art-view',
        computed: {
            ...mapState({
                'art': state => state.art.art,
            })
        },
        data () {
            return {
                artViewModal: false,
            };
        },
        methods:{
            open(){
                this.artViewModal=true;
            }
        }



    }
</script>