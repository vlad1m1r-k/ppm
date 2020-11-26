<template>
    <div class="container-fluid">
        <div class="row">
            <div class="col">
                <div class="alert alert-danger" v-if="message">
                    {{ message }}
                    <button class="close" @click="message = ''">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col bg-light">
                <tree-item :item="tree"></tree-item>
            </div>
            <div class="col">
                Data
            </div>
        </div>
    </div>
</template>

<script>
import treeItem from "./mainTab/treeItem.vue";

export default {
    name: "mainTab",
    components: {
        'tree-item': treeItem
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            tree: {},
            message: ""
        }
    },
    methods: {
        async updateTree() {
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/container/getTree",
                    method: "POST",
                    data: encryptedData
                });
                this.tree = Vue.cryptoProvider.decrypt(answer);
            } catch (e) {
                this.message = Vue.errorParser(e);
            }
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