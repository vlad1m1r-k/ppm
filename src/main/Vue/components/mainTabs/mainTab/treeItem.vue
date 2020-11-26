<template>
    <ul>
        <li>
            {{ item.name }}
        </li>
        <template v-for="child in item.children">
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
        return {}
    }
}
</script>

<style scoped>

</style>