<template>
    <div style="height: 100%">
        <vue-terminal :query="query"/>
    </div>
</template>

<script>
import VueTerminal from "../components/terminal/vue-terminal";

export default {
    name: "Console",
    components: {VueTerminal},
    data() {
        return {
            query: {}
        }
    },
    created() {
        this.query = {
            id: this.$route.params.id ? this.$route.params.id : "",
            host: this.$route.params.host ? this.$route.params.host : "",
            port: this.$route.query.port ? this.$route.query.port : ""
        }
    }
}
</script>

<style scoped>

</style>