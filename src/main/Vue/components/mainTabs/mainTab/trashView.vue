<template>
    <div class="decor-iv">
        <div class="row m-0">
            <div class="col">
                <button class="btn btn-sm btn-outline-secondary" :class="{selected: currentTab === 'containers'}" @click="currentTab = 'containers'">{{ language.data.di2 }}</button>
                <button class="btn btn-sm btn-outline-secondary" :class="{selected: currentTab === 'items'}" @click="currentTab = 'items'">{{ language.data.di3 }}</button>
            </div>
        </div>
        <div class="row m-0">
            <div class="col">
                <component :item="item" :is="currentTab"></component>
            </div>
        </div>
    </div>
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
            currentTab: "items",
        }
    }
}
</script>

<style scoped>
.decor-iv {
    background-color: #eaeaea;
    border-radius: 3px;
}
.selected {
    border-bottom: 4px solid darkblue;
}
</style>