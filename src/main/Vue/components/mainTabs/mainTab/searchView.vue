<template>
    <div class="area-body">
        <div class="row m-0 area-header">
            <div class="col">{{ language.data.sf2 }}</div>
            <div class="col">
                <button class="close" @click="$emit('close-dlg')">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </div>
        <div class="row m-0">
            <div class="col">
                <div>{{ language.data.sf3 }}</div>
                <ul>
                    <li v-for="cn in searchResult">
                        <a href="javascript:void(0);" @click="localSearch(cn.id)"><span v-html="markText(cn.name, searchText)"></span></a>
                        <ul>
                            <li v-for="nt in cn.notes">
                                {{ language.data.iv7 }}
                                <a href="javascript:void(0);" @click="localSearch(cn.id, 'n', nt.id)">
                                    <span v-html="markText(nt.name, searchText)"></span>
                                </a>
                            </li>
                        </ul>
                        <ul>
                            <li v-for="pw in cn.passwords">
                                {{ language.data.iv8 }}
                                <a href="javascript:void(0);" @click="localSearch(cn.id, 'p', pw.id)">
                                    <span v-html="markText(pw.name, searchText)"></span>
                                </a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "searchView",
    props: {
        searchResult: Array,
        searchText: String
    },
    data() {
        return {
            language: this.$root.$data.language
        }
    },
    methods: {
        localSearch(cntId, type, itemId) {
            this.$emit('close-dlg');
            this.eventHub.emit("search-event", {
                cntId: cntId,
                type: type,
                itemId: itemId
            });
        },
        markText(data, text) {
            return data.replaceAll(text, "<mark>" + text + "</mark>");
        }
    }
}
</script>

<style scoped>

</style>