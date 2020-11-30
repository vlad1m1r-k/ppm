<template>
    <ul>
        <li style="white-space: nowrap">
            <span class="caret" :class="{'caret-down': isOpen}" @click="isOpen = !isOpen"></span>
            <span class="branch">{{ item.name }}</span>
        </li>
        <div v-show="isOpen">
            <tree-item :item="child" v-for="child in item.children"></tree-item>
        </div>
    </ul>
</template>

<script>
export default {
    name: "treeItem",
    props: {
        item: Object
    },
    data() {
        return {
            isOpen: false
        }
    }
}
</script>

<style scoped>
ul {
    list-style-type: none;
}
.caret {
    cursor: pointer;
}
.caret::before {
    content: "\25B7";
    color: black;
    margin-right: 6px;
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
</style>