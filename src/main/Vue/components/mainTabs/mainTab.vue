<template>
    <div>
        <div class="row">
            <div class="col-md-auto bg-light p-0">
                <span @click="updateTree" style="cursor: pointer; position: absolute">&#x21BA;</span>
                <tree-item :item="tree" :selected-item="selectedItem" @item-select="selectItem"></tree-item>
            </div>
            <div class="col p-0">
                <item-view :item="selectedItem" :search-data="searchData" v-if="currentTab.name === 'itemView'"></item-view>
                <trash-view :item="selectedItem" v-if="currentTab.name === 'trashView'"></trash-view>
                <search-view v-if="currentTab.name === 'searchView'"></search-view>
            </div>
        </div>
    </div>
</template>

<script>
import treeItem from "./mainTab/treeItem.vue";
import itemView from "./mainTab/itemView.vue";
import trashView from "./mainTab/trashView.vue";
import searchView from "./mainTab/searchView.vue";

export default {
    name: "mainTab",
    components: {
        'tree-item': treeItem,
        'item-view': itemView,
        'trash-view': trashView,
        searchView
    },
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            tree: {},
            selectedItem: {},
            searchData: null,
            currentTab: itemView
        }
    },
    methods: {
        async updateTree() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/container/getTree",
                    method: "POST",
                    data: encryptedData
                });
                this.tree = cryptoProvider.decrypt(answer);
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        selectItem(evt, data) {
            this.selectedItem = evt;
            if (data) {
                this.searchData = data;
            } else {
                this.searchData = null;
            }
        },
        toggleTrash(currentTab) {
            if (currentTab === "mainTab") {
                this.currentTab = this.currentTab.name === "itemView" ? trashView : itemView;
            } else {
                this.currentTab = trashView;
            }
        },
        showMain() {
            this.currentTab = itemView;
        },
        searchResult(data) {
            this.currentTab = searchView;
            //TODO
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
        this.eventHub.on("search-result", this.searchResult);
        this.eventHub.on("show-main", this.showMain);
    },
    beforeUnmount() {
        this.eventHub.off("update-tree", this.updateTree);
        this.eventHub.off("toggle-trash", this.toggleTrash);
        this.eventHub.off("search-result", this.searchResult);
        this.eventHub.off("show-main", this.showMain);
    }
}
</script>

<style scoped>

</style>