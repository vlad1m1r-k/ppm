<template>
    <ul>
        <li>
            <span class="caret" :class="{'caret-down': isOpen}" @click="isOpen = !isOpen">{{ item.name }}</span>
        </li>
        <template v-for="child in item.children" v-show="isOpen">
            <tree-item :item="child"></tree-item>
        </template>
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
.caret::before {
    content: "\25B7";
    color: black;
    margin-right: 6px;
    cursor: pointer;
}
.caret-down::before {
    transform: rotate(90deg);
}
</style>