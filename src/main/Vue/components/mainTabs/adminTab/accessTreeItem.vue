<template>
    <dl>
        <dt>
            <div class="row">
                <div class="col">{{ item.name }}</div>
                <div class="col col-sm align-self-end">
                    {{ item.accessNA }} NA
                </div>
                <div class="col col-sm align-self-end">
                    {{ item.accessPT }} PT
                </div>
                <div class="col col-sm align-self-end">
                    {{ item.accessRO }} RO
                </div>
                <div class="col col-sm align-self-end">
                    {{ item.accessRW }} RW
                </div>
            </div>
        </dt>
        <dd v-for="child in item.children" :key="'ati' + child.id">
            <access-tree-item :item="child"></access-tree-item>
        </dd>
    </dl>
</template>

<script>
export default {
    name: "accessTreeItem",
    props: {item: Object},
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language
        }
    },
    methods: {

    }
}
</script>

<style scoped>
dd {
    margin-left: 22px;
    margin-bottom: auto;
}
dl {
    margin-bottom: auto;
}
</style>