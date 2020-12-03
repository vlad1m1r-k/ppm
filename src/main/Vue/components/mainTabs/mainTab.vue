<template>
    <div>
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
            <div class="col-md-auto bg-light">
                <span @click="updateTree" style="cursor: pointer; position: absolute">&#x21BA;</span>
                <tree-item :item="tree" :selected-item="selectedItem" @msg-evt="displayMsg($event)" @update-tree="updateTree"
                           @item-select="selectItem"></tree-item>
            </div>
            <div class="col p-0">
                <item-view :item="selectedItem" @msg-evt="displayMsg($event)"></item-view>
            </div>
        </div>
    </div>
</template>

<script>
import treeItem from "./mainTab/treeItem.vue";
import itemView from "./mainTab/itemView.vue";

export default {
    name: "mainTab",
    components: {
        'tree-item': treeItem,
        'item-view': itemView
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            tree: {},
            selectedItem: {},
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
        },
        displayMsg(evt) {
            this.message = Vue.errorParser(evt);
        },
        selectItem(evt) {
            this.selectedItem = evt;
        }
    },
    watch: {
        'tokenProvider.token'(newVal, oldVal) {
            if (oldVal === null && newVal !== null) {
                this.updateTree();
            }
        },
        tree(newVal, oldVal) {
            if (oldVal.name === undefined) {
                this.selectedItem = this.tree;
            }
        }
    }
}
</script>

<style scoped>

</style>