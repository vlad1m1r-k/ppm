<template>
    <span style="display: flex">
        <button class="btn btn-sm btn-outline-secondary" :title="language.data.sf1" @click="doSearch" :disabled="!text">&#x1f50d;</button>
        <input type="text" class="form-control-sm align-middle" :placeholder="language.data.sf1" v-model="text" @keypress.enter="doSearch">
    </span>
</template>

<script>
export default {
    name: "searchForm",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            text: ""
        }
    },
    methods: {
        async doSearch() {
            if (this.text && this.text.trim()) {
                if (this.text.trim().match("^\\$id:.*")) {
                    this.localSearch(this.text.trim());
                } else {
                    //TODO
                    console.log("remote search")
                }
            }
        },
        localSearch(link) {
            const matchArray = link.match("(?:^\\$id:)(\\d*)(\\D)?(\\d*)?");
            this.eventHub.emit("select-item", {
                cntId: Number.parseInt(matchArray[1]),
                type: matchArray[2],
                itemId: Number.parseInt(matchArray[3])
            });
        }
    }
}
</script>

<style scoped>

</style>