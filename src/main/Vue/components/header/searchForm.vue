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
            text: "",
            result: []
        }
    },
    watch: {
        text() {
            this.result = [];
        }
    },
    methods: {
        async doSearch() {
            if (this.text && this.text.trim()) {
                if (this.text.trim().match("^\\$id:.*")) {
                    this.localSearch(this.text.trim());
                } else {
                    if (this.result.length > 0) {
                        this.eventHub.emit("search-result", {result: this.result, text: this.text});
                    } else {
                        this.eventHub.emit("show-msg", "");
                        try {
                            const token = await this.tokenProvider.getToken();
                            const encryptedData = await cryptoProvider.encrypt({
                                token: token,
                                text: this.text
                            });
                            const answer = await $.ajax({
                                url: "/container/search",
                                method: "POST",
                                data: encryptedData
                            });
                            this.result = cryptoProvider.decrypt(answer);
                            this.eventHub.emit("search-result", {result: this.result, text: this.text});
                        } catch (e) {
                            this.eventHub.emit("show-msg", this.errorParser(e));
                        }
                    }
                }
            }
        },
        localSearch(link) {
            const matchArray = link.match("(?:^\\$id:)(\\d*)(\\D)?(\\d*)?");
            this.eventHub.emit("search-event", {
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