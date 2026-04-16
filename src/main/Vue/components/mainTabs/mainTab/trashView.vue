<template>
    <button class="btn blue" :class="{ selected: currentTab === 'containers' }" @click="currentTab = 'containers'">{{ language.data.di2 }}</button>
    <button class="btn blue" :class="{ selected: currentTab === 'items' }" @click="currentTab = 'items'">{{ language.data.di3 }}</button>
    <component :item="item" :is="currentTab"></component>
</template>

<script>
import containers from "./trashView/containers.vue";
import items from "./trashView/items.vue";

export default {
    name: "trashView",
    components: {
        containers, items
    },
    props: {
        item: Object
    },
    data() {
        return {
            language: this.$root.$data.language,
            currentTab: "containers",
        }
    }
}
</script>

<style scoped>

</style>