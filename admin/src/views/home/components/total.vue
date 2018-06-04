

<template>
    <Row :gutter="10">
        <Col :xs="24" :sm="12" :md="12" :style="{marginBottom: '10px'}">
        <infor-card
                id-name="user_created_count"
                :end-val="custCount"
                iconType="android-person-add"
                color="#2d8cf0"
                intro-text="今日新增客户数"
        />
        </Col>
        <Col :xs="24" :sm="12" :md="12" :style="{marginBottom: '10px'}">
        <infor-card
                id-name="visit_count"
                :end-val="amount"
                iconType="social-yen"
                color="#64d572"

                intro-text="今日交易金额"
        />
        </Col>
        <Col :xs="24" :sm="12" :md="12" :style="{marginBottom: '10px'}">
        <infor-card
                id-name="collection_count"
                :end-val="tradeCount"
                iconType="arrow-graph-up-right"
                color="#ffd572"
                intro-text="今日交易笔数"
        />
        </Col>
        <Col :xs="24" :sm="12" :md="12" :style="{marginBottom: '10px'}">
        <infor-card
                id-name="transfer_count"
                :end-val="feeAmount"
                iconType="card"
                color="#f25e43"
                intro-text="今日手续费总额"
        />
        </Col>
    </Row>
</template>

<script>

    import inforCard from './inforCard.vue';
export default {

    name: 'total',
    components: {
        inforCard
    },
    mounted() {

        //页面加载时或数据方法
        this.$axios.post('/home/total').then((res) => {
            this.amount=res.amount;
            this.custCount=res.custCount;
            this.tradeCount=res.tradeCount;
            this.feeAmount=res.feeAmount;
        })

    },
    data () {
        return {
            amount:0,
            custCount:0,
            tradeCount:0,
            feeAmount:0,
        }
    },
};
</script>
<style lang="less">
    .feeList {
        font-size: 12px;
        line-height: 30px;
    }
</style>
