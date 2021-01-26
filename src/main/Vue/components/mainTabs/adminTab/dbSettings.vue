<template>
    <div>
        <div>
            {{ language.data.db2 }} {{ statusTxt }}
        </div>
        <div v-if="status === 'NEW_DB'">
            <button class="btn btn-sm btn-warning" @click="generate" :disabled="key">{{ language.data.db6 }}</button>
            <br> &#x26A0; {{ language.data.db7 }}
            <textarea class="form-control" readonly rows="3" v-model="key"></textarea>
            <span v-html="language.data.db8"></span>
        </div>
        <div v-if="status === 'NEED_KEY'">
            <textarea class="form-control" rows="3" v-model="key"></textarea>
            <button class="btn btn-sm btn-warning" @click="sendKey" :disabled="!key">{{ language.data.db9 }}</button>
        </div>
    </div>
</template>

<script>
export default {
    name: "dbSettings",
    data() {
        return {
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            status: null,
            key: null
        }
    },
    computed: {
        statusTxt() {
            switch (this.status) {
                case "OK":
                    return this.language.data.db3;
                case "NEED_KEY":
                    return this.language.data.db4;
                case "NEW_DB":
                    return this.language.data.db5;
            }
        }
    },
    methods: {
        async getStatus() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await this.cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/settings/dbStatus",
                    method: "POST",
                    data: encryptedData
                });
                const data = this.cryptoProvider.decrypt(answer);
                this.status = data.message;
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async generate() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await this.cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/settings/keyGen",
                    method: "POST",
                    data: encryptedData
                });
                const data = this.cryptoProvider.decrypt(answer);
                this.key = data.message;
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        },
        async sendKey() {
            this.eventHub.emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await this.cryptoProvider.encrypt({
                    token: token,
                    key: this.key
                });
                const answer = await $.ajax({
                    url: "/settings/setKey",
                    method: "POST",
                    data: encryptedData
                });
                const data = this.cryptoProvider.decrypt(answer);
                if (data.message) {
                    this.eventHub.emit("show-msg", this.language.data[data.message]);
                } else {
                    this.getStatus();
                }
            } catch (e) {
                this.eventHub.emit("show-msg", this.errorParser(e));
            }
        }
    },
    mounted() {
        this.getStatus();
    }
}
</script>

<style scoped>

</style>