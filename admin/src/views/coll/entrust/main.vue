<template>
    <Card>
        <Tabs>
            <TabPane label="委托状态管理">
                <entrustStatusPaneContent></entrustStatusPaneContent>
            </TabPane>
            <TabPane label="交易流水">
                <entrustTradePaneContent></entrustTradePaneContent>
            </TabPane>
        </Tabs>
    </Card>
</template>

<script>
    import { mapState } from 'vuex'
    import entrustStatusPaneContent from './statusPaneContent.vue'
    import entrustTradePaneContent from './tradePaneContent.vue'
    export default {
        components: {
            entrustStatusPaneContent: entrustStatusPaneContent,
            entrustTradePaneContent: entrustTradePaneContent,
        }
    }
</script>
<style lang="less">
    @import '../../../styles/common.less';
</style>