<template>
    <ul class="tr-ul">
        <li style="white-space: nowrap" :draggable="item.access === 'RW' && item.name !== 'root'" @dragover="dragover"
            @dragstart="startDrag($event, item)" @drop="dropHandler($event, item)">
            <span class="caret" :class="{'caret-down': isOpen}" @click="isOpen = !isOpen" v-if="item.children && item.children.length > 0"></span>
            <span class="branch" :class="{'tr-selected': item.id === selectedItem.id}" @click="$emit('item-select', item)" @dblclick="isOpen = !isOpen">
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
        }
    },
    methods: {
        dragover(evt) {
            if (this.item.access === "RW" && evt.dataTransfer.getData("id") != this.item.id) {
                evt.preventDefault();
            }
        },
        startDrag(evt, item) {
            evt.dataTransfer.dropEffect = "move";
            evt.dataTransfer.effectAllowed = "move";
            evt.dataTransfer.setData("id", item.id);
            evt.dataTransfer.setData("name", item.name);
        },
        async dropHandler(evt, item) {
            this.eventHub.emit("show-msg", "");
            const confResult = confirm(this.language.data.cf1 + " \"" + evt.dataTransfer.getData("name") +
                    "\" " + this.language.data.cf2 + " \"" + item.name + "\" ?");
            if (confResult) {
                try {
                    const token = await this.tokenProvider.getToken();
                    const encryptedData = await cryptoProvider.encrypt({
                        token: token,
                        item: parseInt(evt.dataTransfer.getData("id")),
                        moveTo: item.id
                    });
                    const answer = await $.ajax({
                        url: "/container/move",
                        method: "POST",
                        data: encryptedData
                    });
                    const data = cryptoProvider.decrypt(answer);
                    if (data.message) {
                        this.eventHub.emit("show-msg", this.language.data[data.message]);
                    } else {
                        this.eventHub.emit("update-tree");
                    }
                } catch (e) {
                    this.eventHub.emit("show-msg", this.errorParser(e));
                }
            }
        },
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