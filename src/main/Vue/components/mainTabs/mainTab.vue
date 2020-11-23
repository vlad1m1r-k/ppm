<template>
    <div>
        Main Tab
    </div>
</template>

<script>
import tokenProvider from "../../provider/tokenProvider";

export default {
    name: "mainTab",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            tree: {}
        }
    },
    methods: {
        updateTree() {
            const token = tokenProvider.getToken();
            //TODO
        }
    },
    watch: {
        'tokenProvider.token'(newVal, oldVal) {
            if (oldVal === null && newVal !== null) {
                this.updateTree();
            }
        }
    }
}
</script>

<style scoped>

</style>