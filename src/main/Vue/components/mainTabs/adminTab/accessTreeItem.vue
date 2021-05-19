<template>
    <dl>
        <dt>
            <div class="row ml-0 mr-0">
                <div class="col">{{ item.name }}</div>
                <div class="col-auto pl-0">
                    <input type="checkbox" :checked="item.accessNA" @click="removeAccess">
                    NA
                </div>
                <div class="col-auto pl-0" :class="{'bg-info': item.accessPT}">
                    <input type="checkbox" :checked="item.accessPT" @click="removeAccess">
                    PT
                </div>
                <div class="col-auto pl-0" :class="{'bg-success': item.accessRO}">
                    <input type="checkbox" :checked="item.accessRO" @click="removeAccess">
                    RO
                </div>
                <div class="col-auto pl-0" :class="{'bg-danger': item.accessRW}">
                    <input type="checkbox" :checked="item.accessRW" @click="removeAccess">
                    RW
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
        async removeAccess() {
            return false;
        }
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