<template>
    <ul class="item-tree">
        <li style="white-space: nowrap">
            <span class="caret" :class="{'caret-down': isOpen}" @click="isOpen = !isOpen" v-if="item.children && item.children.length > 0"></span>
            <span class="branch" :class="{'tr-selected': item.id === selectedItem.id}" @click="$emit('item-select', item); isOpen = true" @dblclick="isOpen = !isOpen">
                {{ item.name }}
            </span>
        </li>
        <li v-show="isOpen">
            <tree-item :item="child" :selected-item="selectedItem" v-for="child in item.children"
                       @item-select="itemSelect" @expand-item="expand" :key="'TREE' + child.id"></tree-item>
        </li>
    </ul>
</template>

<script>
export default {
    name: "treeItem",
    emits: ["item-select", "expand-item"],
    props: {
        item: Object,
        selectedItem: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            tokenProvider: this.$root.$data.tokenProvider,
            isOpen: false
        }
    },
    watch: {
        item() {
            if (this.item.id === this.selectedItem.id) {
                this.$emit('item-select', this.item);
            }
            if (this.item.name === "root") {
                this.isOpen = true;
            }
        }
    },
    methods: {
        searchEvent(data) {
            if (this.item.id === data.cntId) {
                this.$emit('item-select', this.item, data);
                this.expand();
            }
        },
        expand() {
            this.isOpen = true;
            this.$emit("expand-item");
        },
        itemSelect(item, data) {
            this.$emit('item-select', item, data);
        }
    },
    beforeMount() {
        this.eventHub.on("search-event", this.searchEvent);
    },
    beforeUnmount() {
        this.eventHub.off("search-event", this.searchEvent);
        if (this.item.id === this.selectedItem.id) {
            this.$emit('item-select', this.$parent.$props.item);
        }
    }
}
</script>

<style scoped>

</style>