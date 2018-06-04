<style lang="less">
    @import '../../../styles/common.less';
    @import 'upload.less';
    .ql-font-roboto {
        font-family: 'Roboto', sans-serif;
    }
</style>

<template>
    <div>
        <Modal
                v-model="artModal"
                :mask-closable="false"
                width="900"
                :styles="{top: '20px'}"
        >
            <p slot="header">
                <Icon type="information-circled"></Icon>
                <span>{{modalTitle}}</span>
            </p>
            <Row>
                <Col span="18" >
                <Card>
                    <Form :label-width="80">
                        <FormItem label="文章标题" >
                            <Input v-model="art.title" icon="android-list"/>
                        </FormItem>
                        <FormItem label="引用链接" >
                            <Input v-model="art.linkTo" icon="link"/>
                        </FormItem>
                        <FormItem label="摘要" >
                            <Input v-model="art.summary" type="textarea" :rows="4"
                                   icon="android-list"/>
                        </FormItem>
                    </Form>
                    <div class="margin-top-20" style="height: 400px;">
                        <div id="editorElem" style="text-align:left"></div>
                        <!--<quill-editor ref="myTextEditor"-->
                                      <!--v-model="art.text"-->
                                      <!--:options="editorOption" style="height: 300px;"-->
                                      <!--&gt;-->
                        <!--</quill-editor>-->
                        <!--<VueTinymce ref="mt" :setting="tinymceCfg" v-model="art.text" :imgUploadUrl="tinymceImgUploadUrl"></VueTinymce>-->
                    </div>
                </Card>
                </Col>
                <Col span="6" class="padding-left-10">
                <Card>
                    <p slot="title">
                        <Icon type="paper-airplane"></Icon>
                        发布
                    </p>
                    <p class="margin-top-10">
                        <Icon type="android-time"></Icon>
                        发布&nbsp;&nbsp;状&nbsp;&nbsp;&nbsp; 态：
                        <Select size="small" style="width:90px"  v-model="art.flag" disabled>
                            <Option v-for="item in articleStateList" :value="item.value" :key="item.value">{{ item.label
                                }}
                            </Option>
                        </Select>
                    </p>
                    <p class="margin-top-10">
                        <Icon type="flag"></Icon>
                        &nbsp;&nbsp;置顶：&nbsp;<b></b>
                        <Select size="small" style="width:90px"  v-model="art.top">
                            <Option v-for="item in yornList" :value="item.value" :key="item.value">{{ item.label }}
                            </Option>
                        </Select>
                    </p>
                    <p class="margin-top-10">
                        <Icon type="fireball"></Icon>
                        &nbsp;&nbsp;精华：&nbsp;<b></b>
                        <Select size="small" style="width:90px"  v-model="art.good">
                            <Option v-for="item in yornList" :value="item.value" :key="item.value">{{ item.label }}
                            </Option>
                        </Select>
                    </p>


                </Card>
                <div class="margin-top-10">
                    <Card>
                        <p slot="title">
                            <Icon type="navicon-round"></Icon>
                            分类目录
                        </p>

                                <div >
                                    <Tree :data="artTaxList" ref="artTaxSelected" show-checkbox></Tree>
                                </div>

                    </Card>
                </div>
                <div class="margin-top-10">
                    <Card>
                        <p slot="title">
                            <Icon type="image"></Icon>
                            缩略图
                        </p>
                        <div class="height-120px">
                            <Row type="flex" justify="center" align="middle" class="height-100">
                                <Upload
                                        action=""
                                        :before-upload="handleUpload"
                                        :max-size="uploadPicMaxSize"
                                >
                                    <span>选择图片上传&nbsp;&nbsp;</span>
                                    <Button type="ghost" icon="ios-cloud-upload-outline">上传文件</Button>
                                </Upload>

                            </Row>
                        </div>
                        <div class="margin-top-10" v-show="previewImg">
                            <Card>
                                <div>
                                    <div class="admin-upload-list">
                                        <template>
                                            <img :src="imgData">
                                            <div class="admin-upload-list-cover">
                                                <Icon type="ios-eye-outline" @click.native="handleView()"></Icon>
                                                <Icon type="ios-trash-outline" @click.native="handleRemove()"></Icon>
                                            </div>
                                        </template>
                                    </div>
                                </div>
                            </Card>
                        </div>
                    </Card>
                    <Modal title="查看图片" v-model="showImg" @on-cancel="artModal=true;showImg=false" @on-ok="artModal=true;showImg=false">
                        <img :src="imgData" v-if="showImg"
                             style="width: 100%">
                    </Modal>

                </div>
                </Col>
            </Row>
            <div slot="footer">
                <Button type="success" :loading="publishLoading" @click="handlePublish">保存</Button>
                <Button type="error" @click="artModal=false">关闭</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
//    import 'quill/dist/quill.core.css'
//    import 'quill/dist/quill.snow.css'
//    import 'quill/dist/quill.bubble.css'
    import env from '../../../../build/env';
    import {mapState} from 'vuex'
    import consts from '../../../libs/consts'
//    import {VueTinymce, Config} from '../../my-components/text-editor/'
//    import { quillEditor,Quill } from 'vue-quill-editor'
//    import {container, ImageExtend, QuillWatch} from '../../../libs/quill-img-upload'
//    import  ImageResize  from 'quill-image-resize-module';
//    Quill.register('modules/ImageExtend', ImageExtend)
//    Quill.register('modules/ImageResize', ImageResize)
    import E from 'wangeditor'

    var editor=null;
    export default {
        name: 'art-publish',
        computed: {
            ...mapState({
                'art': state => state.art.art,
                'artTaxList': state => state.art.artTaxList,
                'uploadPicMaxSize': state => state.uploadPicMaxSize,
            })
        },
        components:{
//            VueTinymce
//            quillEditor
        },

        data () {
            return {
                articleStateList: [{value: '01', label: '草稿'}, {value: '00', 'label': '发布'}],
                yornList: [{value: '0', label: '是'}, {value: '1', 'label': '否'}],
                artModal: false,
                isAdd: true,
                modalTitle: '新增文章',
                imgData: '',
                file: '',
                previewImg: false,
                showImg:false,
                publishLoading:false,
//                tinymceCfg: Object.assign(Config, {
//                    height: 200
//                }),
//                tinymceImgUploadUrl:consts.imgUploadUrl,
//                editorOption:{
//                        modules: {
//                            ImageResize: {},
//                            ImageExtend: {
//                                loading: true,
//                                name: 'file',
//                                size: 1,
//                                accept: 'image/png, image/gif, image/jpeg, image/bmp,',
//                                action: consts.imgUploadUrl,  // 必填参数 图片上传地址
//                                response: (res) => {
//                                    if(res.resCode&&res.resCode=='success'){
//                                        return res.resData
//                                    }else{
//                                        this.$Message.error('图片上传失败,请重试');
//                                    }
//                                },
//                                sizeError: () => {
//                                    this.$Message.error('图片过大,图片大小不能超过1M')
//                                },  // 图片超过大小的回调
//                                start: () => {},  // 可选参数 自定义开始上传触发事件
//                                end: () => {},  // 可选参数 自定义上传结束触发的事件，无论成功或者失败
//                                error: () => {},  // 可选参数 上传失败触发的事件
//                                success: () => {},  // 可选参数  上传成功触发的事件
//                                change: (xhr, formData) => {
//
//                                }
//                            },
//                            toolbar: {
//                                container: container,
//                                handlers: {
//                                    'image': function () {
//                                        QuillWatch.emit(this.quill.id)
//                                    }
//                                }
//                            }
//                            },
//                    placeholder: "输入任何内容，支持html",
//                    scrollingContainer: '#scrolling-container',
//                }
            }
        },
        methods: {
            open(title, isAdd){
                this.isAdd = isAdd
                this.modalTitle = title;
                this.artModal = true;
                this.showImg=false;
                this.file='';
                this.previewImg=false;
                this.imgData='';
                editor.txt.clear()
                if (isAdd){
                    this.$store.commit('set_art');
                }else{
                    editor.txt.html(this.art.text)
                }
            },
            handlePublish(){
                let vm=this;
                this.publishLoading=true;
                if(!this.art.title||this.art.title.length==0) {
                    this.$Message.error("请填写标题");
                    this.publishLoading=false;
                    return
                }

                let taxArray=this.$refs.artTaxSelected.getCheckedNodes();
                let taxIds='';
                for(let s of taxArray){
                    if(taxIds==''){
                        taxIds=s.id;
                    }else{
                        taxIds=taxIds+","+s.id
                    }
                }

                let thumbnailBase64=this.imgData

                //let text=this.$refs.mt.getContent();
                //let action=(this.art.id)?'update':'save'
                let param={'tax':taxIds,'thumbnailBase64':thumbnailBase64,'action':'save'}

                this.$store.dispatch('art_save',param).then((res)=>{
                    this.publishLoading = false;
                    if (res && res == 'success') {
                        vm.artModal = false;
                        this.$store.dispatch('art_list')
                    }
                })


            },
            handleUpload(file){
                let vm = this;
                let name=file.name;
                let ary=name.split(".");
                let format=['jpg', 'png', 'jpeg', 'gif', 'bmp', 'svg'];
                let fileSize=file.size;
                if(format.indexOf(ary[1].toLowerCase())==-1){
                    this.$Notice.warning({
                        title: '文件格式不正确',
                        desc: '文件 >>' + file.name + '<< 格式不正确，请选择jpg,png,jpeg,gif,bmp,svg文件。'
                    });
                    return false;
                }
                if(parseInt(fileSize)>parseInt(this.uploadPicMaxSize)){
                    this.$Notice.warning({
                        title: '文件大小错误',
                        desc: '文件 >>' +file.name+'<< 过大，不能超过' + parseInt(vm.uploadPicMaxSize)/1024/1024 + "M"
                    });
                    return false;
                }
                let reader = new FileReader();
                reader.onload = function () {
                    vm.imgData = this.result;
                }
                reader.readAsDataURL(file)
                this.previewImg = true
                return false;
            },
            handleRemove(){
                this.imgData = '';
                this.file = '';
                this.showImg=false;
                this.previewImg = false;
            },
            handleFormatError (file) {
                this.$Notice.warning({
                    title: '文件格式不正确',
                    desc: '文件 ' + file.name + ' 格式不正确，请选择图片文件。'
                });
            },
            handleView(){
                this.showImg=true;
                this.artModal=false;
            },
            handlerSizeError(file){
                let vm = this;
                this.$Notice.warning({
                    title: '文件大小错误',
                    desc: '文件大小不能超过' + vm.uploadPicMaxSize + "kb"
                });
            }

        },
        mounted () {
            this.$store.dispatch('art_tax_jsonArray');
            editor = new E('#editorElem')
            editor.customConfig.onchange = (html) => {
                this.$store.commit('set_art_content',html)
            }
            editor.customConfig.uploadImgShowBase64 = true
            editor.create()

        },

        destroyed () {
            editor=null;
        }
    };
</script>
