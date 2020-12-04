<template>
    <div>
        <div class="row">
            <div class="col-md-auto bg-light">
                <span @click="updateTree" style="cursor: pointer; position: absolute">&#x21BA;</span>
                <tree-item :item="tree" :selected-item="selectedItem" @item-select="selectItem"></tree-item>
            </div>
            <div class="col p-0">
                <item-view :item="selectedItem"></item-view>
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
        }
    },
    methods: {
        async updateTree() {
            this.$eventHub.$emit("show-msg", "");
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
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
            }
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
    },
    created() {
        this.$eventHub.$on("update-tree", this.updateTree);
    },
    beforeDestroy() {
        this.$eventHub.$off("update-tree");
    }
}
</script>

<style scoped>

</style>