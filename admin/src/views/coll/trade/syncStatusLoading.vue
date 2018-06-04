<template>
    <div class="ssl_loading">
        <div class="ssl-loading-center">
            <div class="ssl-object ssl-object-three" v-bind:class="{ 'ssl-object-active': isActive }"></div>
            <div class="ssl-object ssl-object-two" v-bind:class="{ 'ssl-object-active': isActive }"></div>
            <div class="ssl-object ssl-object-one" v-bind:class="{ 'ssl-object-active': isActive }"></div>
        </div>
        <Tooltip placement="top" :transfer="transfer" :delay="1000" class="ssl-tooltip">
            <div slot="content">
                <p>{{contentText}}</p>
            </div>
            <div class="ssl-tooltip-holder"></div>
        </Tooltip>
    </div>
</template>

<script>
    export default {
        computed: {
            contentText: function () {
                return this.isActive ? '同步中...' : '订单不可同步';
            }
        },
        data() {
            return {
                transfer: true,
                isActive: true
            }
        }
    }
</script>
<style lang="less">
    @import './ssl.less';
</style>