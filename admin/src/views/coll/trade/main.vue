<template>
    <div>
        <Card>
            <Tabs>
                <TabPane label="交易列表">
                    <tradeContent ref="tc"></tradeContent>
                </TabPane>
            </Tabs>
        </Card>
        <!-- <Tabs>
            <TabPane label="交易管理">
                <tradeContent ref="tc"></tradeContent>
            </TabPane>
            <TabPane label="批量交易管理">
                <batchTradeContent ref="btc"></batchTradeContent>
            </TabPane>
        </Tabs> -->
    </div>
</template>

<script>
    import { mapState } from 'vuex'
    import tradeContent from './tradeContent.vue'

    export default {
        components: {
            tradeContent: tradeContent,
        },
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>