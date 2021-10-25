<template>
    <span style="display: flex">
        <input type="text" class="form-control-sm align-middle" :placeholder="placehold" v-model="text" @keypress.enter="doSearch">
        <button class="btn btn-sm btn-outline-secondary mr-2" :title="language.data.sf1" @click="doSearch" :disabled="!text">&#x1f50d;</button>
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
            result: [],
            isLogTabActive: false
        }
    },
    watch: {
        text(newVal) {
            this.result = [];
            if (newVal.length === 0 && this.isLogTabActive) {
                this.eventHub.emit("log-search", "");
            }
        }
    },
    computed: {
        placehold() {
            if (this.isLogTabActive) {
                return this.language.data.sf4;
            }
            return this.language.data.sf1;
        }
    },
    methods: {
        async doSearch() {
            if (this.text && this.text.trim()) {
                if (this.text.trim().match("^\\$id:.*")) {
                    this.localSearch(this.text.trim());
                } else {
                    if (this.isLogTabActive) {
                        this.eventHub.emit("log-search", this.text);
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
            }
        },
        localSearch(link) {
            const matchArray = link.match("(?:^\\$id:)(\\d*)(\\D)?(\\d*)?");
            this.eventHub.emit("search-event", {
                cntId: Number.parseInt(matchArray[1]),
                type: matchArray[2],
                itemId: Number.parseInt(matchArray[3])
            });
        },
        logTab(isLogTab) {
            this.isLogTabActive = isLogTab;
        }
    },
    created() {
        this.eventHub.on("logTab-active", this.logTab);
    },
    beforeUnmount() {
        this.eventHub.off("logTab-active", this.logTab);
    }
}
</script>

<style scoped>

</style>