<style lang="less">
    @import "./home.less";
    @import "../../styles/common.less";
</style>
<template>
    <div class="home-main">
        <Row :gutter="10">
            <Col :md="12" :lg="12" v-if="isTotal">
            <total/>
            </Col>
            <Col :md="12" :lg="12" v-if="isFeeCard">
            <feeCard/>
            </Col>
        </Row>

    </div>
</template>

<script>
    import Cookies from 'js-cookie';
    import feeCard from './components/feeCard.vue';
    import total from './components/total.vue';


    export default {
        name: 'home',
        components: {
            feeCard,
            total,

        },

        mounted() {
            let serviceArray = eval("(" + Cookies.get('serviceArray') + ")");
            let menuArray = eval("(" + Cookies.get('menuArray') + ")");
            // console.info(serviceArray)
            // console.info(menuArray)
            for (let i = 0; i < serviceArray.length; i++) {
                if(serviceArray[i] === '/home/fee') {
                    this.isFeeCard = true;
                }
                if (serviceArray[i] === '/home/total') {
                    this.isTotal = true;
                }
            }

        },
        data() {
            return {
                isFeeCard: false,
                isTotal:false,
            }
        },


    };


</script>
