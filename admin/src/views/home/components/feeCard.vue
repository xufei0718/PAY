

<template>
    <Row :gutter="10">
        <Col span="24" style="margin-bottom: '10px">
            <Card>
                <Row >
                    <Col :xs="24" :sm="12" :md="12"  align="center" class="feeCol">
                    <div style="font-size: 13px; font-weight: bold; color: #ffffff; background-color: #2d8cf0; padding: 5px; ;margin: 0px 1px; ">加急手续费</div>
                    <table>
                        <div v-for="item in merFeeListJ" class="feeList">
                            <tr>
                                <td width="230" align="center">

                                    <span v-if="item.amountLower > '0'">{{ item.amountLower }}<</span>
                                    交易金额
                                    <span v-if="item.amountUpper > '0'"><={{ item.amountUpper }}</span>
                                </td>
                                <td width="80" align="center">
                                    <span v-if="item.feeType === '1'">每笔{{ item.amount }}元</span>
                                    <span v-else-if="item.feeType  === '2'">{{item.ratio*100}}%</span>
                                </td>

                            </tr>
                        </div>
                    </table>

                    </Col>
                    <Col :xs="24" :sm="12" :md="12"  align="center" class="feeCol">
                    <div style="font-size: 13px; font-weight: bold;color: #ffffff; background-color: #2d8cf0; padding: 5px;margin: 0px 1px;">标准手续费</div>
                    <table>
                        <div v-for="item in merFeeListB" class="feeList">
                            <tr>
                                <td width="230" align="center">

                                    <span v-if="item.amountLower > '0'">{{ item.amountLower }}<</span>
                                    交易金额
                                    <span v-if="item.amountUpper > '0'"><={{ item.amountUpper }}</span>
                                </td>
                                <td width="80" align="center">
                                    <span v-if="item.feeType === '1'">每笔{{ item.amount }}元</span>
                                    <span v-else-if="item.feeType  === '2'">{{item.ratio*100}}%</span>
                                </td>

                            </tr>
                        </div>
                    </table>
                    </Col>
                </Row>
                <div style="font-size: 13px; font-weight: bold; padding-right: 10px" align="right">
                    手续费账户余额：{{merInfo.feeAmount}}元
                </div>
            </Card>
        </Col>

    </Row>
</template>

<script>

export default {


    name: 'feeCard',

    mounted() {
        //页面加载时或数据方法
            this.$axios.post('/home/fee').then((res) => {
                     this.merFeeListJ = res.feeListJ;
                    this.merFeeListB = res.feeListB;
                    this.merInfo = res.merInfo;
            })

    },
    data () {
        return {
            merFeeListJ: [],
            merFeeListB: [],
            merInfo:{},
        }
    },
};
</script>
<style lang="less">
    .feeList {
        font-size: 12px;
        line-height: 30px;

    }
    .feeCol{
          min-height: 160px;
    }
</style>
