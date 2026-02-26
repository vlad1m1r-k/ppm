<template>
    <div class="modal-header">
        {{ language.data.sf2 }}
        <button class="btn-img cncl" @click="$emit('close-dlg')"></button>
    </div>
    <div style="margin-top: 10px;" v-if="searchResult.length === 0">{{ language.data.sf3 }}</div>
    <ul>
        <li v-for="cn in searchResult">
            <button class="btn link" @click="localSearch(cn.id)"><span v-html="markText(cn.name, searchText)"></span></button>
            <ul>
                <li v-for="nt in cn.notes">
                    {{ language.data.iv7 }}
                    <button class="btn link" @click="localSearch(cn.id, 'n', nt.id)">
                        <span v-html="markText(nt.name, searchText)"></span>
                    </button>
                </li>
            </ul>
            <ul>
                <li v-for="pw in cn.passwords">
                    {{ language.data.iv8 }}
                    <button class="btn link" @click="localSearch(cn.id, 'p', pw.id)">
                        <span v-html="markText(pw.name, searchText)"></span>
                    </button>
                </li>
            </ul>
            <ul>
                <li v-for="file in cn.files">
                    {{ language.data.fl1 }}
                    <button class="btn link" @click="localSearch(cn.id, 'f', file.id)">
                        <span v-html="markText(file.name, searchText)"></span>
                    </button>
                </li>
            </ul>
        </li>
    </ul>
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
            const regExp = new RegExp(text, "ig");
            return data.replaceAll(regExp, "<mark>$&</mark>");
        }
    }
}
</script>

<style scoped>

</style>