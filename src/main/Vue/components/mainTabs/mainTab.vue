<template>
    <div>
        <div class="row">
            <div class="col-md-auto bg-light p-0">
                <span @click="updateTree" style="cursor: pointer; position: absolute">&#x21BA;</span>
                <tree-item :item="tree" :selected-item="selectedItem" @item-select="selectItem"></tree-item>
            </div>
            <div class="col p-0">
                <item-view :item="selectedItem" v-if="!trash"></item-view>
                <trash-view :item="selectedItem" v-if="trash"></trash-view>
            </div>
        </div>
    </div>
</template>

<script>
import treeItem from "./mainTab/treeItem.vue";
import itemView from "./mainTab/itemView.vue";
import trashView from "./mainTab/trashView.vue";

export default {
    name: "mainTab",
    components: {
        'tree-item': treeItem,
        'item-view': itemView,
        'trash-view': trashView
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            tree: {},
            selectedItem: {},
            trash: false
        }
    },
    methods: {
        async updateTree() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await this.cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/container/getTree",
                    method: "POST",
                    data: encryptedData
                });
                this.tree = this.cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        selectItem(evt) {
            this.selectedItem = evt;
        },
        toggleTrash(currentTab) {
            if (currentTab === "mainTab") {
                this.trash = !this.trash;
            } else {
                this.trash = true;
            }
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
        this.eventHub.on("update-tree", this.updateTree);
        this.eventHub.on("toggle-trash", this.toggleTrash);
    },
    beforeUnmount() {
        this.eventHub.off("update-tree");
        this.eventHub.off("toggle-trash");
    }
}
</script>

<style scoped>

</style>