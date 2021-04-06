<template>
    <ul>
        <li style="white-space: nowrap" :draggable="item.access === 'RW' && item.name !== 'root'" @dragover="dragover"
            @dragstart="startDrag($event, item)" @drop="dropHandler($event, item)">
            <span class="caret" :class="{'caret-down': isOpen}" @click="isOpen = !isOpen" v-if="item.children && item.children.length > 0"></span>
            <span class="branch" :class="{'selected': item.id === selectedItem.id}" @click="$emit('item-select', item)">
                {{ item.name }}
            </span>
        </li>
        <li v-show="isOpen">
            <tree-item :item="child" :selected-item="selectedItem" v-for="child in item.children"
                       @item-select="$emit('item-select', $event)" :key="'TREE' + child.id"></tree-item>
        </li>
    </ul>
</template>

<script>
export default {
    name: "treeItem",
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
        }
    },
    beforeUnmount() {
        if (this.item.id === this.selectedItem.id) {
            this.$emit('item-select', this.$parent.$props.item);
        }
    }
}
</script>

<style scoped>
ul {
    list-style-type: none;
    padding-left: 20px;
}
.caret {
    cursor: pointer;
}
.caret::before {
    content: "\25B7";
    color: black;
}
.caret-down::before {
    content: "\25BD";
}
.branch {
    cursor: pointer;
    background-color:#dadada;
    padding-right: 5px;
    padding-left: 5px;
    border-radius: .2rem;
}
.selected {
    border: 2px solid blue;
}
</style>