<style lang="less">
    @import './own-space.less';
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="person"></Icon>
                商户中心
            </p>
            <div>
                <Row>


                    <i-col  class="mc-col"  :xs="24" :sm="24" :md="12"  :lg="12">
                        <Card style="height: 250px">
                            <p slot="title">商户基本信息</p>

                            <div class="mc-label">商户名称：</div>
                            <div class="mc-content">{{merInfo.merchantName}}</div>
                            <div class="mc-label">商户编号：</div>
                            <div class="mc-content">{{merInfo.merchantNo}}</div>
                            <div class="mc-label">商户类型：</div>
                            <div class="mc-content">{{merInfo.merTypeTxt}}</div>

                            <div style="margin-left: 5px;">
                                <a :href="downUrl">
                                    <Button type="primary" style="margin-top: 20px">下载代扣授权书</Button>
                                </a>
                            </div>

                        </Card>

                    </i-col>
                    <i-col  :xs="24" :sm="24" :md="12"  :lg="12" class="mc-col">
                        <Card style="height: 250px">

                            <div align="center">
                                <div style="margin-top: 10px"><img :src="urlEWImg" width="150"></div>
                                <div style="margin-top: 10px">客户请扫描此二维码进行银行卡绑定</div>
                            </div>

                        </Card>

                    </i-col>
                    <i-col  :xs="24" :sm="24" :md="12"  :lg="12" class="mc-col">
                        <Card>
                            <p slot="title">负责人信息</p>


                            <div class="mc-label">负责人名称：</div>
                            <div class="mc-content">{{merInfo.perName}}</div>

                            <div class="mc-label">身份证号码：</div>
                            <div class="mc-content">{{merInfo.cardID}}</div>

                            <div class="mc-label">负责人手机号：</div>
                            <div class="mc-content">{{merInfo.mobile}}</div>

                            <div class="mc-label">负责人Email：</div>
                            <div class="mc-content">{{merInfo.email}}</div>

                            <div class="mc-label">负责人联系地址：</div>
                            <div class="mc-content">{{merInfo.address}}</div>

                            <div class="mc-label">备用联系地址2：</div>
                            <div class="mc-content">{{merInfo.mobile1}}</div>

                            <div class="mc-label">备用联系地址2：</div>
                            <div class="mc-content">{{merInfo.mobile2}}</div>


                        </Card>
                    </i-col>
                    <i-col  :xs="24" :sm="24" :md="12"  :lg="12" class="mc-col">
                        <Card>
                            <p slot="title">清算信息</p>
                            <div class="mc-label">预存手续费余额：</div>
                            <div class="mc-content">{{merInfo.feeAmount}}元</div>
                            <div class="mc-label">最大代扣金额：</div>
                            <div class="mc-content">{{merInfo.maxTradeAmount}}元</div>
                            <div class="mc-label">清算银行卡号：</div>
                            <div class="mc-content">{{merInfo.bankNo}}</div>
                            <div class="mc-label">清算银行卡户名：</div>
                            <div class="mc-content">{{merInfo.bankAccountName}}</div>
                            <div class="mc-label">清算银行卡预留手机号：</div>
                            <div class="mc-content">{{merInfo.bankPhone}}</div>
                            <div class="mc-label">清算银行卡开户行全名：</div>
                            <div class="mc-content">{{merInfo.bankName}}</div>
                            <div class="mc-label">清算银行卡开户行行号：</div>
                            <div class="mc-content">{{merInfo.bankCode}}</div>

                        </Card>

                    </i-col>


                    <i-col  :xs="24" :sm="24" :md="8"  :lg="8" class="mc-col">
                        <Card>
                            <p slot="title">手持身份证照片</p>
                            <div align="center">
                            <img :src="urlCard" class="mc-img">
                            </div>
                        </Card>

                    </i-col>
                    <i-col  :xs="24" :sm="24" :md="8"  :lg="8" class="mc-col">
                        <Card>
                            <p slot="title">身份证正面</p>
                            <div align="center">
                            <img :src="urlCardZ" class="mc-img">
                            </div>
                        </Card>

                    </i-col>
                    <i-col  :xs="24" :sm="24" :md="8"  :lg="8" class="mc-col">
                        <Card >
                            <p slot="title">身份证反面</p>
                            <div align="center">
                            <img :src="urlCardF" class="mc-img">
                            </div>
                        </Card>

                    </i-col>
                </Row>
                <div slot="footer">

                </div>
            </div>
        </Card>
    </div>
</template>

<script>
    import consts from '../../libs/consts'
    import ICol from "iview/src/components/grid/col";

    export default {
        components: {ICol},
        name: 'merCenter',
        data() {

            return {
                merInfo: {},
                urlCard: '',
                urlCardZ: '',
                urlCardF: '',
                urlEWImg: '',
                downUrl: '',
            };
        },
        methods: {
            downloadDoc() {
                this.$axios.post(this.downUrl)

            },
        },
        mounted() {
            this.$store.dispatch('login_merInfo').then((res) => {
                let now = new Date().getTime();

                this.merInfo = res
                this.urlCard = consts.devLocation + "/cmn/act04?tt=" + now + "&picid=" + this.merInfo.cardImg;
                this.urlCardF = consts.devLocation + "/cmn/act04?tt=" + now + "&picid=" + this.merInfo.cardF;
                this.urlCardZ = consts.devLocation + "/cmn/act04?tt=" + now + "&picid=" + this.merInfo.cardZ;
                this.urlEWImg = consts.devLocation + "/cmn/act05?tt=" + now + "&merNo=" + this.merInfo.merchantNo;
                this.downUrl = consts.devLocation + "/cmn/act06";
            });
        }
    };
</script>
<style>

    .mc-label {
        float: left;
        text-align: right;
        font-weight: bold;
        padding: 5px 10px;
        font-size: 13px;
    }

    .mc-content {
        font-size: 13px;
        padding: 5px 5px;
    }
    .mc-col{
        padding: 5px;
    }
    .mc-img {
        display: block;
        width: 100%;

        height: auto;
    }
</style>
