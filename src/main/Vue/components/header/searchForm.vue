<template>
    <span>
        <input type="text" class="input search" :placeholder="placehold" v-model="text" @keypress.enter="doSearch">
        <button class="btn blue icon search" :title="language.data.sf1" @click="doSearch" :disabled="!text">
            <svg class="s-icon" fill="#000000" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 183.792 183.792" xml:space="preserve">
                <path d="M54.734,9.053C39.12,18.067,27.95,32.624,23.284,50.039c-4.667,17.415-2.271,35.606,6.743,51.22c12.023,20.823,34.441,33.759,58.508,33.759c7.599,0,15.139-1.308,22.287-3.818l30.364,52.592l21.65-12.5l-30.359-52.583
	                c10.255-8.774,17.638-20.411,21.207-33.73c4.666-17.415,2.27-35.605-6.744-51.22C134.918,12.936,112.499,0,88.433,0C76.645,0,64.992,3.13,54.734,9.053z M125.29,46.259c5.676,9.831,7.184,21.285,4.246,32.25c-2.938,10.965-9.971,
                    20.13-19.802,25.806c-6.462,3.731-13.793,5.703-21.199,5.703c-15.163,0-29.286-8.146-36.857-21.259c-5.676-9.831-7.184-21.284-4.245-32.25c2.938-10.965,9.971-20.13,19.802-25.807C73.696,26.972,81.027,25,88.433,25C103.597,25,
                    117.719,33.146,125.29,46.259z"/>
            </svg>
        </button>
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