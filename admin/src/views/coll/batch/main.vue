<template>
    <div>
        <batchTradeContent ref="btc"></batchTradeContent>
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import batchTradeContent from './batchTradeContent.vue'

    export default {
        components: {
            batchTradeContent: batchTradeContent,
        },
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>