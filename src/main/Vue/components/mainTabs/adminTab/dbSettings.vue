<template>
    <div>
        {{ language.data.db2 }}
        <div v-if="status === 'OK'">
            {{ language.data.db3 }}
        </div>
        <div v-if="status === 'NEED_KEY'">
            {{ language.data.db4 }}
        </div>
        <div v-if="status === 'NEW_DB'">
            {{ language.data.db5 }}
        </div>
    </div>
</template>

<script>
export default {
    name: "dbSettings",
    data() {
        return{
            tokenProvider: this.$root.$data.tokenProvider,
            language: this.$root.$data.language,
            status: null
        }
    },
    computed: {
        status() {
            //TODO
        }
    },
    methods: {
        async getStatus() {
            this.$eventHub.$emit("show-msg", "");
            try {
                const token = await this.tokenProvider.getToken();
                const encryptedData = await Vue.cryptoProvider.encrypt({token: token});
                const answer = await $.ajax({
                    url: "/settings/dbStatus",
                    method: "POST",
                    data: encryptedData
                });
                const data = Vue.cryptoProvider.decrypt(answer);
                this.status = data.message;
            } catch (e) {
                this.$eventHub.$emit("show-msg", Vue.errorParser(e));
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